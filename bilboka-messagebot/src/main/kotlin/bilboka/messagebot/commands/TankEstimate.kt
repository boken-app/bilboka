package bilboka.messagebot.commands

import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.Vehicle
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.*
import bilboka.messagebot.formatShort

internal class TankEstimate(
    private val vehicleService: VehicleService,
    userService: UserService
) : CarBookCommand(userService) {
    private val estimatMatcher = Regex("estimat", RegexOption.IGNORE_CASE)

    override fun isMatch(message: String): Boolean {
        return estimatMatcher.containsMatchIn(message)
    }

    override fun execute(conversation: Conversation, message: String) {
        val state = conversation.withdrawClaim<State>(this)?.apply {
            recordProvidedData(message) {
                when (this) {
                    State.EstimationDataTypes.VEHICLE ->
                        VEHICLE_REGEX.find(message)
                            ?.let { vehicleService.getVehicle(it.value) }

                    State.EstimationDataTypes.ODOMETER -> message.toInt()
                }
            }
        } ?: firstAttempt(message)

        state.complete()?.apply {
            conversation.replyWithInfo(vehicle.content as Vehicle, odometer.content as Int)
        } ?: askForNext(conversation, state)
    }

    private fun firstAttempt(message: String): State {
        return State().apply {
            with(StringMatchExtractor(message)) {
                extract(estimatMatcher) {}
                extract(ODOMETER_REGEX) { it.toInt() }
                    .also { odometer.content = it }
                extract(VEHICLE_REGEX) { vehicleService.findVehicle(it) }
                    .also { vehicle.content = it }
            }
        }
    }

    private fun Conversation.replyWithInfo(
        vehicle: Vehicle,
        odometer: Int
    ) {
        vehicle.tankEstimate(odometer)?.run {
            sendReply(
                "Tank-estimat: \n" +
                        "Tanken er ${percentFull().formatShort()} % full. \n" +
                        "Liter igjen: ${litersFromEmpty.formatShort()} \n" +
                        "Liter til full: ${litersFromFull.formatShort()} \n" +
                        "Ca. ${distanceFromEmpty.formatShort()} ${vehicle.odometerUnit} til tom tank\n" +
                        "(Treffsikkerhet: ${accuracy.toAccuracyText()})\n"
            )
        } ?: sendReply("Klarte ikke å estimere")
    }

    class State : DataCollectingChatState<State.EstimationDataTypes>() {
        enum class EstimationDataTypes { VEHICLE, ODOMETER }

        override val collectedData = linkedMapOf(
            Pair(EstimationDataTypes.VEHICLE, QueryableDataItem("Hvilken bil? \uD83D\uDE97")),
            Pair(EstimationDataTypes.ODOMETER, QueryableDataItem("Kilometerstand? 🔢")),
        )
        val vehicle = collectedData[EstimationDataTypes.VEHICLE]!!
        val odometer = collectedData[EstimationDataTypes.ODOMETER]!!

        override fun complete(): State? {
            if (vehicle.isMissing() || odometer.isMissing()) {
                return null
            }
            return this
        }
    }
}

private fun Double.toAccuracyText(): String {
    return when {
        this == 1.0 -> "Eksakt"
        this > 0.9 -> "Bra!"
        this > 0.8 -> "Ganske bra"
        this > 0.6 -> "Helt grei"
        this > 0.4 -> "Laber"
        this > 0.2 -> "Dårlig"
        this > 0.1 -> "Skikkelig dårlig"
        else -> "Ubrukelig"
    }
}
