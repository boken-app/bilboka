package bilboka.messagebot.commands

import bilboka.core.book.domain.FuelRecord
import bilboka.core.vehicle.FuelType
import bilboka.messagebot.BotMessenger
import bilboka.messagebot.CarBookExecutor
import bilboka.messagebot.format
import kotlin.text.RegexOption.IGNORE_CASE

class FuelRecordAdder(
    private val botMessenger: BotMessenger,
    private val executor: CarBookExecutor
) : CarBookCommand(botMessenger) {
    private val matcher = Regex(
        "(drivstoff|tank|fylt|fuel)\\s+(\\w+[[\\s-]+?\\w]+?)\\s([0-9]{1,7})\\s?(km|mi)?\\s+(\\d+[.|,]?\\d{0,2})\\s?l\\s+(\\d+[.|,]?\\d{0,2})\\s?kr",
        IGNORE_CASE
    )

    override fun isMatch(message: String): Boolean {
        return matcher.containsMatchIn(message)
    }

    override fun execute(senderID: String, message: String) {
        val values = matcher.find(message)!!.groupValues
        val vehicleName = values[2]
        val odoReading = values[3]
        val amount = values[5]
        val cost = values[6]

        val fuelRecord = FuelRecord(
            odometer = odoReading.toInt(),
            amount = amount.convertToDouble(),
            costNOK = cost.convertToDouble(),
            fuelType = FuelType.DIESEL,
        )
        val vehicle = executor.addRecordToVehicle(
            fuelRecord,
            vehicleName
        )

        botMessenger.sendMessage(
            "Registrert tanking av ${vehicle.name} ved ${fuelRecord.odometer} ${vehicle.odometerUnit}: ${fuelRecord.amount.format()} liter for ${fuelRecord.costNOK.format()} kr, ${
                fuelRecord.pricePerLiter().format()
            } kr/l",
            senderID
        )
    }

    override fun resetState() {

    }
}

private fun String.convertToDouble(): Double {
    return this.replace(',', '.').toDouble()
}
