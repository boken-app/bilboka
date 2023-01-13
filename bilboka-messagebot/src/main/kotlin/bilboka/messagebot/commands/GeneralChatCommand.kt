package bilboka.messagebot.commands

import bilboka.messagebot.BotMessenger

abstract class GeneralChatCommand(botMessenger: BotMessenger) : ChatCommand(botMessenger) {
    override fun validUser(regTypeID: String, senderID: String): Boolean {
        return true
    }
}
