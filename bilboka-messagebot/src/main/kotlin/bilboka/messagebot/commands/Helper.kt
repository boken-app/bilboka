package bilboka.messagebot.commands

import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.GeneralChatCommand

internal const val DEFAULT_HELP_MESSAGE =
    "For å registrere drivstoff, skriv f.eks. \"Drivstoff XC70 256789 30l 300kr\" \n" +
            "Andre kommandoer: \n" +
            "Angre -> Angre siste handling\n" +
            "Siste [bil] -> Siste drivstoff-fylling\n" +
            "Info [bil] -> Info om valgt bil\n" +
            "Statistikk -> Se statistikk over siste drivstoffpriser" +
            "Brukerinfo -> Se info om brukeren du er registrert som\n" +
            ""

internal class Helper : GeneralChatCommand() {

    override fun isMatch(message: String): Boolean {
        return setOf("hjelp", "help", "hlp", "h", "info", "?").contains(message.lowercase())
    }

    override fun execute(conversation: Conversation, message: String) {
        conversation.sendReply(DEFAULT_HELP_MESSAGE)
    }

}
