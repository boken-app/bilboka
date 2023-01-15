package bilboka.messagebot.commands

abstract class ChatCommand {

    abstract fun isMatch(message: String): Boolean
    abstract fun validUser(regTypeID: String, senderID: String): Boolean
    abstract fun execute(senderID: String, message: String)
    abstract fun resetState()

}
