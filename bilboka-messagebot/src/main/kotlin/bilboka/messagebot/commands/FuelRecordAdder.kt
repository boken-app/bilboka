package bilboka.messagebot.commands

import bilboka.core.book.domain.FuelRecord
import bilboka.core.vehicle.VehicleNotFoundException
import bilboka.messagebot.BotMessenger
import bilboka.messagebot.CarBookExecutor
import kotlin.text.RegexOption.IGNORE_CASE

class FuelRecordAdder(
    private val botMessenger: BotMessenger,
    private val executor: CarBookExecutor
) : CarBookCommand(botMessenger) {
    private val matcher = Regex(
        "(drivstoff|tank|fylt|fuel)\\s+(\\w+[\\s+?\\w]+?)\\s+(\\d+[.|,]?\\d{0,2})\\s?l\\s+(\\d+[.|,]?\\d{0,2})\\s?kr",
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
            val fuelRecord = FuelRecord(
                odometer = null, // TODO
                amount = amount.convertToDouble(),
                costNOK = cost.convertToDouble(),
            )
            val vehicle = executor.addRecordToVehicle(
                fuelRecord,
                vehicleName
            )

            botMessenger.sendMessage(
                "Registrert tanking av ${vehicle.name}, ${fuelRecord.amount} liter for ${fuelRecord.costNOK} kr, ${fuelRecord.pricePerLiter()} kr/l",
                senderID
            )
        } catch (e: VehicleNotFoundException) {
            botMessenger.sendMessage("Kjenner ikke til bil $vehicleName", senderID)
        }
    }

    override fun resetState() {

    }
}

private fun String.convertToDouble(): Double {
    if (this.contains(',')) {
        return this.replace(',', '.').toDouble()
    }
    return this.toDouble()
}
