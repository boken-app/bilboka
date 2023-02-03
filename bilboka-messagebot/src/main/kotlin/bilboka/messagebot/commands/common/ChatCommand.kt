package bilboka.messagebot.commands.common

import bilboka.messagebot.Conversation

abstract class ChatCommand {

    abstract fun isMatch(message: String): Boolean
    abstract fun validUser(regTypeID: String, senderID: String): Boolean
    abstract fun execute(conversation: Conversation, message: String)

    // TODO tror vi kan droppe denne nå
    abstract fun resetState(conversation: Conversation? = null)

}
