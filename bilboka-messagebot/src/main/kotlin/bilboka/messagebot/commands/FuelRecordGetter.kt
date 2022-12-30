package bilboka.messagebot.commands

import bilboka.core.book.Book
import bilboka.core.book.domain.Record
import bilboka.core.vehicle.domain.Vehicle
import bilboka.messagebot.BotMessenger
import bilboka.messagebot.format

class FuelRecordGetter(
    private val botMessenger: BotMessenger,
    private val book: Book
) : CarBookCommand(botMessenger) {
    private val matcher = Regex(
        "siste\\s+(\\w+([\\s-]+?\\w+)?)",
        RegexOption.IGNORE_CASE
    )

    override fun isMatch(message: String): Boolean {
        return matcher.containsMatchIn(message)
    }

    override fun execute(senderID: String, message: String) {
        val (vehicleName) = matcher.find(message)!!.destructured

        book.getLastFuelRecord(vehicleName)?.apply {
            replyWithLastRecord(this.vehicle, this, senderID)
        } ?: botMessenger.sendMessage(
            "Finner ingen tankinger for $vehicleName",
            senderID
        )
    }

    private fun replyWithLastRecord(
        vehicle: Vehicle,
        lastRecord: Record,
        senderID: String
    ) {
        botMessenger.sendMessage(
            "Siste tanking av ${vehicle.name}: ${lastRecord.amount.format()} liter " +
                    "for ${lastRecord.costNOK.format()} kr (${lastRecord.pricePerLiter().format()} kr/l) ${
                        lastRecord.dateTime?.format()
                    } ved ${lastRecord.odometer} ${vehicle.odometerUnit}",
            senderID
        )
    }

    override fun resetState() {

    }
}
