package bilboka.messagebot.commands

import bilboka.core.exception.VehicleNotFoundException
import bilboka.messagebot.BotMessenger
import bilboka.messagebot.CarBookExecutor
import kotlin.text.RegexOption.IGNORE_CASE

class FuelRecordAdder(
    private val botMessenger: BotMessenger,
    private val executor: CarBookExecutor
) : CarBookCommand(botMessenger) {
    private val matcher = Regex(
        "(drivstoff|tanking|fylt|fuel)\\s+(\\w+[\\s+?\\w]+?)\\s+(\\d+[.|,]?\\d{0,2})\\s?l\\s+(\\d+[.|,]?\\d{0,2})\\s?kr",
        IGNORE_CASE
    )

    override fun isMatch(message: String): Boolean {
        return matcher.containsMatchIn(message)
    }

    override fun execute(senderID: String, message: String) {
        val values = matcher.find(message)!!.groupValues
        val vehicleName = values[2]
        val amount = values[3]
        val cost = values[4]

        try {
            val fuelRecord = executor.addFuelRecord(vehicleName, amount.convertToDouble(), cost.convertToDouble())
            botMessenger.sendMessage(
                "Registrert tanking av $vehicleName, ${fuelRecord.amount} liter for ${fuelRecord.costNOK} kr, ${fuelRecord.pricePerLiter()} kr/l",
                senderID
            )
        } catch (e: VehicleNotFoundException) {
            botMessenger.sendMessage("Kjenner ikke til bil $vehicleName", senderID)
        }
    }
}

private fun String.convertToDouble(): Double {
    if (this.contains(',')) {
        return this.replace(',', '.').toDouble()
    }
    return this.toDouble()
}
