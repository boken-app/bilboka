package bilboka.messagebot

import bilboka.core.user.domain.User

class Conversation(
    var user: User? = null,
    val senderID: String,
    val botMessenger: BotMessenger
) {
    fun getSource(): String {
        return botMessenger.sourceID
    }

    fun withWhom(): User {
        return user
            ?: throw DontKnowWithWhomException("Samtalen kjenner ikke til bruker med id $senderID for meldingskilde ${getSource()}")
    }

    fun registerUser(user: User) {
        if (this.user != null) {
            throw IllegalStateException("Kan ikke endre bruker på eksisterende samtale")
        }
        this.user = user
    }

    fun sendReply(message: String) {
        botMessenger.sendMessage(
            message,
            senderID
        )
    }
}

class DontKnowWithWhomException(message: String) : RuntimeException(message)
