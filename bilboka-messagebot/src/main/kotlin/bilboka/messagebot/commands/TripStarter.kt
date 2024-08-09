package bilboka.messagebot.commands

import bilboka.core.TripService
import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.Vehicle
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.*

private val nameRegex = Regex("[\\w\\d\\sæøå?!,.-]{3,}")

internal class TripStarter(
    private val vehicleService: VehicleService,
    private val tripService: TripService,
    userService: UserService
) : CarBookCommand(userService) {
    private val startTripMatcher = Regex("start tur", RegexOption.IGNORE_CASE)

    override fun isMatch(message: String): Boolean {
        return startTripMatcher.containsMatchIn(message)
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

        if (state.askedForTripName) {
            val tripName = nameRegex.find(message)?.value ?: "(ingen navn)"
            val vehicle = state.vehicle.content as Vehicle
            val odometer = state.odometer.content as Int
            tripService.startTrip(vehicle, tripName, odometer, conversation.user)
            conversation.sendReply("Tur '$tripName' startet for ${vehicle.name} ved $odometer")
            //    conversation.sendReply("Avslutt turen igjen ved å skrive 'Avslutt tur [bil] [kilometerstand]'")
        } else {
            state.complete()?.apply {
                askedForTripName = true
                conversation.claim(this@TripStarter, this)
                conversation.sendReply("Navn på turen?")
            } ?: askForNext(conversation, state)
        }
    }

    private fun firstAttempt(message: String): State {
        return State().apply {
            with(StringMatchExtractor(message)) {
                extract(startTripMatcher) {}
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