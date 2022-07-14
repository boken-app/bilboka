package bilboka.messagebot.commands

class SmallTalk : CarBookCommand {

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
        Pair("meh", "meh"),
        Pair("jada", "Joda så"),
        Pair(":)", "^^"),
        Pair("😊", ":D"),
    )

    override fun isMatch(message: String): Boolean {
        return conversations.keys.contains(message.lowercase())
    }

    override fun execute(message: String): String {
        return conversations[message.lowercase()] ?: "Usikker på hva du mener med $message"
    }
}
