package bilboka.messagebot.commands

import bilboka.core.book.Book
import bilboka.core.user.UserService
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.CarBookCommand

internal class ReportGetter(val book: Book, userService: UserService) : CarBookCommand(userService) {
    override fun isMatch(message: String): Boolean {
        return message.contentEquals("rapport", true)
    }

    override fun execute(conversation: Conversation, message: String) {
        val report = book.getReport("Teste litt rapport da")

        conversation.sendFile(report)
    }
}
