package bilboka.messagebot.commands

interface Undoable<T> {
    fun undo(item: T)
}
