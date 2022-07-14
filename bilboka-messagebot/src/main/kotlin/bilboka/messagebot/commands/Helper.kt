package bilboka.messagebot.commands

class Helper : CarBookCommand {

    override fun isMatch(message: String): Boolean {
        return setOf("hjelp", "help", "hlp", "h", "info", "?").contains(message.lowercase())
    }

    override fun execute(message: String): String {
        return "For å registrere drivstoff, skriv f.eks. \"Drivstoff xc70 30l 400kr\""
    }
}
