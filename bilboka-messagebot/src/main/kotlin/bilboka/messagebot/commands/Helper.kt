package bilboka.messagebot.commands

import bilboka.messagebot.Conversation

internal const val DEFAULT_HELP_MESSAGE = "For å registrere drivstoff, skriv f.eks. \"Drivstoff XC70 256789 30l 300kr\""

class Helper : GeneralChatCommand() {

    override fun isMatch(message: String): Boolean {
        return setOf("hjelp", "help", "hlp", "h", "info", "?").contains(message.lowercase())
    }

    override fun execute(conversation: Conversation, message: String) {
        conversation.sendReply(DEFAULT_HELP_MESSAGE)
    }

    override fun resetState() {

    }
}
