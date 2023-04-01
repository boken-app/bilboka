package bilboka.messagebot.commands

import bilboka.core.book.Book
import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleService
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.CarBookCommand
import bilboka.messagebot.commands.common.StringMatchExtractor
import bilboka.messagebot.commands.common.VEHICLE_REGEX

internal class ReportGetter(val book: Book, val vehicleService: VehicleService, userService: UserService) :
    CarBookCommand(userService) {
    private val rapportMatcher = Regex("rapport", RegexOption.IGNORE_CASE)
    private val maintenanceMatcher = Regex("vedlikehold|vedl", RegexOption.IGNORE_CASE)
    private val yearRegex = Regex("\\d{4}")

    override fun isMatch(message: String): Boolean {
        return rapportMatcher.containsMatchIn(message)
    }

    override fun execute(conversation: Conversation, message: String) {
        val extractor = StringMatchExtractor(message)
            .apply { extract(rapportMatcher) {} }
        val isMaintenance = extractor
            .extract(maintenanceMatcher) { } != null
        val vehicle = extractor
            .extract(VEHICLE_REGEX) {
                vehicleService.findVehicle(it)
            }
        val year = extractor
            .extract(yearRegex) { it.toInt() }

        vehicle?.run {
            if (isMaintenance) {
                replyWithReportOrNothing(
                    conversation,
                    filename = "vedlikeholdsrapport_${name.lowercase().replace(" ", "_")}"
                ) { book.getMaintenanceReport(this) }
            } else {
                replyWithReportOrNothing(
                    conversation,
                    filename = "rapport${year ?: ""}_${name.lowercase().replace(" ", "_")}"
                ) { book.getReport(this, year) }
            }
        } ?: conversation.sendReply("Fant ikke bil")
    }

    private fun replyWithReportOrNothing(conversation: Conversation, filename: String, getReport: () -> ByteArray?) {
        getReport()?.run {
            conversation.sendPdf(this, filename)
        } ?: conversation.sendReply("Ingenting å rapportere ¯\\_(ツ)_/¯")
    }
}
