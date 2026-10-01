package io.github.chandu4221.designode.ui.undo

import io.github.chandu4221.designode.domain.model.ScreenId

/**
 * Encapsulates an undoable and redoable user mutation.
 */
data class UndoAction(
    val screenId: ScreenId,
    val undo: () -> Unit,
    val redo: () -> Unit,
)

/**
 * Manages an in-memory stack of inverse operations with a bounded history.
 */
class UndoManager(
    private val maxHistory: Int = 100,
) {
    private val undoStack = ArrayDeque<UndoAction>()
    private val redoStack = ArrayDeque<UndoAction>()

    var isPerformingUndoRedo: Boolean = false
        private set

    val canUndo: Boolean get() = undoStack.isNotEmpty()
    val canRedo: Boolean get() = redoStack.isNotEmpty()

    /**
     * Records a new action and clears the redo stack.
     * Ignored if an undo or redo is currently in progress.
     */
    fun record(action: UndoAction) {
        if (isPerformingUndoRedo) return
        undoStack.addLast(action)
        if (undoStack.size > maxHistory) {
            undoStack.removeFirst()
        }
        redoStack.clear()
    }

    /**
     * Executes the most recent undo action.
     * Returns the executed [UndoAction] or null if the undo stack is empty.
     */
    fun undo(): UndoAction? {
        if (undoStack.isEmpty()) return null
        val action = undoStack.removeLast()
        isPerformingUndoRedo = true
        try {
            action.undo()
            redoStack.addLast(action)
        } finally {
            isPerformingUndoRedo = false
        }
        return action
    }

    /**
     * Executes the most recent redo action.
     * Returns the executed [UndoAction] or null if the redo stack is empty.
     */
    fun redo(): UndoAction? {
        if (redoStack.isEmpty()) return null
        val action = redoStack.removeLast()
        isPerformingUndoRedo = true
        try {
            action.redo()
            undoStack.addLast(action)
        } finally {
            isPerformingUndoRedo = false
        }
        return action
    }

    /**
     * Clears all recorded undo and redo history.
     */
    fun clear() {
        undoStack.clear()
        redoStack.clear()
    }
}
