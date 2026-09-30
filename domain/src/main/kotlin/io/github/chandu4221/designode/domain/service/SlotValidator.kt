package io.github.chandu4221.designode.domain.service

import io.github.chandu4221.designode.domain.model.*
import io.github.chandu4221.designode.domain.port.ComponentRegistry
import io.github.chandu4221.designode.domain.spec.SlotSpec

/**
 * Pure validator for slot-level rules.
 *
 * Answers two different questions:
 *  - [canDrop]    : can one more child be added to this slot right now?
 *  - [incompleteSlots] : which slots on this node are not yet satisfied?
 *
 * The first fires during editing. The second fires on demand — for example
 * before exporting code, to refuse a tree that has empty required slots.
 */
class SlotValidator(
    private val registry: ComponentRegistry,
) {

    fun canDrop(
        parent: AtomicNode,
        slotId: SlotId,
        child: AtomicNode,
    ): Result<Unit> {
        val slotSpec = findSlot(parent, slotId)
            ?: return failure("Slot '${slotId.value}' does not exist on ${parent.type.value}")

        // 1. Type acceptance
        if (slotSpec.accepts.isNotEmpty() && child.type !in slotSpec.accepts) {
            return failure(
                "Component '${child.type.value}' is not allowed in slot '${slotSpec.label}'"
            )
        }

        // 2. Cardinality — would adding one more violate the upper bound?
        val existing = parent.slots[slotId] ?: SlotContent.Empty
        val currentCount = existing.nodes().size
        when (val c = slotSpec.cardinality) {
            Cardinality.ZeroOrOne, Cardinality.ExactlyOne -> {
                if (currentCount >= 1) {
                    return failure("Slot '${slotSpec.label}' is full")
                }
            }

            Cardinality.ZeroOrMany, Cardinality.OneOrMany -> Unit
            is Cardinality.Range -> {
                if (currentCount >= c.max) {
                    return failure("Slot '${slotSpec.label}' allows at most ${c.max} children")
                }
            }
        }

        // 3. Modifier scope
        child.modifiers.forEach { modifier ->
            if (slotSpec.scope !in modifier.allowedScopes &&
                LayoutScope.Unscoped !in modifier.allowedScopes
            ) {
                return failure(
                    "Modifier '${modifier::class.simpleName}' is not valid in ${slotSpec.scope}"
                )
            }
        }

        return Result.success(Unit)
    }

    /**
     * Returns every slot on [node] whose cardinality is not yet satisfied.
     *
     * - EXACTLY_ONE  : must have exactly 1 child
     * - ONE_OR_MANY  : must have at least 1 child
     * - RANGE(min,max): must have at least `min` children
     * - ZERO_OR_ONE, ZERO_OR_MANY: always satisfied (no lower bound)
     */
    fun incompleteSlots(node: AtomicNode): List<SlotSpec> {
        val spec = registry.spec(node.type) ?: return emptyList()
        return spec.slots.filter { slotSpec ->
            val count = (node.slots[slotSpec.id] ?: SlotContent.Empty).nodes().size
            when (val c = slotSpec.cardinality) {
                Cardinality.ZeroOrOne -> false
                Cardinality.ExactlyOne -> count != 1
                Cardinality.ZeroOrMany -> false
                Cardinality.OneOrMany -> count < 1
                is Cardinality.Range -> count < c.min
            }
        }
    }

    private fun findSlot(parent: AtomicNode, slotId: SlotId): SlotSpec? =
        registry.spec(parent.type)?.slots?.firstOrNull { it.id == slotId }

    private fun failure(message: String): Result<Unit> =
        Result.failure(IllegalArgumentException(message))
}