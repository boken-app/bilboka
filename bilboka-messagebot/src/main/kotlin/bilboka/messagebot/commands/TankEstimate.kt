package bilboka.messagebot.commands

import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.Vehicle
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.CarBookCommand
import bilboka.messagebot.commands.common.ODOMETER_REGEX
import bilboka.messagebot.commands.common.StringMatchExtractor
import bilboka.messagebot.commands.common.VEHICLE_REGEX
import bilboka.messagebot.formatShort

internal class TankEstimate(
    private val vehicleService: VehicleService,
    userService: UserService
) : CarBookCommand(userService) {
    private val estimatMatcher = Regex("estimat", RegexOption.IGNORE_CASE)
    private val matcher = Regex(
        "(estimat)\\s+([\\wæøå]+([\\s-]+?[\\wæøå]+)?)\\s($ODOMETER_REGEX)",
        RegexOption.IGNORE_CASE
    )

    override fun isMatch(message: String): Boolean {
        return matcher.containsMatchIn(message)
    }

    override fun execute(conversation: Conversation, message: String) { // TODO samle verdier stegvis
        val extractor = StringMatchExtractor(message)
            .apply { extract(estimatMatcher) {} }
        val odometer = extractor
            .extract(ODOMETER_REGEX) { it.toInt() }
            .also { if (it == null) conversation.sendReply("Mangler kilometerstand") }
        val vehicle = extractor
            .extract(VEHICLE_REGEX) {
                vehicleService.findVehicle(it)
            }

        vehicle?.apply {
            odometer?.let { replyWithInfo(this, conversation, it) }
        } ?: conversation.sendReply("Fant ikke bil")
    }

    private fun replyWithInfo(
        vehicle: Vehicle,
        conversation: Conversation,
        odometer: Int
    ) {
        vehicle.tankEstimate(odometer)?.run {
            conversation.sendReply(
                "Tank-estimat: \n" +
                        "Tanken er ${percentFull().formatShort()} % full. \n" +
                        "Liter igjen: ${litersFromEmpty.formatShort()} \n" +
                        "Liter til full: ${litersFromFull.formatShort()} \n" +
                        "Ca. ${distanceFromEmpty.formatShort()} ${vehicle.odometerUnit} til tom tank\n" +
                        "(Treffsikkerhet: ${accuracy.toAccuracyText()})\n"
            )
        } ?: conversation.sendReply("Klarte ikke å estimere")
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
