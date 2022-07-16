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
        val lastRecord = executor.getLastRecord(vehicleName)
        if (lastRecord is FuelRecord) {
            botMessenger.sendMessage(
                "Siste tanking av $vehicleName: ${lastRecord.amount} liter " +
                        "for ${lastRecord.costNOK} kr (${lastRecord.pricePerLiter()} kr/l) ${
                            lastRecord.dateTime?.format(
                                DateTimeFormatter.ISO_DATE
                            )
                        }",
                senderID
            )
        } else {
            botMessenger.sendMessage(
                "Finner ingen tankinger for $vehicleName",
                senderID
            )
        }
    }

    override fun resetState() {

    }
}
