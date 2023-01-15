package bilboka.messagebot.commands

import bilboka.core.user.UserService
import bilboka.core.user.domain.User
import bilboka.messagebot.BotMessenger

class UserInfo(
    private val botMessenger: BotMessenger,
    private val userService: UserService
) : GeneralChatCommand() {
    private val matcher = Regex(
        "(brukerinfo)",
        RegexOption.IGNORE_CASE
    )

    override fun isMatch(message: String): Boolean {
        return matcher.containsMatchIn(message)
    }

    override fun execute(senderID: String, message: String) {
        userService.getUserByRegistration(botMessenger.sourceID, senderID)?.apply {
            replyWithInfo(this, senderID)
        } ?: botMessenger.sendMessage(
            "Du har ikke en registrert bruker. :(",
            senderID
        )
    }

    private fun replyWithInfo(
        user: User,
        senderID: String
    ) {
        botMessenger.sendMessage(
            "Du er registrert! \n" +
                    "Brukernavn: ${user.username}",
            senderID
        )
    }

    override fun resetState() {

    }
}
