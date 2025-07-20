package bilboka.messagebot.commands

import bilboka.core.book.domain.BookEntry
import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleService
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.CarBookCommand
import org.slf4j.LoggerFactory

internal class PKKChecker(
    private val vehicleService: VehicleService,
    userService: UserService
) : CarBookCommand(userService) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val matcher = Regex(
        "(pkk|eu)\\s+([\\wæøå]+([\\s-]+?[\\wæøå]+)?)",
        RegexOption.IGNORE_CASE
    )

    override fun isMatch(message: String): Boolean {
        return matcher.containsMatchIn(message)
    }

    override fun execute(conversation: Conversation, message: String) {
        val values = matcher.find(message)!!.groupValues
        val vehicleName = values[2]

        val vehicle = vehicleService.getVehicle(vehicleName)
        vehicle.lastPKK()?.apply {
            replyWithInfo(this, conversation)
        } ?: conversation.sendReply("Ingen registrert PKK for ${vehicle.name}")
    }

    private fun replyWithInfo(
        entry: BookEntry,
        conversation: Conversation
    ) {
        conversation.sendReply(
            "\uD83D\uDE97 \nSiste registrert godkjente EU-kontroll for ${entry.vehicle.name} \n" +
                    "Dato: ${entry.dateTime ?: "(ukjent)"} \n" +
                    "Kilometerstand: ${entry.odometer ?: "(ukjent)"}"
        )
    }
}
