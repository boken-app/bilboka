package bilboka.messagebot.commands

import bilboka.messagebot.BotMessenger

class Helper(private val botMessenger: BotMessenger) : CarBookCommand(botMessenger) {

    override fun isMatch(message: String): Boolean {
        return setOf("hjelp", "help", "hlp", "h", "info", "?").contains(message.lowercase())
    }

    override fun execute(senderID: String, message: String) {
        botMessenger.sendMessage("For å registrere drivstoff, skriv f.eks. \"Drivstoff xc70 30l 400kr\"", senderID)
    }
}
