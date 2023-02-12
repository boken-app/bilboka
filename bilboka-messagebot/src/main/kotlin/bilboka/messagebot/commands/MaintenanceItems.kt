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
        "(vedlikehold|vedl)",
        RegexOption.IGNORE_CASE
    )

    override fun isMatch(message: String): Boolean {
        return matcher.containsMatchIn(message)
    }

    override fun execute(conversation: Conversation, message: String) {
        conversation.sendReply(
            book.maintenanceItems().joinToString(
                prefix = "Eksisterende vedlikeholdspunkt: \n",
                separator = "\n",
                // TODO når den funksjonen er implementert.
                //   postfix = " \n Skriv '[vedlikeholdspunkt] [bil-navn]' for å se siste oppføring på gitt bil"
            )
        )
    }
}
