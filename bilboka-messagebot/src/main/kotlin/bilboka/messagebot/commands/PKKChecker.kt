package bilboka.messagebot.commands

import bilboka.core.book.domain.BookEntry
import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleService
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.CarBookCommand
import bilboka.messagebot.commands.common.VEHICLE_REGEX
import bilboka.messagebot.format
import bilboka.messagebot.formatAsDate

internal class PKKChecker(
    private val vehicleService: VehicleService,
    userService: UserService
) : CarBookCommand(userService) {
    private val matcher = Regex(
        "(pkk|eu)\\s+${VEHICLE_REGEX.pattern}",
        RegexOption.IGNORE_CASE
    )

    override fun isMatch(message: String): Boolean {
        return matcher.containsMatchIn(message)
    }

    override fun execute(conversation: Conversation, message: String) {
        val values = matcher.find(message)!!.groupValues
        val vehicleName = values[2]

        val vehicle = vehicleService.getVehicle(vehicleName)
        val lastPKK = vehicle.lastPKK()

        val lastPkkBilboka = lastPKK?.dateTime?.toLocalDate()
        val lastPkkAutosys = vehicleService.getPKKFromAutosys(vehicleName)?.sistGodkjent

        if (lastPkkBilboka != null && lastPkkAutosys != null) {
            if (lastPkkAutosys.isAfter(lastPkkBilboka.plusMonths(1))) {
                conversation.sendReply(
                    "EU-kontroll registrert i bilboka for ${vehicle.name}: ${lastPkkBilboka.format()}. " +
                            "Fant nyere i Autosys: ${lastPkkAutosys.format()}"
                )
                conversation.replyWithOptions(
                    "Oppdatere med EU-godkjenning fra Autosys?",
                    "oppdater-eu-fra-autosys ${vehicle.name}" to "Ja! 🚙"
                )
            } else {
                replyWithInfo(lastPKK, conversation)
            }
        } else if (lastPkkAutosys != null) {
            conversation.sendReply(
                "Ingen registert EU-godkjenning i bilboka for ${vehicle.name}. " +
                        "Fant PKK-dato i Autosys: ${lastPkkAutosys.format()}"
            )
            conversation.replyWithOptions(
                "Oppdatere med EU-godkjenning fra Autosys?",
                "oppdater-eu-fra-autosys ${vehicle.name}" to "Ja! 🚙"
            )
        } else {
            lastPKK?.apply {
                replyWithInfo(this, conversation)
            } ?: conversation.sendReply("Ingen registert EU-godkjenning for ${vehicle.name}")
        }
    }

    private fun replyWithInfo(
        entry: BookEntry,
        conversation: Conversation
    ) {
        conversation.sendReply(
            "📃 \nRegistrert siste godkjente EU-kontroll for ${entry.vehicle.name}: \n" +
                    "${entry.dateTime.formatAsDate()} ved " +
                    (entry.odometer?.let { "$it ${entry.vehicle.odometerUnit ?: ""}" } ?: "(ukjent)")
        )
    }
}
