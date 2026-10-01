package io.github.chandu4221.designode.ui

import androidx.compose.ui.geometry.Offset
import io.github.chandu4221.designode.catalog.Material3Catalog
import io.github.chandu4221.designode.codegen.ComposeSourceGenerator
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.ModifierToken
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
import io.github.chandu4221.designode.domain.event.NodeEvent
import io.github.chandu4221.designode.domain.model.deepCopyWithNewIds
import io.github.chandu4221.designode.domain.model.nodes
import io.github.chandu4221.designode.ui.undo.UndoAction
import io.github.chandu4221.designode.ui.undo.UndoManager
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
    val undoManager = UndoManager()
    private var clipboardNode: AtomicNode? = null

    private val publisher = NodeEventPublisher { event ->
        if (undoManager.isPerformingUndoRedo) return@NodeEventPublisher
        val activeScreen = session.activeScreenId
        val tree = session.tree(activeScreen) ?: return@NodeEventPublisher

        val action = when (event) {
            is NodeEvent.NodeInserted -> UndoAction(
                screenId = activeScreen,
                undo = {
                    session.tree(activeScreen)?.remove(event.node.id)
                    if (_state.value.selectedId == event.node.id) {
                        _state.update { it.copy(selectedId = null) }
                    }
                },
                redo = {
                    session.tree(activeScreen)?.insertSubtree(event.parentId, event.slotId, event.node, event.index)
                    _state.update { it.copy(selectedId = event.node.id) }
                },
            )
            is NodeEvent.NodeRemoved -> UndoAction(
                screenId = activeScreen,
                undo = {
                    session.tree(activeScreen)?.insertSubtree(event.parentId, event.slotId, event.node, event.index)
                    _state.update { it.copy(selectedId = event.node.id) }
                },
                redo = {
                    session.tree(activeScreen)?.remove(event.node.id)
                    if (_state.value.selectedId == event.node.id) {
                        _state.update { it.copy(selectedId = null) }
                    }
                },
            )
            is NodeEvent.NodeMoved -> UndoAction(
                screenId = activeScreen,
                undo = {
                    session.tree(activeScreen)?.move(event.nodeId, event.fromParentId, event.fromSlotId, event.fromIndex)
                    _state.update { it.copy(selectedId = event.nodeId) }
                },
                redo = {
                    session.tree(activeScreen)?.move(event.nodeId, event.toParentId, event.toSlotId, event.toIndex)
                    _state.update { it.copy(selectedId = event.nodeId) }
                },
            )
            is NodeEvent.PropertyChanged -> UndoAction(
                screenId = activeScreen,
                undo = {
                    session.tree(activeScreen)?.updateProperty(event.nodeId, event.key, event.oldValue)
                    _state.update { it.copy(selectedId = event.nodeId) }
                },
                redo = {
                    session.tree(activeScreen)?.updateProperty(event.nodeId, event.key, event.newValue)
                    _state.update { it.copy(selectedId = event.nodeId) }
                },
            )
            is NodeEvent.VariantChanged -> UndoAction(
                screenId = activeScreen,
                undo = {
                    session.tree(activeScreen)?.updateVariant(event.nodeId, event.oldVariant)
                    _state.update { it.copy(selectedId = event.nodeId) }
                },
                redo = {
                    session.tree(activeScreen)?.updateVariant(event.nodeId, event.newVariant)
                    _state.update { it.copy(selectedId = event.nodeId) }
                },
            )
            is NodeEvent.ModifiersChanged -> UndoAction(
                screenId = activeScreen,
                undo = {
                    session.tree(activeScreen)?.updateModifiers(event.nodeId, event.oldModifiers)
                    _state.update { it.copy(selectedId = event.nodeId) }
                },
                redo = {
                    session.tree(activeScreen)?.updateModifiers(event.nodeId, event.newModifiers)
                    _state.update { it.copy(selectedId = event.nodeId) }
                },
            )
        }
        undoManager.record(action)
        updateUndoRedoState()
    }
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

    fun updateModifiers(id: NodeId, modifiers: List<ModifierToken>) {
        currentTree.updateModifiers(id, modifiers)
        syncRoot()
        markDirty()
    }

    fun undo() {
        val action = undoManager.undo() ?: return
        if (session.activeScreenId != action.screenId) {
            session.switchScreen(action.screenId)
        }
        syncRoot()
        markDirty()
        updateUndoRedoState()
    }

    fun redo() {
        val action = undoManager.redo() ?: return
        if (session.activeScreenId != action.screenId) {
            session.switchScreen(action.screenId)
        }
        syncRoot()
        markDirty()
        updateUndoRedoState()
    }

    fun copySelected() {
        val selectedId = _state.value.selectedId ?: return
        if (selectedId == currentTree.root.id) return
        val node = currentTree.find(selectedId) ?: return
        clipboardNode = node
        updateUndoRedoState()
    }

    fun duplicateSelected() {
        val selectedId = _state.value.selectedId ?: return
        if (selectedId == currentTree.root.id) return
        val node = currentTree.find(selectedId) ?: return
        val parent = currentTree.parentOf(selectedId) ?: return
        val slotId = currentTree.slotOf(parent.id, selectedId) ?: return

        val siblings = parent.slots[slotId]?.nodes().orEmpty()
        val currentIndex = siblings.indexOfFirst { it.id == selectedId }
        val insertIndex = if (currentIndex >= 0) currentIndex + 1 else null

        val cloned = node.deepCopyWithNewIds(idGenerator)
        currentTree.insertSubtree(parent.id, slotId, cloned, insertIndex).onSuccess {
            _state.update { it.copy(selectedId = cloned.id) }
            syncRoot()
            markDirty()
        }
    }

    fun paste() {
        val clip = clipboardNode ?: return
        val cloned = clip.deepCopyWithNewIds(idGenerator)
        val selectedId = _state.value.selectedId

        var targetParentId: NodeId? = null
        var targetSlotId: SlotId? = null
        var insertIndex: Int? = null

        if (selectedId != null) {
            val selectedNode = currentTree.find(selectedId)
            if (selectedNode != null) {
                val spec = registry.spec(selectedNode.type)
                val directSlot = spec?.slots?.firstOrNull { clip.type in it.accepts }
                    ?: spec?.slots?.firstOrNull { it.isDefault }
                    ?: spec?.slots?.singleOrNull()

                if (directSlot != null && validator.canDrop(selectedNode, directSlot.id, cloned).isSuccess) {
                    targetParentId = selectedNode.id
                    targetSlotId = directSlot.id
                } else {
                    val parent = currentTree.parentOf(selectedId)
                    val slot = if (parent != null) currentTree.slotOf(parent.id, selectedId) else null
                    if (parent != null && slot != null && validator.canDrop(parent, slot, cloned).isSuccess) {
                        targetParentId = parent.id
                        targetSlotId = slot
                        val siblings = parent.slots[slot]?.nodes().orEmpty()
                        val idx = siblings.indexOfFirst { it.id == selectedId }
                        insertIndex = if (idx >= 0) idx + 1 else null
                    }
                }
            }
        }

        // Fallback: paste into root Scaffold's content container
        if (targetParentId == null || targetSlotId == null) {
            val rootScaffold = currentTree.root
            val contentNode = rootScaffold.slots[SlotId("content")]?.nodes()?.firstOrNull()
            if (contentNode != null) {
                val spec = registry.spec(contentNode.type)
                val slot = spec?.slots?.firstOrNull { it.isDefault }?.id ?: SlotId("children")
                if (validator.canDrop(contentNode, slot, cloned).isSuccess) {
                    targetParentId = contentNode.id
                    targetSlotId = slot
                }
            }
        }

        if (targetParentId != null && targetSlotId != null) {
            currentTree.insertSubtree(targetParentId, targetSlotId, cloned, insertIndex).onSuccess {
                _state.update { it.copy(selectedId = cloned.id) }
                syncRoot()
                markDirty()
            }
        }
    }

    fun deselect() {
        selectNode(null)
    }

    // ─────────────────────────────────────────────────────
    // Drag-and-drop
    // ─────────────────────────────────────────────────────

    fun beginDrag(type: ComponentTypeId) {
        _state.update { it.copy(dragType = type, dragPosition = null, dropTarget = null) }
    }

    fun updateDragPosition(positionInRoot: Offset) {
        val current = _state.value
        val draggedType = current.dragType ?: return
        val target = computeValidDropTarget(positionInRoot, draggedType)
        _state.update { it.copy(dragPosition = positionInRoot, dropTarget = target) }
    }

    fun commitDrag() {
        val current = _state.value
        val draggedType = current.dragType
        val target = current.dropTarget
        _state.update { it.copy(dragType = null, dragPosition = null, dropTarget = null) }

        if (draggedType == null || target == null) return

        currentTree.insert(target.nodeId, target.slotId, draggedType).onSuccess { newId ->
            _state.update { it.copy(selectedId = newId) }
            syncRoot()
            markDirty()
        }
    }

    fun cancelDrag() {
        _state.update { it.copy(dragType = null, dragPosition = null, dropTarget = null) }
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
        undoManager.clear()
        clipboardNode = null
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
                canUndo = false,
                canRedo = false,
                hasClipboard = false,
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
                    undoManager.clear()
                    clipboardNode = null
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
                            canUndo = false,
                            canRedo = false,
                            hasClipboard = false,
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

        if (validator.canDrop(node, slotId, fakeChild).isSuccess) {
            return DropTarget(node.id, slotId)
        }

        // If dropping into candidate container failed (e.g. Scaffold.content has Cardinality.ExactlyOne and is full),
        // check if that slot's child is an open container that can accept the dragged component.
        val slotChildren = node.slots[slotId]?.nodes().orEmpty()
        for (child in slotChildren) {
            val childSpec = registry.spec(child.type) ?: continue
            val childSlotId = resolveDropSlot(childSpec, draggedType) ?: continue
            if (validator.canDrop(child, childSlotId, fakeChild).isSuccess) {
                return DropTarget(child.id, childSlotId)
            }
        }

        return null
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

    private fun updateUndoRedoState() {
        _state.update {
            it.copy(
                canUndo = undoManager.canUndo,
                canRedo = undoManager.canRedo,
                hasClipboard = clipboardNode != null,
            )
        }
    }

    private fun syncRoot() {
        _state.update {
            it.copy(
                root = session.activeTree.root,
                projectName = session.name,
                screens = computeScreenTabs(),
                activeScreenId = session.activeScreenId,
                theme = session.theme,
                canUndo = undoManager.canUndo,
                canRedo = undoManager.canRedo,
                hasClipboard = clipboardNode != null,
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