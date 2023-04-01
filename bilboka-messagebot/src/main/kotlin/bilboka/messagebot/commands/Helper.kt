package bilboka.messagebot.commands

import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.GeneralChatCommand

internal const val DEFAULT_HELP_MESSAGE =
    "For å registrere drivstoff, skriv f.eks. \"Drivstoff XC70 256789 30l 300kr\" \n" +
            "\nAndre kommandoer: \n" +
            "Angre -> Angre siste handling\n" +
            "Siste [bil] -> Siste drivstoff-fylling\n" +
            "Info [bil] -> Info om valgt bil \n" +
            "Statistikk -> Se statistikk over siste drivstoffpriser \n" +
            "Siste [vedlikehold] [bil] -> Siste gang angitt vedlikehold ble registrert\n" +
            "Rapport [år] [bil] -> Rapport over alle hendelser for et år \n" +
            "Vedlikehold -> Se oversikt over hvilke vedlikeholdspunkt som kan registreres \n" +
            "Brukerinfo -> Se info om brukeren du er registrert som \n" +
            "\nTips: Skriv 'tnk' for å registrere drivstofff, så vil du bli ledet gjennom resten" +
            ""

internal class Helper : GeneralChatCommand() {

    override fun isMatch(message: String): Boolean {
        return setOf("hjelp", "help", "hlp", "h", "info", "?").contains(message.lowercase())
    }

    override fun execute(conversation: Conversation, message: String) {
        conversation.sendReply(DEFAULT_HELP_MESSAGE)
    }

}
