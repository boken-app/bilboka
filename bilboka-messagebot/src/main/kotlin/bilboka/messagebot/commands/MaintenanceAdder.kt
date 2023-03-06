package bilboka.messagebot.commands

import bilboka.core.book.Book
import bilboka.core.book.domain.BookEntry
import bilboka.core.book.toMaintenanceItem
import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.Vehicle
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.CarBookCommand
import bilboka.messagebot.commands.common.ChatState
import bilboka.messagebot.commands.common.ODOMETER_REGEX
import bilboka.messagebot.commands.common.Undoable
import kotlin.text.RegexOption.IGNORE_CASE

private val keywordRegex = Regex(
    "(?:bytte|vedlikehold|skifte|skift|bytt|ny|nytt|nye)",
    IGNORE_CASE
)
private val vehicleRegex = Regex("([\\wæøå]+([\\s-]+?[\\wæøå]+)?)", IGNORE_CASE)
private val maintenanceItemRegex = Regex("[\\s\\wæøå-]+", IGNORE_CASE)

internal class MaintenanceAdder(
    private val book: Book,
    private val vehicleService: VehicleService,
    userService: UserService
) : CarBookCommand(userService), Undoable<BookEntry> {
    private val matcher = keywordRegex

    override fun isMatch(message: String): Boolean {
        return matcher.containsMatchIn(message)
    }

    override fun execute(conversation: Conversation, message: String) {
        var messageToProcess = message
        conversation.withdrawClaim<State>(this)?.let {
            if (it.hasAskedForAdding && message.lowercase() == "ja") {
                book.addMaintenanceItem(it.thingToAdd)
                messageToProcess = it.prevMsg
            } else {
                conversation.sendReply("Neivel")
                return
            }
        }

        val odometer: Int?
        val vehicle: Vehicle?
        val maintItem: String?

        val extractor = Extractor(messageToProcess)

            .apply { extract(keywordRegex) {} }

            .apply { extract(ODOMETER_REGEX) { it.toInt() }.also { odometer = it } }

            .apply {
                extract(vehicleRegex) {
                    vehicleService.findVehicle(it)
                }.also { vehicle = it }
            }

            .apply {
                extract(maintenanceItemRegex) {
                    book.maintenanceItems().firstOrNull { item ->
                        item == it.toMaintenanceItem()
                    }
                }.also { maintItem = it }
            }

        if (odometer != null && maintItem != null && vehicle != null) {
            vehicle.enterMaintenance(
                maintenanceItem = maintItem,
                odometer = odometer,
                enteredBy = conversation.withWhom(),
                source = conversation.getSource()
            ).also {
                conversation.setUndoable(this, it)
                conversation.sendReply("Registrert ${it.maintenanceItem?.item} ved ${it.odometer}")
            }
        } else if (vehicle == null) {
            conversation.sendReply("Mangler bil")
        } else if (odometer == null) {
            conversation.sendReply("Mangler kilometerstand")
        } else {
            conversation.claim(
                this,
                State(
                    hasAskedForAdding = true,
                    thingToAdd = extractor.matchRemainder,
                    prevMsg = messageToProcess
                )
            )
            conversation.sendReply(
                "Legge til ${extractor.matchRemainder} som et vedlikeholdspunkt? " +
                        "(for å se liste over alle eksisterende, skriv 'vedlikehold')"
            )
        }
    }

    override fun undo(item: BookEntry) {
        item.delete()
    }

    class State(
        val hasAskedForAdding: Boolean,
        val prevMsg: String,
        val thingToAdd: String
    ) : ChatState()
}

class Extractor(
    matchSource: String,
) {
    var matchRemainder: String = matchSource

    fun <T> extract(matchRegex: Regex, extractor: (String) -> T?): T? {
        return matchRegex.findAll(matchRemainder)
            .flatMap { it.groupValues }
            .firstNotNullOfOrNull { value ->
                extractor(value).also { if (it != null) matchRemainder = getRemainingFrom(value) }
            }
    }

    private fun getRemainingFrom(match: String): String {
        return match.let { matchRemainder.lowercase().split(it).joinToString("").trim() }
    }
}
