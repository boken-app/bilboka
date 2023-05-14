package bilboka.messagebot.commands

import bilboka.core.book.entryClosestTo
import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.OdometerUnit.KILOMETERS
import bilboka.core.vehicle.domain.Vehicle
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.CarBookCommand
import bilboka.messagebot.format
import bilboka.messagebot.formatAsDate
import java.time.LocalDateTime.now

internal class VehicleInfo(
    private val vehicleService: VehicleService,
    userService: UserService
) : CarBookCommand(userService) {
    private val matcher = Regex(
        "(inf|info|kjøretøyinfo)\\s+([\\wæøå]+([\\s-]+?[\\wæøå]+)?)",
        RegexOption.IGNORE_CASE
    )

    override fun isMatch(message: String): Boolean {
        return matcher.containsMatchIn(message)
    }

    override fun execute(conversation: Conversation, message: String) {
        val values = matcher.find(message)!!.groupValues
        val vehicleName = values[2]

        vehicleService.getVehicle(vehicleName).apply {
            replyWithInfo(this, conversation)
        }
    }

    private fun replyWithInfo(
        vehicle: Vehicle,
        conversation: Conversation
    ) {
        conversation.sendReply(
            "\uD83D\uDE97 \nBil-navn: ${vehicle.name} \n" +
                    "Alternative navn: ${vehicle.nicknames.joinToString(", ")} \n" +
                    "Registreringsnummer: ${vehicle.tegnkombinasjonNormalisert ?: "(ukjent)"} \n" +
                    "Siste forbruk: ${
                        vehicle.lastConsumptionEstimate()?.litersPer10Km()
                            ?.let { "${it.format()} liter per mil" } ?: "(mangler data)"
                    } \n" +
                    "Distansemåleenhet: ${vehicle.odometerUnit} \n" +
                    "Tankvolum: ${vehicle.tankVolume?.let { "$it liter" } ?: "(ukjent)"} \n" +
                    "Drivstofftype: ${vehicle.fuelType ?: "(ukjent)"} \n" +
                    "Antall oppføringer: ${vehicle.bookEntries.count()} \n" +
                    "Sist registrert km-stand: ${vehicle.lastOdometer() ?: "-"} \n" +
                    "Kjørt siste år: ${getDistanceLastYear(vehicle)}"
        )
        conversation.replyWithOptions(
            "Hente mer data fra Autosys?",
            "autosys-data ${vehicle.name}" to "Ja! 🚙"
        )
    }

    private fun getDistanceLastYear(vehicle: Vehicle): String {
        return vehicle.bookEntries.toList()
            .entryClosestTo(now().minusYears(1)) { it.odometer != null }
            ?.let {
                val diff = vehicle.lastOdometer()?.minus(it.odometer!!)
                if (vehicle.odometerUnit == null) {
                    "(mangler enhet)"
                } else if (diff == null) {
                    "(mangler data)"
                } else {
                    "${(vehicle.odometerUnit!!.convertToKilometers(diff))} km " +
                            (if (vehicle.odometerUnit != KILOMETERS) "/ $diff ${vehicle.odometerUnit} " else "") +
                            "(siden ${it.dateTime.formatAsDate()})"
                }
            } ?: "(mangler data)"
    }

}
