package bilboka.messagebot.commands

import bilboka.core.book.Book
import bilboka.core.book.domain.BookEntry
import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.Vehicle
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.CarBookCommand
import bilboka.messagebot.commands.common.ChatState
import bilboka.messagebot.commands.common.ODOMETER_REGEX
import bilboka.messagebot.commands.common.Undoable
import bilboka.messagebot.format
import kotlin.text.RegexOption.IGNORE_CASE

internal class FuelEntryAdder(
    private val book: Book,
    private val vehicleService: VehicleService,
    userService: UserService
) : CarBookCommand(userService), Undoable<BookEntry> {
    private val fullMatcher = Regex(
        "(drivstoff|tank|fylt|fuel|bensin|diesel)\\s+(\\w+[\\s-+?\\w]+?)\\s([0-9]{1,7})\\s?(km|mi)?\\s+(\\d+[.|,]?\\d{0,2})\\s?l\\s+(\\d+[.|,]?\\d{0,2})\\s?kr",
        IGNORE_CASE
    )

    override fun isMatch(message: String): Boolean {
        return Regex(
            "(drivstoff|tank|fylt|fuel|bensin|diesel)",
            IGNORE_CASE
        ).containsMatchIn(message)
    }

    override fun execute(conversation: Conversation, message: String) {
        val claimed = conversation.withdrawClaim<State>(this)
        if (claimed != null) {
// TODO her må det ryddes!

            if (claimed.vehicle.wasJustQueried) {
                val vehicle = Regex("\\w+[\\s-+?\\w]+").find(message)?.let { vehicleService.getVehicle(it.value) }
                conversation.claim(this, claimed.apply {
                    this.vehicle.wasJustQueried = false
                    this.vehicle.content = vehicle
                    this.odometer.wasJustQueried = true
                    conversation.sendReply(this.odometer.query)
                })
                return
            }
            if (claimed.odometer.wasJustQueried) {
                val odo = Regex(ODOMETER_REGEX).find(message)?.value?.toInt()!!
                conversation.claim(this, claimed.apply {
                    this.odometer.wasJustQueried = false
                    this.odometer.content = odo
                    this.amount.wasJustQueried = true
                    conversation.sendReply(this.amount.query)
                })
                return
            }
            if (claimed.amount.wasJustQueried) {
                val amount = message.convertToDouble()
                conversation.claim(this, claimed.apply {
                    this.amount.wasJustQueried = false
                    this.amount.content = amount
                    this.cost.wasJustQueried = true
                    conversation.sendReply(this.cost.query)
                })
                return
            }
            if (claimed.cost.wasJustQueried) {
                val cost = message.convertToDouble()
                claimed.apply {
                    this.cost.wasJustQueried = false
                    this.cost.content = cost
                }
            }
            if (claimed.gatheredData.all { it.value.content != null }) {
                finish(
                    conversation, book.addFuelForVehicle(
                        vehicleName = (claimed.vehicle.content as Vehicle).name,
                        enteredBy = conversation.withWhom(),
                        odoReading = claimed.odometer.content as Int,
                        amount = claimed.amount.content as Double,
                        costNOK = claimed.cost.content as Double,
                        source = conversation.getSource(),
                    )
                )
                return
            }
        } else {


            if (fullMatcher.containsMatchIn(message)) {
                val values = fullMatcher.find(message)!!.groupValues
                val vehicleName = values[2]
                val odoReading = values[3]
                val amount = values[5]
                val cost = values[6]

                finish(
                    conversation, book.addFuelForVehicle(
                        vehicleName = vehicleName,
                        enteredBy = conversation.withWhom(),
                        odoReading = odoReading.toInt(),
                        amount = amount.convertToDouble(),
                        costNOK = cost.convertToDouble(),
                        source = conversation.getSource(),
                    )
                )
                return
            }

            val vehicleMatcher = Regex(
                "(drivstoff|tank|fylt|fuel|bensin|diesel)\\s+(\\w+[[\\s-]+?\\w]+?)\\s(\\d)\\s?(\\D)?",
                IGNORE_CASE
            )

            val vehicle = vehicleMatcher.find(message)?.let { vehicleService.findVehicle(it.groupValues[2]) }
            if (vehicle == null) {
                conversation.claim(this, State().apply { this.vehicle.wasJustQueried = true })
                conversation.sendReply("Hvilken bil?")
            }
        }

    }

    private fun finish(conversation: Conversation, addedFuel: BookEntry) {
        conversation.setUndoable(this, addedFuel)

        conversation.sendReply(
            "Registrert tanking av ${addedFuel.vehicle.name} ved ${addedFuel.odometer} ${addedFuel.vehicle.odometerUnit}: " +
                    "${addedFuel.amount.format()} liter for ${addedFuel.costNOK.format()} kr, " +
                    "${addedFuel.pricePerLiter().format()} kr/l"
        )
    }

    override fun undo(item: BookEntry) {
        item.delete()
    }

    data class FuelingDataItem(
        val query: String,
        var content: Any? = null,
        var wasJustQueried: Boolean = false
    )

    class State(
    ) : ChatState() {
        enum class FuelDataType { VEHICLE, ODOMETER, AMOUNT, COST }

        val gatheredData = mapOf(
            Pair(FuelDataType.VEHICLE, FuelingDataItem("Hvilken bil?")),
            Pair(FuelDataType.ODOMETER, FuelingDataItem("Kilometerstand?")),
            Pair(FuelDataType.AMOUNT, FuelingDataItem("Antall liter?")),
            Pair(FuelDataType.COST, FuelingDataItem("Kroner?"))
        )
        val vehicle = gatheredData[FuelDataType.VEHICLE]!!
        val odometer = gatheredData[FuelDataType.ODOMETER]!!
        val amount = gatheredData[FuelDataType.AMOUNT]!!
        val cost = gatheredData[FuelDataType.COST]!!
    }
}

private fun String.convertToDouble(): Double {
    return this.replace(',', '.').toDouble()
}
