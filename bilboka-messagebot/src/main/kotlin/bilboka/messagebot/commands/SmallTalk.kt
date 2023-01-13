package bilboka.messagebot.commands

import bilboka.messagebot.BotMessenger

class SmallTalk(private val botMessenger: BotMessenger) : GeneralChatCommand(botMessenger) {

    private var hasAskedSomething = false
    // TODO: Denne må håndteres pr. senderID for å fungere for flere brukere.

    val conversations = mapOf(
        Pair("hei", "Hei"),
        Pair("hei!", "Hei! :D"),
        Pair("hey!", "Yo!"),
        Pair("hey", "Yo!"),
        Pair("yo", "hey"),
        Pair("hmm", "hm?"),
        Pair("meh", "¯\\_(ツ)_/¯"),
        Pair("driver med", "Ikke noe spes. Der?"),
        Pair("skjer", "Ikke noe spes. Der?"),
        Pair("skjer?", "Ikke noe spes. Der?"),
        Pair("sup", "sup"),
        Pair("hvem der", "Bare meg!"),
        Pair("ikke noe", "ok"),
        Pair("ingenting", "ok"),
        Pair("jada", "Joda så"),
        Pair(":)", "^^"),
        Pair("😊", ":D"),
    )

    override fun isMatch(message: String): Boolean {
        return hasAskedSomething || conversations.keys.contains(message.lowercase())
    }

    override fun execute(senderID: String, message: String) {
        if (hasAskedSomething) {
            botMessenger.sendMessage("Cool", senderID)
            resetState()
        } else {
            botMessenger.sendMessage(
                conversations[message.lowercase()] ?: "Usikker på hva du mener med $message",
                senderID
            )
        }
        if (message.lowercase() == "skjer?") {
            hasAskedSomething = true
        }
    }

    override fun resetState() {
        hasAskedSomething = false
    }
}
