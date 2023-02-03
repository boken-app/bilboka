package bilboka.messagebot.commands.common

interface Undoable<in T : Any> {
    fun undo(item: T)
}
