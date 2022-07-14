package bilboka.messagebot.commands

import bilboka.core.exception.VehicleNotFoundException
import bilboka.messagebot.CarBookExecutor
import kotlin.text.RegexOption.IGNORE_CASE

class FuelRecordAdder(
    val executor: CarBookExecutor
) : CarBookCommand {
    private val matcher = Regex(
        "(drivstoff|tanking|fylt|fuel)\\s+(\\w+[\\s+?\\w]+?)\\s+(\\d+[.|,]?\\d{0,2})\\s?l\\s+(\\d+[.|,]?\\d{0,2})\\s?kr",
        IGNORE_CASE
    )

    override fun isMatch(message: String): Boolean {
        return matcher.containsMatchIn(message)
    }

    override fun execute(message: String): String {
        val values = matcher.find(message)!!.groupValues
        val vehicleName = values[2]
        val amount = values[3]
        val cost = values[4]

        return try {
            val fuelRecord = executor.addFuelRecord(vehicleName, amount.convertToDouble(), cost.convertToDouble())
            "Registrert tanking av $vehicleName, ${fuelRecord.amount} liter for ${fuelRecord.costNOK} kr, ${fuelRecord.pricePerLiter()} kr/l"
        } catch (e: VehicleNotFoundException) {
            "Kjenner ikke til bil $vehicleName"
        }
    }

}

private fun String.convertToDouble(): Double {
    if (this.contains(',')) {
        return this.replace(',', '.').toDouble()
    }
    return this.toDouble()
}
