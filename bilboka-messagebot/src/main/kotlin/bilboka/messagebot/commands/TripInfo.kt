package bilboka.messagebot.commands

import bilboka.core.TripService
import bilboka.core.trips.domain.Trip
import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.Vehicle
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.CarBookCommand
import bilboka.messagebot.formatAsDate

internal class TripInfo(
    private val vehicleService: VehicleService,
    private val tripService: TripService,
    userService: UserService
) : CarBookCommand(userService) {
    private val turinfoMatcher = Regex(
        "(aktiv tur)\\s+([\\wæøå]+([\\s-]+?[\\wæøå]+)?)",
        RegexOption.IGNORE_CASE
    )

    override fun isMatch(message: String): Boolean {
        return turinfoMatcher.containsMatchIn(message)
    }

    override fun execute(conversation: Conversation, message: String) {
        val values = turinfoMatcher.find(message)!!.groupValues
        val vehicleName = values[2]

        vehicleService.getVehicle(vehicleName).apply {
            conversation.replyWithTripInfo(this, tripService.getActiveTrip(this))
        }
    }
}

private fun Conversation.replyWithTripInfo(vehicle: Vehicle, activeTrip: Trip?) {
    activeTrip?.run {
        sendReply(
            "\uD83D\uDEE3\uFE0F Aktiv tur '${activeTrip.tripName}' for ${vehicle.name} startet ved " +
                    "${activeTrip.odometerStart} ${vehicle.odometerUnit?.displayValue ?: ""}, " +
                    "${activeTrip.dateTimeStart.formatAsDate()}."
        )
    } ?: sendReply("Ingen aktiv tur for ${vehicle.name}")
}
