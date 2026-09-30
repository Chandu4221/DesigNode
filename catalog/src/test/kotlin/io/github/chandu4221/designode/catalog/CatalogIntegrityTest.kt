package io.github.chandu4221.designode.catalog

import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.model.VariantId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Verifies that the catalog is internally consistent.
 *
 * These are not tests of the domain. They're tests of the catalog data —
 * checking for cross-reference mistakes that are easy to make and hard to
 * catch until runtime.
 */
class CatalogIntegrityTest {

    private val specs = Material3Catalog.specs
    private val registeredTypes: Set<ComponentTypeId> = specs.map { it.type }.toSet()

    @Test
    fun `no duplicate component type ids`() {
        val typeIds = specs.map { it.type }
        assertEquals(typeIds.size, typeIds.toSet().size, "Duplicate ComponentTypeId in catalog")
    }

    @Test
    fun `no duplicate slot ids within a spec`() {
        specs.forEach { spec ->
            val slotIds = spec.slots.map { it.id }
            assertEquals(
                slotIds.size,
                slotIds.toSet().size,
                "Duplicate SlotId in '${spec.type.value}': $slotIds",
            )
        }
    }

    @Test
    fun `no duplicate property keys within a spec`() {
        specs.forEach { spec ->
            val keys = spec.properties.map { it.key }
            assertEquals(
                keys.size,
                keys.toSet().size,
                "Duplicate PropertyKey in '${spec.type.value}': $keys",
            )
        }
    }

    @Test
    fun `no duplicate variant ids within a spec`() {
        specs.forEach { spec ->
            val variantIds = spec.variants.map { it.id }
            assertEquals(
                variantIds.size,
                variantIds.toSet().size,
                "Duplicate VariantId in '${spec.type.value}': $variantIds",
            )
        }
    }

    @Test
    fun `every slot accepts only registered component types`() {
        specs.forEach { spec ->
            spec.slots.forEach { slot ->
                slot.accepts.forEach { acceptedType ->
                    assertTrue(
                        acceptedType in registeredTypes,
                        "Spec '${spec.type.value}' slot '${slot.id.value}' accepts " +
                                "'${acceptedType.value}', which is not registered in the catalog",
                    )
                }
            }
        }
    }

    @Test
    fun `exactly one default slot per spec`() {
        specs.forEach { spec ->
            val defaults = spec.slots.count { it.isDefault }
            assertTrue(
                defaults <= 1,
                "Spec '${spec.type.value}' has $defaults default slots; at most one allowed",
            )
        }
    }
}