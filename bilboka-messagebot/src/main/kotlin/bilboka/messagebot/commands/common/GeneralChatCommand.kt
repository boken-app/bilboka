package bilboka.messagebot.commands.common

internal abstract class GeneralChatCommand : ChatCommand() {
    override fun validUser(regTypeID: String, senderID: String): Boolean {
        return true
    }
}
