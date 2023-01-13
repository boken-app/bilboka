package bilboka.messagebot.commands

import bilboka.messagebot.BotMessenger

internal const val DEFAULT_HELP_MESSAGE = "For å registrere drivstoff, skriv f.eks. \"Drivstoff XC70 256789 30l 300kr\""

class Helper(private val botMessenger: BotMessenger) : GeneralChatCommand(botMessenger) {

    override fun isMatch(message: String): Boolean {
        return setOf("hjelp", "help", "hlp", "h", "info", "?").contains(message.lowercase())
    }

    override fun execute(senderID: String, message: String) {
        botMessenger.sendMessage(DEFAULT_HELP_MESSAGE, senderID)
    }

    override fun resetState() {

    }
}
