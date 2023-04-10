package bilboka.messagebot.commands

import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.ChatState
import bilboka.messagebot.commands.common.GeneralChatCommand

internal class SmallTalk : GeneralChatCommand() {

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
        Pair("sup", "sup 😎"),
        Pair("hvem der", "Bare meg!"),
        Pair("ikke noe", "ok"),
        Pair("ingenting", "ok"),
        Pair("jada", "Joda så"),
        Pair(":)", "^^"),
        Pair("😊", ":D"),
        Pair("bil", "🏎"),
        Pair("lol", "😂"),
        Pair("ok", "👍"),
        Pair("hm", "😐"),
    )

    override fun isMatch(message: String): Boolean {
        if (message.lowercase() == "opts") return true
        return conversations.keys.contains(message.lowercase())
    }

    override fun execute(conversation: Conversation, message: String) {
        if (conversation.withdrawClaim<State>(this)?.hasAskedSomething == true) {
            conversation.sendReply("Cool")
        } else {
            conversation.sendReply(
                conversations[message.lowercase()] ?: "Usikker på hva du mener med $message"
            )
        }
        if (message.lowercase() == "skjer?") {
            conversation.claim(this, State(hasAskedSomething = true))
        }
        if (message.lowercase() == "opts") {
            conversation.replyWithOptions("En ting", "en annen ting", "stats")
        }
    }

    class State(val hasAskedSomething: Boolean) : ChatState()
}
