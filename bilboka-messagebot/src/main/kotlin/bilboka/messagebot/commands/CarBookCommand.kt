package bilboka.messagebot.commands

import bilboka.core.user.UserService
import bilboka.messagebot.BotMessenger

abstract class CarBookCommand(
    botMessenger: BotMessenger,
    private val userService: UserService
) : ChatCommand(botMessenger) {
    override fun validUser(regTypeID: String, senderID: String): Boolean {
        return userService.getUserByRegistration(regTypeID, senderID) != null
    }
}
