package bilboka.messagebot.commands.common

internal interface Undoable<in T : Any> {
    fun undo(item: T)
}
