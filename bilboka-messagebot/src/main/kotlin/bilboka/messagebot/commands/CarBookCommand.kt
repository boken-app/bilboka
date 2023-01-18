package bilboka.messagebot.commands

import bilboka.core.user.UserService

abstract class CarBookCommand(
    private val userService: UserService
) : ChatCommand() {
    override fun validUser(regTypeID: String, senderID: String): Boolean {
        return userService.findUserByRegistration(regTypeID, senderID) != null
    }
}
