package bilboka.messagebot.commands

import bilboka.core.book.domain.FuelRecord
import bilboka.core.vehicle.Vehicle
import bilboka.messagebot.BotMessenger
import bilboka.messagebot.CarBookExecutor
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class FuelRecordGetter(
    private val botMessenger: BotMessenger,
    private val executor: CarBookExecutor
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
        val book = executor.getBookForVehicle(vehicleName)

        book.getLastFuelRecord()?.apply {
            replyWithLastRecord(book.vehicle, this, senderID)
        } ?: botMessenger.sendMessage(
            "Finner ingen tankinger for ${book.vehicle.name}",
            senderID
        )
    }

    private fun replyWithLastRecord(
        vehicle: Vehicle,
        lastRecord: FuelRecord,
        senderID: String
    ) {
        botMessenger.sendMessage(
            "Siste tanking av ${vehicle.name}: ${lastRecord.amount} liter " +
                    "for ${lastRecord.costNOK} kr (${lastRecord.pricePerLiter()} kr/l) ${
                        lastRecord.dateTime?.format(
                            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                        )
                    } ved ${lastRecord.odometer} ${vehicle.odometerUnit}",
            senderID
        )
    }

    override fun resetState() {

    }
}
