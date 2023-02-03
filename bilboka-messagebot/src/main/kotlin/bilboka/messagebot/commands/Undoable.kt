package bilboka.messagebot.commands

interface Undoable<in T : Any> {
    fun undo(item: T)
}
