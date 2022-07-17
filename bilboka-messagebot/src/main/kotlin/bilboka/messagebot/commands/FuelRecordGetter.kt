package bilboka.messagebot.commands

import bilboka.core.book.domain.FuelRecord
import bilboka.messagebot.BotMessenger
import bilboka.messagebot.CarBookExecutor
import java.time.format.DateTimeFormatter

class FuelRecordGetter(
    private val botMessenger: BotMessenger,
    private val executor: CarBookExecutor
) : CarBookCommand(botMessenger) {
    private val matcher = Regex(
        "siste\\s+(\\w+(\\s+?\\w+)?)",
        RegexOption.IGNORE_CASE
    )

    override fun isMatch(message: String): Boolean {
        return matcher.containsMatchIn(message)
    }

    override fun execute(senderID: String, message: String) {
        val (vehicleName) = matcher.find(message)!!.destructured
        val book = executor.getBookForVehicle(vehicleName)
        val lastRecord = book.getLastFuelRecord()
        if (lastRecord != null) {
            replyWithLastRecord(book.vehicle.name, lastRecord, senderID)
        } else {
            botMessenger.sendMessage(
                "Finner ingen tankinger for ${book.vehicle.name}",
                senderID
            )
        }
    }

    private fun replyWithLastRecord(
        vehicleName: String,
        lastRecord: FuelRecord,
        senderID: String
    ) {
        botMessenger.sendMessage(
            "Siste tanking av $vehicleName: ${lastRecord.amount} liter " +
                    "for ${lastRecord.costNOK} kr (${lastRecord.pricePerLiter()} kr/l) ${
                        lastRecord.dateTime?.format(
                            DateTimeFormatter.ISO_DATE
                        )
                    }",
            senderID
        )
    }

    override fun resetState() {

    }
}
