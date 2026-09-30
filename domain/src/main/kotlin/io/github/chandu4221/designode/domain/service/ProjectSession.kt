package io.github.chandu4221.designode.domain.service

import io.github.chandu4221.designode.domain.event.ProjectEvent
import io.github.chandu4221.designode.domain.model.*
import io.github.chandu4221.designode.domain.port.ComponentRegistry
import io.github.chandu4221.designode.domain.port.NodeEventPublisher
import io.github.chandu4221.designode.domain.port.NodeIdGenerator
import io.github.chandu4221.designode.domain.port.ProjectEventPublisher

class ProjectSession(
    val projectId: ProjectId,
    private val registry: ComponentRegistry,
    private val validator: SlotValidator,
    private val publisher: NodeEventPublisher,
    private val projectPublisher: ProjectEventPublisher,
    private val idGenerator: NodeIdGenerator,
) {
    private var projectName: String = "Untitled"
    private val trees: MutableMap<ScreenId, NodeTree> = mutableMapOf()
    private val screenNames: MutableMap<ScreenId, String> = mutableMapOf()
    private var screenOrder: MutableList<ScreenId> = mutableListOf()
    private var startScreenId: ScreenId? = null
    private var activeId: ScreenId? = null

    val activeScreenId: ScreenId
        get() = activeId ?: error("No active screen")

    val activeTree: NodeTree
        get() = trees[activeScreenId] ?: error("Tree missing for active screen")

    val name: String get() = projectName

    // ─────────────────────────────────────────────────────
    // Screen lifecycle
    // ─────────────────────────────────────────────────────

    fun addScreen(name: String, root: AtomicNode): Result<ScreenId> {
        val id = ScreenId.generate()
        val tree = NodeTree(registry, validator, publisher, idGenerator, root)
        trees[id] = tree
        screenNames[id] = name
        screenOrder.add(id)
        if (activeId == null) activeId = id
        if (startScreenId == null) startScreenId = id

        projectPublisher.publish(
            ProjectEvent.ScreenAdded(
                screen = Screen(id, name, root),
                index = screenOrder.size - 1,
            )
        )
        return Result.success(id)
    }

    fun removeScreen(id: ScreenId): Result<Unit> {
        if (screenOrder.size <= 1) return failure("Cannot remove last screen")
        val tree = trees[id] ?: return failure("Unknown screen: ${id.value}")
        val name = screenNames[id] ?: ""
        val index = screenOrder.indexOf(id)

        trees.remove(id)
        screenNames.remove(id)
        screenOrder.remove(id)
        if (startScreenId == id) startScreenId = screenOrder.first()
        if (activeId == id) activeId = screenOrder.first()

        projectPublisher.publish(
            ProjectEvent.ScreenRemoved(
                screen = Screen(id, name, tree.root),
                index = index,
            )
        )
        return Result.success(Unit)
    }

    fun renameScreen(id: ScreenId, newName: String): Result<Unit> {
        val old = screenNames[id] ?: return failure("Unknown screen: ${id.value}")
        screenNames[id] = newName
        projectPublisher.publish(ProjectEvent.ScreenRenamed(id, old, newName))
        return Result.success(Unit)
    }

    fun switchScreen(id: ScreenId): Result<Unit> {
        if (id !in trees) return failure("Unknown screen: ${id.value}")
        activeId = id
        return Result.success(Unit)
    }

    fun setStartScreen(id: ScreenId): Result<Unit> {
        if (id !in trees) return failure("Unknown screen: ${id.value}")
        val old = startScreenId
        startScreenId = id
        projectPublisher.publish(ProjectEvent.StartScreenChanged(old, id))
        return Result.success(Unit)
    }

    fun rename(newName: String) {
        projectName = newName
    }

    // ─────────────────────────────────────────────────────
    // Queries
    // ─────────────────────────────────────────────────────

    fun screenIds(): List<ScreenId> = screenOrder.toList()
    fun screenName(id: ScreenId): String? = screenNames[id]
    fun startScreen(): ScreenId? = startScreenId
    fun tree(id: ScreenId): NodeTree? = trees[id]

    // ─────────────────────────────────────────────────────
    // Snapshot
    // ─────────────────────────────────────────────────────

    fun snapshot(): Project = Project(
        id = projectId,
        name = projectName,
        screens = screenOrder.associateWith { id ->
            Screen(
                id = id,
                name = screenNames[id] ?: "",
                root = trees.getValue(id).root,
            )
        },
        startScreenId = startScreenId,
    )

    private fun failure(message: String): Result<Nothing> =
        Result.failure(IllegalArgumentException(message))

    companion object {
        fun fromProject(
            project: Project,
            registry: ComponentRegistry,
            validator: SlotValidator,
            publisher: NodeEventPublisher,
            projectPublisher: ProjectEventPublisher,
            idGenerator: NodeIdGenerator,
        ): ProjectSession {
            val session = ProjectSession(
                projectId = project.id,
                registry = registry,
                validator = validator,
                publisher = publisher,
                projectPublisher = projectPublisher,
                idGenerator = idGenerator,
            )
            session.projectName = project.name
            project.screens.forEach { (id, screen) ->
                session.trees[id] = NodeTree(registry, validator, publisher, idGenerator, screen.root)
                session.screenNames[id] = screen.name
                session.screenOrder.add(id)
            }
            session.startScreenId = project.startScreenId ?: session.screenOrder.firstOrNull()
            session.activeId = session.startScreenId
            return session
        }
    }
}