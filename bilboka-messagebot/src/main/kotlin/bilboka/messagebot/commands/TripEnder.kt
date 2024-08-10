package bilboka.messagebot.commands

import bilboka.core.TripService
import bilboka.core.trips.domain.Trip
import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.Vehicle
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.*

internal class TripEnder(
    private val vehicleService: VehicleService,
    private val tripService: TripService,
    userService: UserService
) : CarBookCommand(userService) {
    private val endTripMatcher = Regex("avslutt tur", RegexOption.IGNORE_CASE)

    override fun isMatch(message: String): Boolean {
        return endTripMatcher.containsMatchIn(message)
    }

    override fun execute(conversation: Conversation, message: String) {
        val state = conversation.withdrawClaim<State>(this)?.apply {
            recordProvidedData(message) {
                when (this) {
                    State.TripStartDataTypes.VEHICLE ->
                        VEHICLE_REGEX.find(message)
                            ?.let { vehicleService.getVehicle(it.value) }

                    State.TripStartDataTypes.ODOMETER -> message.toInt()
                }
            }
        } ?: firstAttempt(message)

        state.complete()?.apply {
            val vehicle = state.vehicle.content as Vehicle
            val odometer = state.odometer.content as Int
            val endedTrip = tripService.endCurrentTrip(vehicle, odometer, conversation.user)
            conversation.replyWithTripStats(vehicle, endedTrip)

        } ?: askForNext(conversation, state)
    }

    private fun Conversation.replyWithTripStats(vehicle: Vehicle, endedTrip: Trip?) {
        endedTrip?.apply {
            val odoDiff = odometerEnd!! - odometerStart
            val kilometers = vehicle.odometerUnit?.convertToKilometers(odoDiff) ?: odoDiff
            sendReply(
                "Avsluttet tur '$tripName'. " +
                        "Kjørt $kilometers km og brukt ca. ${
                            vehicle.consumptionLastKm(kilometers)?.amountPerDistanceUnit?.times(
                                odoDiff.toDouble()
                            ) ?: "(ukjent)"
                        } liter drivstoff"
            )
        } ?: sendReply("Ingen tur å avslutte for ${vehicle.name}")
    }

    private fun firstAttempt(message: String): State {
        return State().apply {
            with(StringMatchExtractor(message)) {
                extract(endTripMatcher) {}
                extract(ODOMETER_REGEX) { it.toInt() }
                    .also { odometer.content = it }
                extract(VEHICLE_REGEX) { vehicleService.findVehicle(it) }
                    .also { vehicle.content = it }
            }
        }
    }

    class State : DataCollectingChatState<State.TripStartDataTypes>() {
        enum class TripStartDataTypes { VEHICLE, ODOMETER }

        override val collectedData = linkedMapOf(
            Pair(TripStartDataTypes.VEHICLE, QueryableDataItem("Hvilken bil? \uD83D\uDE97")),
            Pair(TripStartDataTypes.ODOMETER, QueryableDataItem("Kilometerstand? 🔢")),
        )
        val vehicle = collectedData[TripStartDataTypes.VEHICLE]!!
        val odometer = collectedData[TripStartDataTypes.ODOMETER]!!
        var askedForTripName: Boolean = false

        override fun complete(): State? {
            if (vehicle.isMissing() || odometer.isMissing()) {
                return null
            }
            return this
        }
    }
}

