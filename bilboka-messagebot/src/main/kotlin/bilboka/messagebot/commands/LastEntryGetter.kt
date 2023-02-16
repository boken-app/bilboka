package bilboka.messagebot.commands

import bilboka.core.book.Book
import bilboka.core.book.domain.BookEntry
import bilboka.core.book.domain.EntryType
import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.Vehicle
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.CarBookCommand
import bilboka.messagebot.format
import bilboka.messagebot.formatAsDate

internal class LastEntryGetter(
    private val book: Book,
    private val vehicleService: VehicleService,
    userService: UserService
) : CarBookCommand(userService) {
    private val matcher = Regex(
        "siste",
        RegexOption.IGNORE_CASE
    )
    private val allMatcher = Regex(
        "siste\\s+(?:([\\wæøå-]+)\\s+)?([\\wæøå]+([\\s-]+?[\\wæøå]+)?)",
        RegexOption.IGNORE_CASE
    )
    private val vehicleMatcher = Regex(
        "siste\\s+([\\wæøå]+([\\s-]+?[\\wæøå]+)?)",
        RegexOption.IGNORE_CASE
    )

    override fun isMatch(message: String): Boolean {
        return matcher.containsMatchIn(message)
    }

    // TODO dette bør bli penere
    override fun execute(conversation: Conversation, message: String) {
        allMatcher.find(message)?.apply {

            val category = groupValues[1].normalizeAsMaintenanceItem()

            if (book.maintenanceItems().contains(category)) {
                val vehicle = vehicleService.getVehicle(groupValues[2])
                replyWithLastEntry(vehicle, vehicle.lastMaintenance(category), conversation)
            } else {
                val vehicle = vehicleMatcher.find(message)?.groupValues?.get(1)?.let {
                    vehicleService.getVehicle(it)
                }
                vehicle?.lastEntry(EntryType.FUEL)?.apply {
                    replyWithLastEntry(this.vehicle, this, conversation)
                } ?: conversation.sendReply(
                    "Finner ingen tankinger for ${vehicle?.name}"
                )
            }
        } ?: conversation.sendReply(
            "Skjønte ikke noe av det der"
        )
    }

    private fun replyWithLastEntry(
        vehicle: Vehicle,
        lastBookEntry: BookEntry?,
        conversation: Conversation
    ) {
        when (lastBookEntry?.type) {
            EntryType.FUEL -> conversation.sendReply(
                "Siste tanking av ${vehicle.name}: ${lastBookEntry.amount.format()} liter " +
                        "for ${lastBookEntry.costNOK.format()} kr (${lastBookEntry.pricePerLiter().format()} kr/l) ${
                            lastBookEntry.dateTime.formatAsDate()
                        } ved ${lastBookEntry.odometer ?: "?"} ${vehicle.odometerUnit}",
            )
            EntryType.MAINTENANCE -> conversation.sendReply(
                "Siste registrert ${lastBookEntry.maintenanceItem?.item} for ${vehicle.name}: " +
                        "${lastBookEntry.dateTime.formatAsDate()} " +
                        "ved ${lastBookEntry.odometer ?: "?"} ${vehicle.odometerUnit}${lastBookEntry.formattedComment()}",
            )
            EntryType.EVENT -> conversation.sendReply(
                "${lastBookEntry.event}: ${lastBookEntry.dateTime.formatAsDate()} " +
                        "ved ${lastBookEntry.odometer ?: "?"}${lastBookEntry.formattedComment()}"
            )
            EntryType.BASIC -> conversation.sendReply(
                "${lastBookEntry.dateTime.formatAsDate()} " +
                        "ved ${lastBookEntry.odometer ?: "?"}${lastBookEntry.formattedComment()}"
            )
            null -> conversation.sendReply(
                "Ingen registrert"
            )
        }
    }
}

private fun String.normalizeAsMaintenanceItem(): String {
    return this.replace(' ', '_')
        .replace('æ', 'e', true)
        .replace('ø', 'o', true)
        .replace('å', 'å', true)
        .uppercase()
}

private fun BookEntry.formattedComment() = this.comment?.let { " ($it)" } ?: ""