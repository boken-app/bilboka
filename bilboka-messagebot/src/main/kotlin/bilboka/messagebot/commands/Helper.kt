package bilboka.messagebot.commands

class Helper : CarBookCommand {

    override fun isMatch(message: String): Boolean {
        return setOf("hjelp", "help", "info", "?").contains(message.lowercase())
    }

    override fun execute(message: String): String {
        return "Her kommer det en hjelpetkst."
    }
}
