package io.github.chandu4221.designode.domain.service

import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.LayoutScope
import io.github.chandu4221.designode.domain.model.SlotContent
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.port.ComponentRegistry
import io.github.chandu4221.designode.domain.spec.Cardinality
import io.github.chandu4221.designode.domain.spec.SlotSpec

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

        // 2. Cardinality
        val existing = parent.slots[slotId] ?: SlotContent.Empty
        when (slotSpec.cardinality) {
            Cardinality.ZERO_OR_ONE, Cardinality.EXACTLY_ONE -> {
                if (existing != SlotContent.Empty) {
                    return failure("Slot '${slotSpec.label}' is full")
                }
            }
            Cardinality.ZERO_OR_MANY, Cardinality.ONE_OR_MANY -> Unit
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

    fun missingRequiredSlots(node: AtomicNode): List<SlotSpec> {
        val spec = registry.spec(node.type) ?: return emptyList()
        return spec.slots.filter { slotSpec ->
            slotSpec.required && (node.slots[slotSpec.id] ?: SlotContent.Empty) == SlotContent.Empty
        }
    }

    private fun findSlot(parent: AtomicNode, slotId: SlotId): SlotSpec? =
        registry.spec(parent.type)?.slots?.firstOrNull { it.id == slotId }

    private fun failure(message: String): Result<Unit> =
        Result.failure(IllegalArgumentException(message))
}