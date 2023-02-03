package bilboka.messagebot.commands

import bilboka.core.user.UserService
import bilboka.messagebot.Conversation

class UndoLast(
    userService: UserService
) : CarBookCommand(userService) {
    private val matcher = Regex(
        "angre",
        RegexOption.IGNORE_CASE
    )

    override fun isMatch(message: String): Boolean {
        return matcher.containsMatchIn(message)
    }

    override fun execute(conversation: Conversation, message: String) {
        conversation.undoLast()
        conversation.sendReply("Angret")
    }

    override fun resetState(conversation: Conversation?) {
    }
}
