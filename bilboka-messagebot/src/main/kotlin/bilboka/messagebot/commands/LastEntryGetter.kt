package bilboka.messagebot.commands

import bilboka.core.book.Book
import bilboka.core.book.domain.BookEntry
import bilboka.core.book.domain.EntryType
import bilboka.core.book.toMaintenanceItem
import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.Vehicle
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.CarBookCommand
import bilboka.messagebot.commands.common.StringMatchExtractor
import bilboka.messagebot.commands.common.VEHICLE_REGEX
import bilboka.messagebot.format
import bilboka.messagebot.formatAsDate

internal class LastEntryGetter(
    private val book: Book,
    private val vehicleService: VehicleService,
    userService: UserService
) : CarBookCommand(userService) {
    private val keywordMatcher = Regex("siste", RegexOption.IGNORE_CASE)
    private val vehicleRegex = VEHICLE_REGEX
    private val maintenanceItemRegex = Regex("([\\wæøå]+(?:[\\s-][\\wæøå]+)*)", RegexOption.IGNORE_CASE)

    override fun isMatch(message: String): Boolean {
        return keywordMatcher.containsMatchIn(message)
    }

    override fun execute(conversation: Conversation, message: String) {
        var vehicle: Vehicle?
        var maintenanceItem: String? = null

        StringMatchExtractor(message)
            .apply { extract(keywordMatcher) {} }
            .apply {
                extract(vehicleRegex) {
                    vehicleService.findVehicle(it)
                }.also { vehicle = it }
            }
            .apply {
                extract(maintenanceItemRegex) {
                    it.takeIf { book.maintenanceItems().contains(it.toMaintenanceItem()) }
                }?.also { maintenanceItem = it.toMaintenanceItem() }
            }

        vehicle?.apply {
            maintenanceItem?.let {
                replyWithLastEntry(this, lastMaintenance(it), conversation)
            } ?: conversation.replyWithLastFuel(this)
        } ?: conversation.sendReply("Ukjent bil")
    }

    private fun Conversation.replyWithLastFuel(vehicle: Vehicle) {
        vehicle.lastEntry(EntryType.FUEL)?.apply {
            replyWithLastEntry(this.vehicle, this, this@replyWithLastFuel)
        } ?: sendReply(
            "Finner ingen tankinger for ${vehicle?.name}"
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
                        lastBookEntry.let { if (it.isFullTank == true) "(full) " else "" } +
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

private fun BookEntry.formattedComment() = this.comment?.let { " ($it)" } ?: ""