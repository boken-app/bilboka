package bilboka.messagebot.commands

import bilboka.core.book.Book
import bilboka.core.book.domain.BookEntry
import bilboka.core.user.UserService
import bilboka.messagebot.Conversation
import bilboka.messagebot.format
import kotlin.text.RegexOption.IGNORE_CASE

class FuelEntryAdder(
    private val book: Book,
    userService: UserService
) : CarBookCommand(userService), Undoable<BookEntry> {
    private val matcher = Regex(
        "(drivstoff|tank|fylt|fuel|bensin|diesel)\\s+(\\w+[[\\s-]+?\\w]+?)\\s([0-9]{1,7})\\s?(km|mi)?\\s+(\\d+[.|,]?\\d{0,2})\\s?l\\s+(\\d+[.|,]?\\d{0,2})\\s?kr",
        IGNORE_CASE
    )

    override fun isMatch(message: String): Boolean {
        return matcher.containsMatchIn(message)
    }

    override fun execute(conversation: Conversation, message: String) {
        val values = matcher.find(message)!!.groupValues
        val vehicleName = values[2]
        val odoReading = values[3]
        val amount = values[5]
        val cost = values[6]

        val addedFuel = book.addFuelForVehicle(
            vehicleName = vehicleName,
            enteredBy = conversation.withWhom(),
            odoReading = odoReading.toInt(),
            amount = amount.convertToDouble(),
            costNOK = cost.convertToDouble(),
            source = conversation.getSource(),
        )
        conversation.setUndoable(this, addedFuel)

        conversation.sendReply(
            "Registrert tanking av ${addedFuel.vehicle.name} ved ${addedFuel.odometer} ${addedFuel.vehicle.odometerUnit}: ${addedFuel.amount.format()} liter for ${addedFuel.costNOK.format()} kr, ${
                addedFuel.pricePerLiter().format()
            } kr/l"
        )
    }

    override fun resetState(conversation: Conversation?) {

    }

    override fun undo(item: BookEntry) {
        item.delete()
    }
}

private fun String.convertToDouble(): Double {
    return this.replace(',', '.').toDouble()
}
