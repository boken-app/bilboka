package bilboka.messagebot.commands

import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.Vehicle
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.CarBookCommand
import bilboka.messagebot.format

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
                            ?.let { "${it.format()} liter per mil" } ?: "(ukjent)"
                    } \n" +
                    "Distansemåleenhet: ${vehicle.odometerUnit} \n" +
                    "Tankvolum: ${vehicle.tankVolume ?: "(ukjent)"} \n" +
                    "Drivstofftype: ${vehicle.fuelType} \n" +
                    "Antall oppføringer: ${vehicle.bookEntries.count()} \n" +
                    "Sist registrert km-stand: ${vehicle.lastOdometer() ?: "-"}"
        )
    }

}
