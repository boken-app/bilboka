package bilboka.messagebot.commands

import bilboka.core.book.Book
import bilboka.core.book.domain.BookEntry
import bilboka.core.user.UserService
import bilboka.core.vehicle.domain.Vehicle
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.CarBookCommand
import bilboka.messagebot.format

internal class FuelEntryGetter(
    private val book: Book,
    userService: UserService
) : CarBookCommand(userService) {
    private val matcher = Regex(
        "siste\\s+([\\wæøå]+([\\s-]+?[\\wæøå]+)?)",
        RegexOption.IGNORE_CASE
    )

    override fun isMatch(message: String): Boolean {
        return matcher.containsMatchIn(message)
    }

    override fun execute(conversation: Conversation, message: String) {
        val (vehicleName) = matcher.find(message)!!.destructured

        book.getLastFuelEntry(vehicleName)?.apply {
            replyWithLastEntry(this.vehicle, this, conversation)
        } ?: conversation.sendReply(
            "Finner ingen tankinger for $vehicleName"
        )
    }

    private fun replyWithLastEntry(
        vehicle: Vehicle,
        lastBookEntry: BookEntry,
        conversation: Conversation
    ) {
        conversation.sendReply(
            "Siste tanking av ${vehicle.name}: ${lastBookEntry.amount.format()} liter " +
                    "for ${lastBookEntry.costNOK.format()} kr (${lastBookEntry.pricePerLiter().format()} kr/l) ${
                        lastBookEntry.dateTime?.format()
                    } ved ${lastBookEntry.odometer} ${vehicle.odometerUnit}",
        )
    }

}
