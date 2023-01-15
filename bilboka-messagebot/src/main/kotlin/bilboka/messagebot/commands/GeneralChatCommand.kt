package bilboka.messagebot.commands

abstract class GeneralChatCommand : ChatCommand() {
    override fun validUser(regTypeID: String, senderID: String): Boolean {
        return true
    }
}
