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

    // TODO mulighet for kostnad og kommentar
    override fun execute(conversation: Conversation, message: String) {
        var messageToProcess = message
        conversation.withdrawClaim<State>(this)?.let {
            if (it.hasAskedForAdding && message.lowercase() == "ja" && it.thingToAdd != null) {
                book.addMaintenanceItem(it.thingToAdd!!)
                messageToProcess = it.prevMsg!!
            } else {
                conversation.sendReply("Neivel")
                return
            }
        }

        val state = State()

        val extractor = Extractor(messageToProcess)

            .apply { extract(keywordRegex) {} }

            .apply { extract(ODOMETER_REGEX) { it.toInt() }.also { state.odometer.content = it } }

            .apply {
                extract(vehicleRegex) {
                    vehicleService.findVehicle(it)
                }.also { state.vehicle.content = it }
            }

            .apply {
                extract(maintenanceItemRegex) {
                    book.maintenanceItems().firstOrNull { item ->
                        item == it.toMaintenanceItem()
                    }
                }.also { state.maintenanceItem.content = it }
            }

        state.complete()?.run {
            (vehicle.content as Vehicle).enterMaintenance(
                maintenanceItem = maintenanceItem.content as String,
                odometer = odometer.content as Int,
                enteredBy = conversation.withWhom(),
                source = conversation.getSource()
            ).also {
                conversation.setUndoable(this@MaintenanceAdder, it)
                conversation.sendReply("Registrert ${it.maintenanceItem?.item} ved ${it.odometer}")
            }
        } ?: run {
            if (state.vehicle.content == null) {
                conversation.sendReply("Mangler bil")
            } else if (state.odometer.content == null) {
                conversation.sendReply("Mangler kilometerstand")
            } else if (extractor.matchRemainder.isReasonableMaintenanceItem()) {
                conversation.claim(
                    this,
                    state.apply {
                        hasAskedForAdding = true
                        thingToAdd = extractor.matchRemainder
                        prevMsg = messageToProcess
                    }
                )
                conversation.sendReply(
                    "Legge til ${extractor.matchRemainder} som et vedlikeholdspunkt? " +
                            "(for å se liste over alle eksisterende, skriv 'vedlikehold')"
                )
            } else {
                conversation.sendReply("Skjønte ingenting")
            }
        }
    }

    override fun undo(item: BookEntry) {
        item.delete()
    }

    data class MaintenanceDataItem(
        val query: String,
        var content: Any? = null,
        var isUnknown: Boolean = false,
        var wasJustQueried: Boolean = false
    )

    class State : ChatState() {
        enum class MaintenanceDataType { VEHICLE, ODOMETER, MAINTENANCEITEM }

        var hasAskedForAdding: Boolean = false
        var prevMsg: String? = null
        var thingToAdd: String? = null

        val collectedData = linkedMapOf(
            Pair(MaintenanceDataType.VEHICLE, MaintenanceDataItem("Hvilken bil? \uD83D\uDE97")),
            Pair(MaintenanceDataType.ODOMETER, MaintenanceDataItem("Kilometerstand? 🔢")),
            Pair(MaintenanceDataType.MAINTENANCEITEM, MaintenanceDataItem("Hva slags vedlikehold?")),
        )
        val vehicle = collectedData[MaintenanceDataType.VEHICLE]!!
        val odometer = collectedData[MaintenanceDataType.ODOMETER]!!
        val maintenanceItem = collectedData[MaintenanceDataType.MAINTENANCEITEM]!!

        fun complete(): State? {
            if (!isDoneAskingForStuff()) {
                return null
            }
            if (vehicle.content == null || maintenanceItem.content == null || (odometer.content == null && !odometer.isUnknown)) {
                return null
            }
            return this
        }

        private fun isDoneAskingForStuff() =
            setOf(odometer).all { it.isUnknown || it.content != null }
    }
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

private fun String.isReasonableMaintenanceItem(): Boolean {
    return this.length >= 3 && maintenanceItemRegex.matches(this)
}
