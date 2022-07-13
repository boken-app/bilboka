package bilboka.messagebot.commands

class SmallTalk : CarBookCommand {

    val conversations = mapOf(
        Pair("Hei", "Hei"),
        Pair("Hei!", "Hei!"),
        Pair("Driver med", "Ikke noe spes. Der?"),
        Pair("Skjer", "Ikke noe spes. Der?"),
        Pair("Skjer?", "Ikke noe spes. Der?"),
        Pair("Hvem der", "Bare meg!"),
        Pair("Ikke noe", "ok"),
        Pair("Ingenting", "ok"),
        Pair("Meh", "meh"),
        Pair("Jada", "Joda så"),
        Pair(":)", "^^"),
    )

    override fun isMatch(message: String): Boolean {
        return conversations.keys.contains(message)
    }

    override fun execute(message: String): String {
        return conversations[message] ?: "Usikker på hva du mener med $message"
    }
}
