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

    override fun isMatch(message: String): Boolean {
        return rapportMatcher.containsMatchIn(message)
    }

    override fun execute(conversation: Conversation, message: String) {
        val vehicle = StringMatchExtractor(message)
            .apply { extract(rapportMatcher) {} }
            .extract(VEHICLE_REGEX) {
                vehicleService.findVehicle(it)
            }

        vehicle?.run {
            conversation.sendPdf(book.getReport("Testrapport for $name"), "testrapport_$name")
        } ?: conversation.sendReply("Fant ikke bil")
    }
}
