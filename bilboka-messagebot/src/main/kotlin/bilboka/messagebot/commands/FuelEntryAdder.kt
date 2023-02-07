package bilboka.messagebot.commands

import bilboka.core.book.Book
import bilboka.core.book.domain.BookEntry
import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleService
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.*
import bilboka.messagebot.format
import kotlin.text.RegexOption.IGNORE_CASE

internal class FuelEntryAdder(
    private val book: Book,
    private val vehicleService: VehicleService,
    userService: UserService
) : CarBookCommand(userService), Undoable<BookEntry> {
    override fun isMatch(message: String): Boolean {
        return Regex(
            "(drivstoff|tank|fylt|fuel|bensin|diesel|tnk|⛽)",
            IGNORE_CASE
        ).containsMatchIn(message)
    }

    override fun execute(conversation: Conversation, message: String) {
        val previouslyProcessed = conversation.withdrawClaim<State>(this)
        if (previouslyProcessed != null) {
            recordProvidedData(previouslyProcessed, message)
            finishOrAskForMore(conversation, previouslyProcessed)
        } else {
            val foundSomeStuffMaybe = findAsMuchDataAsPossible(message)
            finishOrAskForMore(conversation, foundSomeStuffMaybe)
        }
    }

    private fun recordProvidedData(state: State, message: String) {
        state.collectedData.filter { it.value.wasJustQueried }.toList().first().apply {
            this.second.wasJustQueried = false
            this.second.content = when (this.first) {
                State.FuelDataType.VEHICLE ->
                    Regex("[\\wæøå]+[\\s-+?[\\wæøå]]+").find(message)?.let { vehicleService.getVehicle(it.value) }?.name
                State.FuelDataType.ODOMETER -> message.toInt()
                State.FuelDataType.AMOUNT -> message.convertToDouble()
                State.FuelDataType.COST -> message.convertToDouble()
            }
        }
    }

    private fun findAsMuchDataAsPossible(message: String): State {
        val workInProgress = State().apply {
            this.vehicle.content = lookForVehicleBetweenBeginningOfMessageAndFirstNumber(message)
        }
        VOLUME_REGEX.find(message)?.let { workInProgress.amount.content = it.groupValues[1].convertToDouble() }
        COST_REGEX.find(message)?.let { workInProgress.cost.content = it.groupValues[1].convertToDouble() }
        ODOMETER_REGEX.find(message)
            ?.let { workInProgress.odometer.content = (it.groups[1] ?: it.groups[2])?.value?.toInt() }

        return workInProgress
    }

    private fun lookForVehicleBetweenBeginningOfMessageAndFirstNumber(message: String): String? {
        val vehicleMatcher = Regex(
            "(?:drivstoff|tank|fylt|fuel|bensin|diesel|tnk)\\s+(([\\wæøå]+)(?:[\\s-][\\wæøå]+)?)(?:\\s*$ODOMETER_REGEX)?",
            IGNORE_CASE
        )
        return vehicleMatcher.find(message)?.let {
            vehicleService.findVehicle(it.groupValues[1])
                ?: vehicleService.getVehicle(it.groupValues[2])
        }?.name
    }

    private fun finishOrAskForMore(
        conversation: Conversation,
        state: State
    ) {
        if (state.collectedData.all { it.value.content != null }) {
            finish(
                conversation, book.addFuelForVehicle(
                    vehicleName = (state.vehicle.content as String),
                    enteredBy = conversation.withWhom(),
                    odoReading = state.odometer.content as Int,
                    amount = state.amount.content as Double,
                    costNOK = state.cost.content as Double,
                    source = conversation.getSource(),
                )
            )
        } else {
            askForNext(conversation, state)
        }
    }

    private fun askForNext(conversation: Conversation, stateInProgress: State) {
        stateInProgress.collectedData.values.first { it.content == null }.let {
            it.wasJustQueried = true
            conversation.claim(this, stateInProgress)
            conversation.sendReply(it.query)
        }
    }

    private fun finish(conversation: Conversation, addedFuel: BookEntry) {
        conversation.setUndoable(this, addedFuel)

        conversation.sendReply(
            "⛽ Registrert tanking av ${addedFuel.vehicle.name} ved ${addedFuel.odometer} ${addedFuel.vehicle.odometerUnit}: " +
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

    class State : ChatState() {
        enum class FuelDataType { VEHICLE, ODOMETER, AMOUNT, COST }

        val collectedData = linkedMapOf(
            Pair(FuelDataType.VEHICLE, FuelingDataItem("Hvilken bil? \uD83D\uDE97")),
            Pair(FuelDataType.ODOMETER, FuelingDataItem("Kilometerstand? 🔢")),
            Pair(FuelDataType.AMOUNT, FuelingDataItem("Antall liter?")),
            Pair(FuelDataType.COST, FuelingDataItem("Kroner? 💸"))
        )
        val vehicle = collectedData[FuelDataType.VEHICLE]!!
        val odometer = collectedData[FuelDataType.ODOMETER]!!
        val amount = collectedData[FuelDataType.AMOUNT]!!
        val cost = collectedData[FuelDataType.COST]!!
    }
}

private fun String.convertToDouble(): Double {
    return this.replace(',', '.').toDouble()
}
