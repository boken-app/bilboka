package bilboka.messagebot.commands

import bilboka.core.user.domain.User
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.GeneralChatCommand

internal class UserInfo : GeneralChatCommand() {
    private val matcher = Regex(
        "(brukerinfo)",
        RegexOption.IGNORE_CASE
    )

    override fun isMatch(message: String): Boolean {
        return matcher.containsMatchIn(message)
    }

    override fun execute(conversation: Conversation, message: String) {
        conversation.user?.apply {
            replyWithInfo(this, conversation)
        } ?: conversation.sendReply(
            "Du har ikke en registrert bruker. 😢"
        )
    }

    private fun replyWithInfo(
        user: User,
        conversation: Conversation
    ) {
        conversation.sendReply(
            "Du er registrert! \n" +
                    "Brukernavn: ${user.username}"
        )
    }

}
