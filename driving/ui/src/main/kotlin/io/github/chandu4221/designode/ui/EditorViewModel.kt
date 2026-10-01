package io.github.chandu4221.designode.ui

import androidx.compose.ui.geometry.Offset
import io.github.chandu4221.designode.catalog.Material3Catalog
import io.github.chandu4221.designode.codegen.ComposeSourceGenerator
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.NodeId
import io.github.chandu4221.designode.domain.model.ProjectId
import io.github.chandu4221.designode.domain.model.PropertyKey
import io.github.chandu4221.designode.domain.model.ScreenId
import io.github.chandu4221.designode.domain.model.SlotContent
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.model.ThemeSpec
import io.github.chandu4221.designode.domain.model.Value
import io.github.chandu4221.designode.domain.model.VariantId
import io.github.chandu4221.designode.domain.port.NodeEventPublisher
import io.github.chandu4221.designode.domain.port.ProjectEventPublisher
import io.github.chandu4221.designode.domain.port.ProjectRepository
import io.github.chandu4221.designode.domain.port.ProjectSummary
import io.github.chandu4221.designode.domain.port.RandomNodeIdGenerator
import io.github.chandu4221.designode.domain.service.NodeTree
import io.github.chandu4221.designode.domain.service.ProjectSession
import io.github.chandu4221.designode.domain.service.SlotValidator
import io.github.chandu4221.designode.domain.spec.ComponentSpec
import io.github.chandu4221.designode.ui.canvas.HitTestRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditorViewModel(
    private val repository: ProjectRepository,
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val registry = Material3Catalog.registry()
    private val validator = SlotValidator(registry)
    private val publisher = NodeEventPublisher { /* no subscribers yet */ }
    private val projectPublisher = ProjectEventPublisher { /* no subscribers yet */ }
    private val idGenerator = RandomNodeIdGenerator
    private val sourceGenerator = ComposeSourceGenerator()

    val hitTestRegistry = HitTestRegistry()
    val specs: List<ComponentSpec> = registry.all()

    fun spec(type: ComponentTypeId): ComponentSpec? = registry.spec(type)

    private var session: ProjectSession = createInitialSession()
    private val currentTree: NodeTree get() = session.activeTree

    private val _state = MutableStateFlow(
        EditorState(
            root = session.activeTree.root,
            projectName = session.name,
            screens = computeScreenTabs(),
            activeScreenId = session.activeScreenId,
            theme = session.theme,
        )
    )
    val state: StateFlow<EditorState> = _state.asStateFlow()

    private val _projectList = MutableStateFlow<List<ProjectSummary>>(emptyList())
    val projectList: StateFlow<List<ProjectSummary>> = _projectList.asStateFlow()

    private fun createInitialSession(): ProjectSession {
        val s = ProjectSession(
            projectId = ProjectId.generate(),
            registry = registry,
            validator = validator,
            publisher = publisher,
            projectPublisher = projectPublisher,
            idGenerator = idGenerator,
        )
        s.addScreen("Main", defaultScreen())
        return s
    }

    private fun computeScreenTabs(): List<ScreenTab> {
        val startId = session.startScreen()
        return session.screenIds().map { id ->
            ScreenTab(
                id = id,
                name = session.screenName(id) ?: "Screen",
                isStart = id == startId,
            )
        }
    }

    // ─────────────────────────────────────────────────────
    // Selection
    // ─────────────────────────────────────────────────────

    fun selectNode(id: NodeId?) {
        _state.update { it.copy(selectedId = id) }
    }

    fun hoverNode(id: NodeId?) {
        _state.update { it.copy(hoveredId = id) }
    }

    // ─────────────────────────────────────────────────────
    // Edit intents
    // ─────────────────────────────────────────────────────

    fun updateVariant(id: NodeId, variant: VariantId?) {
        currentTree.updateVariant(id, variant)
        syncRoot()
        markDirty()
    }

    fun updateProperty(id: NodeId, key: PropertyKey, value: Value?) {
        currentTree.updateProperty(id, key, value)
        syncRoot()
        markDirty()
    }

    fun removeNode(id: NodeId) {
        currentTree.remove(id).onSuccess {
            _state.update { it.copy(selectedId = null) }
            syncRoot()
            markDirty()
        }
    }

    // ─────────────────────────────────────────────────────
    // Drag-and-drop
    // ─────────────────────────────────────────────────────

    fun beginDrag(type: ComponentTypeId) {
        _state.update { it.copy(dragType = type, dropTarget = null) }
    }

    fun updateDragPosition(positionInRoot: Offset) {
        val current = _state.value
        val draggedType = current.dragType ?: return
        val target = computeValidDropTarget(positionInRoot, draggedType)
        _state.update { it.copy(dropTarget = target) }
    }

    fun commitDrag() {
        val current = _state.value
        val draggedType = current.dragType
        val target = current.dropTarget
        _state.update { it.copy(dragType = null, dropTarget = null) }

        if (draggedType == null || target == null) return

        currentTree.insert(target.nodeId, target.slotId, draggedType).onSuccess { newId ->
            _state.update { it.copy(selectedId = newId) }
            syncRoot()
            markDirty()
        }
    }

    fun cancelDrag() {
        _state.update { it.copy(dragType = null, dropTarget = null) }
    }

    // ─────────────────────────────────────────────────────
    // Screen Management
    // ─────────────────────────────────────────────────────

    fun addScreen(name: String? = null) {
        val nextNum = session.screenIds().size + 1
        val screenName = name ?: "Screen $nextNum"
        val newRoot = defaultScreen()
        session.addScreen(screenName, newRoot).onSuccess { newId ->
            session.switchScreen(newId)
            _state.update {
                it.copy(
                    root = session.activeTree.root,
                    activeScreenId = session.activeScreenId,
                    screens = computeScreenTabs(),
                    selectedId = null,
                    hoveredId = null,
                    dropTarget = null,
                )
            }
            markDirty()
        }
    }

    fun switchScreen(id: ScreenId) {
        session.switchScreen(id).onSuccess {
            _state.update {
                it.copy(
                    root = session.activeTree.root,
                    activeScreenId = session.activeScreenId,
                    screens = computeScreenTabs(),
                    selectedId = null,
                    hoveredId = null,
                    dropTarget = null,
                )
            }
        }
    }

    fun renameScreen(id: ScreenId, newName: String) {
        val trimmed = newName.trim()
        if (trimmed.isEmpty()) return
        session.renameScreen(id, trimmed).onSuccess {
            syncRoot()
            markDirty()
        }
    }

    fun removeScreen(id: ScreenId) {
        session.removeScreen(id).onSuccess {
            _state.update {
                it.copy(
                    root = session.activeTree.root,
                    activeScreenId = session.activeScreenId,
                    screens = computeScreenTabs(),
                    selectedId = null,
                    hoveredId = null,
                    dropTarget = null,
                )
            }
            markDirty()
        }
    }

    fun setStartScreen(id: ScreenId) {
        session.setStartScreen(id).onSuccess {
            syncRoot()
            markDirty()
        }
    }

    // ─────────────────────────────────────────────────────
    // Project lifecycle
    // ─────────────────────────────────────────────────────

    fun newProject() {
        session = createInitialSession()
        _state.update {
            it.copy(
                root = session.activeTree.root,
                projectName = session.name,
                screens = computeScreenTabs(),
                activeScreenId = session.activeScreenId,
                selectedId = null,
                hoveredId = null,
                theme = session.theme,
                saveStatus = SaveStatus.Idle,
            )
        }
    }

    fun save() {
        session.theme = _state.value.theme
        val project = session.snapshot()
        scope.launch {
            _state.update { it.copy(saveStatus = SaveStatus.Saving) }
            repository.save(project)
                .onSuccess {
                    _state.update { state -> state.copy(saveStatus = SaveStatus.Saved) }
                }
                .onFailure { throwable ->
                    val message = throwable.message ?: "Save failed"
                    _state.update { state -> state.copy(saveStatus = SaveStatus.Failed(message)) }
                }
        }
    }

    fun openProjectPicker() {
        _state.update { it.copy(projectPickerOpen = true) }
        scope.launch {
            repository.list()
                .onSuccess { list -> _projectList.value = list }
                .onFailure { _projectList.value = emptyList() }
        }
    }

    fun closeProjectPicker() {
        _state.update { it.copy(projectPickerOpen = false) }
    }

    fun loadProject(id: ProjectId) {
        scope.launch {
            repository.load(id)
                .onSuccess { project ->
                    session = ProjectSession.fromProject(
                        project = project,
                        registry = registry,
                        validator = validator,
                        publisher = publisher,
                        projectPublisher = projectPublisher,
                        idGenerator = idGenerator,
                    )
                    _state.update { state ->
                        state.copy(
                            root = session.activeTree.root,
                            projectName = session.name,
                            screens = computeScreenTabs(),
                            activeScreenId = session.activeScreenId,
                            selectedId = null,
                            hoveredId = null,
                            theme = session.theme,
                            projectPickerOpen = false,
                            saveStatus = SaveStatus.Idle,
                        )
                    }
                }
        }
    }

    // ─────────────────────────────────────────────────────
    // Theme
    // ─────────────────────────────────────────────────────

    fun openThemeDialog() {
        _state.update { it.copy(themeDialogOpen = true) }
    }

    fun closeThemeDialog() {
        _state.update { it.copy(themeDialogOpen = false) }
    }

    fun updateTheme(theme: ThemeSpec) {
        session.theme = theme
        _state.update { it.copy(theme = theme) }
        markDirty()
    }

    fun updateThemeSeedColor(seedColor: Long) {
        val updated = session.theme.copy(seedColor = seedColor)
        session.theme = updated
        _state.update { it.copy(theme = updated) }
        markDirty()
    }

    fun toggleThemeDarkMode() {
        val updated = session.theme.copy(isDark = !session.theme.isDark)
        session.theme = updated
        _state.update { it.copy(theme = updated) }
        markDirty()
    }

    fun updateThemeContrast(contrast: Double) {
        val updated = session.theme.copy(contrastLevel = contrast)
        session.theme = updated
        _state.update { it.copy(theme = updated) }
        markDirty()
    }

    fun updateThemeStyle(style: String) {
        val updated = session.theme.copy(style = style)
        session.theme = updated
        _state.update { it.copy(theme = updated) }
        markDirty()
    }

    // ─────────────────────────────────────────────────────
    // Export
    // ─────────────────────────────────────────────────────

    fun openExportDialog() {
        _state.update { it.copy(exportDialogOpen = true) }
    }

    fun closeExportDialog() {
        _state.update { it.copy(exportDialogOpen = false) }
    }

    fun generateCode(): String = sourceGenerator.generate(session.snapshot())

    // ─────────────────────────────────────────────────────
    // Internals
    // ─────────────────────────────────────────────────────

    private fun markDirty() {
        _state.update { state ->
            if (state.saveStatus is SaveStatus.Saved) {
                state.copy(saveStatus = SaveStatus.Idle)
            } else {
                state
            }
        }
    }

    private fun computeValidDropTarget(
        positionInRoot: Offset,
        draggedType: ComponentTypeId,
    ): DropTarget? {
        val fakeChild = AtomicNode(
            id = NodeId("__drag-preview__"),
            type = draggedType,
        )

        // 1. Priority: Direct hit on a dedicated slot drop zone (e.g. Scaffold topBar/bottomBar/FAB placeholder)
        val slotHit = hitTestRegistry.hitTestSlot(positionInRoot.x, positionInRoot.y)
        if (slotHit != null) {
            val node = currentTree.find(slotHit.nodeId)
            if (node != null && validator.canDrop(node, slotHit.slotId, fakeChild).isSuccess) {
                return DropTarget(node.id, slotHit.slotId)
            }
        }

        // 2. Priority: Container hit on the canvas
        val hitId = hitTestRegistry.hitTest(positionInRoot.x, positionInRoot.y) ?: return null

        val dedicatedTarget = findDedicatedAncestorSlot(hitId, draggedType, fakeChild)
        if (dedicatedTarget != null) {
            return dedicatedTarget
        }

        // 3. Fallback: Standard container ancestor resolution (e.g. Column.children)
        val candidate = findContainerAncestor(hitId) ?: return null
        val node = currentTree.find(candidate) ?: return null
        val spec = registry.spec(node.type) ?: return null
        val slotId = resolveDropSlot(spec, draggedType) ?: return null

        return if (validator.canDrop(node, slotId, fakeChild).isSuccess) {
            DropTarget(node.id, slotId)
        } else {
            null
        }
    }

    private fun findDedicatedAncestorSlot(
        startId: NodeId,
        draggedType: ComponentTypeId,
        fakeChild: AtomicNode,
    ): DropTarget? {
        var current: AtomicNode? = currentTree.find(startId)
        while (current != null) {
            val spec = registry.spec(current.type)
            if (spec != null) {
                val matchingSlot = spec.slots.firstOrNull { draggedType in it.accepts }
                if (matchingSlot != null && validator.canDrop(current, matchingSlot.id, fakeChild).isSuccess) {
                    return DropTarget(current.id, matchingSlot.id)
                }
            }
            current = currentTree.parentOf(current.id)
        }
        return null
    }

    private fun findContainerAncestor(startId: NodeId): NodeId? {
        var current = currentTree.find(startId)
        while (current != null) {
            val spec = registry.spec(current.type)
            if (spec != null && spec.slots.isNotEmpty()) return current.id
            current = currentTree.parentOf(current.id)
        }
        return null
    }

    private fun resolveDropSlot(
        targetSpec: ComponentSpec,
        draggedType: ComponentTypeId,
    ): SlotId? {
        val explicit = targetSpec.slots.filter { draggedType in it.accepts }
        if (explicit.isNotEmpty()) {
            val preferred = explicit.firstOrNull { it.isDefault } ?: explicit.first()
            return preferred.id
        }
        val default = targetSpec.slots.firstOrNull { it.isDefault }
        if (default != null) return default.id
        return targetSpec.slots.singleOrNull()?.id
    }

    private fun syncRoot() {
        _state.update {
            it.copy(
                root = session.activeTree.root,
                projectName = session.name,
                screens = computeScreenTabs(),
                activeScreenId = session.activeScreenId,
                theme = session.theme,
            )
        }
    }

    private fun defaultScreen(): AtomicNode {
        val contentColumn = AtomicNode(
            id = idGenerator.next(),
            type = ComponentTypeId("Column"),
        )
        return AtomicNode(
            id = idGenerator.next(),
            type = ComponentTypeId("Scaffold"),
            slots = mapOf(
                SlotId("content") to SlotContent.One(contentColumn),
            ),
        )
    }
}