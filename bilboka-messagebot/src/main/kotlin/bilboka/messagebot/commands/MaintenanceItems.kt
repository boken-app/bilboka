package bilboka.messagebot.commands

import bilboka.core.book.Book
import bilboka.core.user.UserService
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.CarBookCommand

internal class MaintenanceItems(
    private val book: Book,
    userService: UserService
) : CarBookCommand(userService) {
    private val matcher = Regex(
        "^(vedlikeholdspunkt|vedlikehold|vedl)$",
        RegexOption.IGNORE_CASE
    )

    override fun isMatch(message: String): Boolean {
        return matcher.containsMatchIn(message)
    }

    override fun execute(conversation: Conversation, message: String) {
        conversation.sendReply(
            book.maintenanceItems().sorted().joinToString(
                prefix = "Eksisterende vedlikeholdspunkt: \n  ",
                separator = "\n  ",
                postfix = " \n(Skriv 'Siste [vedlikeholdspunkt] [bil-navn]' for å se siste oppføring på gitt bil)"
            )
        )
    }
}
