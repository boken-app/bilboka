package bilboka.messagebot.commands

import bilboka.core.book.Book
import bilboka.core.book.domain.BookEntry
import bilboka.core.user.UserService
import bilboka.core.vehicle.domain.Vehicle
import bilboka.messagebot.BotMessenger
import bilboka.messagebot.format

class FuelEntryGetter(
    private val botMessenger: BotMessenger,
    private val book: Book,
    userService: UserService
) : CarBookCommand(botMessenger, userService) {
    private val matcher = Regex(
        "siste\\s+(\\w+([\\s-]+?\\w+)?)",
        RegexOption.IGNORE_CASE
    )

    override fun isMatch(message: String): Boolean {
        return matcher.containsMatchIn(message)
    }

    override fun execute(senderID: String, message: String) {
        val (vehicleName) = matcher.find(message)!!.destructured

        book.getLastFuelEntry(vehicleName)?.apply {
            replyWithLastEntry(this.vehicle, this, senderID)
        } ?: botMessenger.sendMessage(
            "Finner ingen tankinger for $vehicleName",
            senderID
        )
    }

    private fun replyWithLastEntry(
        vehicle: Vehicle,
        lastBookEntry: BookEntry,
        senderID: String
    ) {
        botMessenger.sendMessage(
            "Siste tanking av ${vehicle.name}: ${lastBookEntry.amount.format()} liter " +
                    "for ${lastBookEntry.costNOK.format()} kr (${lastBookEntry.pricePerLiter().format()} kr/l) ${
                        lastBookEntry.dateTime?.format()
                    } ved ${lastBookEntry.odometer} ${vehicle.odometerUnit}",
            senderID
        )
    }

    override fun resetState() {

    }
}
