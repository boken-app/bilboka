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
            if (message.isUnknown()) {
                this.second.isUnknown = true
            } else {
                this.second.content = when (this.first) {
                    State.FuelDataType.VEHICLE ->
                        Regex("[\\wæøå]+[\\s-+?[\\wæøå]]+").find(message)
                            ?.let { vehicleService.getVehicle(it.value) }?.name
                    State.FuelDataType.ODOMETER -> message.toInt()
                    State.FuelDataType.AMOUNT -> message.convertToDouble()
                    State.FuelDataType.COST -> message.convertToDouble()
                    State.FuelDataType.COST_PER_AMOUNT -> message.convertToDouble()
                }
            }
        }
    }

    private fun findAsMuchDataAsPossible(message: String): State {
        val workInProgress = State().apply {
            this.vehicle.content = lookForVehicleBetweenBeginningOfMessageAndFirstNumber(message)
        }
        VOLUME_REGEX.find(message)?.let { workInProgress.amount.content = it.groupValues[1].convertToDouble() }
        COST_REGEX.find(message)?.let { workInProgress.cost.content = it.groupValues[1].convertToDouble() }
        COST_PER_AMOUNT_REGEX.find(message)
            ?.let { workInProgress.costPerAmount.content = it.groupValues[1].convertToDouble() }
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
        state.complete()?.run {
            finish(
                conversation, book.addFuelForVehicle(
                    vehicleName = (this.vehicle.content as String),
                    enteredBy = conversation.withWhom(),
                    odoReading = this.odometer.content as Int?,
                    amount = this.amount.content as Double?,
                    costNOK = this.cost.content as Double?,
                    source = conversation.getSource(),
                )
            )
        } ?: askForNext(conversation, state)
    }

    private fun askForNext(conversation: Conversation, stateInProgress: State) {
        stateInProgress.collectedData.values.first { it.content == null && !it.isUnknown }.let {
            it.wasJustQueried = true
            conversation.claim(this, stateInProgress)
            conversation.sendReply(it.query)
        }
    }

    private fun finish(conversation: Conversation, addedFuel: BookEntry) {
        conversation.setUndoable(this, addedFuel)

        conversation.sendReply(
            "⛽ Registrert tanking av ${addedFuel.vehicle.name} ved ${addedFuel.odometer ?: "<ukjent>"} ${addedFuel.vehicle.odometerUnit}: " +
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
        var isUnknown: Boolean = false,
        var wasJustQueried: Boolean = false
    )

    class State : ChatState() {
        enum class FuelDataType { VEHICLE, ODOMETER, AMOUNT, COST, COST_PER_AMOUNT }

        val collectedData = linkedMapOf(
            Pair(FuelDataType.VEHICLE, FuelingDataItem("Hvilken bil? \uD83D\uDE97")),
            Pair(FuelDataType.ODOMETER, FuelingDataItem("Kilometerstand? 🔢")),
            Pair(FuelDataType.AMOUNT, FuelingDataItem("Antall liter?")),
            Pair(FuelDataType.COST, FuelingDataItem("Kroner? 💸")),
            Pair(FuelDataType.COST_PER_AMOUNT, FuelingDataItem("Pris per liter?")),
        )
        val vehicle = collectedData[FuelDataType.VEHICLE]!!
        val odometer = collectedData[FuelDataType.ODOMETER]!!
        val amount = collectedData[FuelDataType.AMOUNT]!!
        val cost = collectedData[FuelDataType.COST]!!
        val costPerAmount = collectedData[FuelDataType.COST_PER_AMOUNT]!!

        fun complete(): State? {
            if (!isDoneAskingForStuff()) {
                return null
            }
            fillOutMissingIfPossible()
            if (vehicle.content == null || (odometer.content == null && !odometer.isUnknown)) {
                return null
            }
            return this
        }

        private fun isDoneAskingForStuff() =
            setOf(amount, cost, costPerAmount).filter { it.content != null }.size >= 2
                    || setOf(amount, cost, costPerAmount).all { it.isUnknown || it.content != null }

        private fun fillOutMissingIfPossible() {
            if (amount.content == null && cost.content != null && costPerAmount.content != null) {
                amount.content = cost.content as Double / costPerAmount.content as Double
            }
            if (cost.content == null && amount.content != null && costPerAmount.content != null) {
                cost.content = amount.content as Double * costPerAmount.content as Double
            }
        }
    }
}

private fun String.isUnknown(): Boolean {
    return Regex(
        "(ukjent|\\?|dunno|vet ikke)",
        IGNORE_CASE
    ).containsMatchIn(this)
}

private fun String.convertToDouble(): Double {
    return this.replace(',', '.').toDouble()
}
