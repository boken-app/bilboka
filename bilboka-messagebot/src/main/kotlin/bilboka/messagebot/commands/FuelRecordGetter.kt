package bilboka.messagebot.commands

import bilboka.core.book.domain.FuelRecord
import bilboka.messagebot.BotMessenger
import bilboka.messagebot.CarBookExecutor

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
            val fuelRecord = lastRecord as FuelRecord
            botMessenger.sendMessage(
                "Siste tanking av $vehicleName, ${fuelRecord.amount} liter for ${fuelRecord.costNOK} kr, ${fuelRecord.pricePerLiter()} kr/l",
                senderID
            )
        }
    }

    override fun resetState() {

    }
}
