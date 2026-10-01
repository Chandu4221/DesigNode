package io.github.chandu4221.designode.domain.service

import io.github.chandu4221.designode.domain.event.NodeEvent
import io.github.chandu4221.designode.domain.model.*
import io.github.chandu4221.designode.domain.port.NodeEventPublisher
import io.github.chandu4221.designode.domain.port.SequentialNodeIdGenerator
import io.github.chandu4221.designode.domain.spec.AtomicLevel
import io.github.chandu4221.designode.domain.spec.ComponentSpec
import io.github.chandu4221.designode.domain.spec.SlotSpec
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NodeTreeTest {

    private val collected = mutableListOf<NodeEvent>()
    private val publisher = NodeEventPublisher { event -> collected += event }

    private val textType = ComponentTypeId("Text")
    private val rowType = ComponentTypeId("Row")

    private val rowSpec = ComponentSpec(
        type = rowType,
        family = FamilyId("Layout"),
        level = AtomicLevel.MOLECULE,
        label = "Row",
        slots = listOf(
            SlotSpec(
                id = SlotId("content"),
                label = "Content",
                cardinality = Cardinality.ZeroOrMany,
                scope = LayoutScope.RowScope,
            )
        ),
    )

    private val textSpec = ComponentSpec(
        type = textType,
        family = FamilyId("Basic"),
        level = AtomicLevel.ATOM,
        label = "Text",
    )

    private val registry = InMemoryComponentRegistry(listOf(rowSpec, textSpec))
    private val validator = SlotValidator(registry)

    private fun newTree(): NodeTree {
        val root = AtomicNode(id = NodeId("root"), type = rowType)
        return NodeTree(
            registry = registry,
            validator = validator,
            publisher = publisher,
            idGenerator = SequentialNodeIdGenerator(),
            root = root,
        )
    }

    @BeforeTest
    fun setup() {
        collected.clear()
    }

    @Test
    fun `insert adds a child and emits event`() {
        val tree = newTree()
        val result = tree.insert(tree.root.id, SlotId("content"), textType)
        assertTrue(result.isSuccess)
        assertEquals(1, tree.root.slots[SlotId("content")]?.nodes()?.size)
        assertTrue(collected.any { it is NodeEvent.NodeInserted })
    }

    @Test
    fun `remove deletes the child`() {
        val tree = newTree()
        val childId = tree.insert(tree.root.id, SlotId("content"), textType).getOrThrow()
        tree.remove(childId)
        assertTrue(tree.root.slots[SlotId("content")]?.nodes().isNullOrEmpty())
        assertTrue(collected.any { it is NodeEvent.NodeRemoved })
    }

    @Test
    fun `cannot remove root`() {
        val tree = newTree()
        assertTrue(tree.remove(tree.root.id).isFailure)
    }

    @Test
    fun `move reparents a node`() {
        val tree = newTree()
        val a = tree.insert(tree.root.id, SlotId("content"), rowType).getOrThrow()
        val b = tree.insert(tree.root.id, SlotId("content"), textType).getOrThrow()

        tree.move(b, a, SlotId("content"))

        assertEquals(1, tree.root.slots[SlotId("content")]?.nodes()?.size)
        val aNode = tree.find(a)!!
        assertEquals(1, aNode.slots[SlotId("content")]?.nodes()?.size)
        assertTrue(collected.any { it is NodeEvent.NodeMoved })
    }

    @Test
    fun `updateProperty changes value and emits event`() {
        val tree = newTree()
        val id = tree.insert(tree.root.id, SlotId("content"), textType).getOrThrow()
        tree.updateProperty(id, PropertyKey("text"), Value.Text("hello"))
        assertEquals(Value.Text("hello"), tree.find(id)!!.properties[PropertyKey("text")])
        assertTrue(collected.any { it is NodeEvent.PropertyChanged })

        // No event emitted when value is unchanged
        val countBefore = collected.size
        tree.updateProperty(id, PropertyKey("text"), Value.Text("hello"))
        assertEquals(countBefore, collected.size)
    }

    @Test
    fun `insertSubtree restores node with existing id and children`() {
        val tree = newTree()
        val child = AtomicNode(
            id = NodeId("existing-child"),
            type = textType,
            properties = mapOf(PropertyKey("text") to Value.Text("preserved")),
        )
        val subtree = AtomicNode(
            id = NodeId("existing-parent"),
            type = rowType,
            slots = mapOf(SlotId("content") to SlotContent.One(child)),
        )

        val result = tree.insertSubtree(tree.root.id, SlotId("content"), subtree)
        assertTrue(result.isSuccess)

        val foundParent = tree.find(NodeId("existing-parent"))
        assertEquals(NodeId("existing-parent"), foundParent?.id)
        val foundChild = tree.find(NodeId("existing-child"))
        assertEquals(Value.Text("preserved"), foundChild?.properties?.get(PropertyKey("text")))
    }

    @Test
    fun `deepCopyWithNewIds generates new unique ids for subtree`() {
        val child = AtomicNode(id = NodeId("old-child"), type = textType)
        val parent = AtomicNode(
            id = NodeId("old-parent"),
            type = rowType,
            slots = mapOf(SlotId("content") to SlotContent.One(child)),
        )

        val idGen = SequentialNodeIdGenerator()
        val cloned = parent.deepCopyWithNewIds(idGen)

        assertEquals(NodeId("test-0"), cloned.id)
        val clonedChild = cloned.slots[SlotId("content")]?.nodes()?.first()!!
        assertEquals(NodeId("test-1"), clonedChild.id)
        assertEquals(textType, clonedChild.type)
    }
}