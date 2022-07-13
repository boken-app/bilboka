package bilboka.messagebot.commands

interface CarBookCommand {

    fun isMatch(message: String): Boolean
    fun execute(message: String): String

}