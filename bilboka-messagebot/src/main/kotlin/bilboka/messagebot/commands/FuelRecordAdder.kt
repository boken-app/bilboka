package bilboka.messagebot.commands

import bilboka.core.book.Book
import bilboka.messagebot.BotMessenger
import bilboka.messagebot.format
import kotlin.text.RegexOption.IGNORE_CASE

class FuelRecordAdder(
    private val botMessenger: BotMessenger,
    private val book: Book
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

        val addedFuel = book.addFuelForVehicle(
            vehicleName,
            odoReading = odoReading.toInt(),
            amount = amount.convertToDouble(),
            costNOK = cost.convertToDouble(),
            source = botMessenger.sourceName,
        )

        botMessenger.sendMessage(
            "Registrert tanking av ${addedFuel.vehicle.name} ved ${addedFuel.odometer} ${addedFuel.vehicle.odometerUnit}: ${addedFuel.amount.format()} liter for ${addedFuel.costNOK.format()} kr, ${
                addedFuel.pricePerLiter().format()
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
