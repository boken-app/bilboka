package bilboka.messagebot.commands

import bilboka.core.book.Book
import bilboka.core.book.domain.BookEntry
import bilboka.core.book.toMaintenanceItem
import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.Vehicle
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.*
import kotlin.text.RegexOption.IGNORE_CASE

private val keywordRegex = Regex(
    "(?:regvedl|bytte|vedlikehold|skifte|skift|bytt|ny|nytt|nye)",
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
        conversation.withdrawClaim<State>(this)?.let {
            if (it.hasAskedForAdding) {
                if (message.saysYes()) {
                    it.thingToAdd?.apply {
                        book.addMaintenanceItem(this)
                        it.maintenanceItem.content = this
                        it.hasAskedForAdding = false
                    }
                    it.completeOrAskForMore(conversation)
                } else {
                    conversation.sendReply("Neivel")
                }
            } else {
                it.recordProvidedData(message) {
                    when (this) {
                        State.MaintenanceDataType.ODOMETER -> message.toInt()
                        State.MaintenanceDataType.VEHICLE -> vehicleRegex.find(message)
                            ?.run { vehicleService.getVehicle(value) }
                        State.MaintenanceDataType.MAINTENANCEITEM -> findMaintenanceItem(message)
                            .also { item ->
                                if (item == null)
                                    conversation.askToAddMaintenanceItem(it, message)
                            }
                    }
                }
                it.completeOrAskForMore(conversation)
            }
        } ?: firstAttempt(conversation, message)
    }

    private fun State.completeOrAskForMore(conversation: Conversation) {
        complete()?.run {
            completeMaintenance(conversation)
        }.also {
            if (it == null && !hasAskedForAdding) {
                askForNext(conversation, this)
            }
        }
    }

    private fun Conversation.askToAddMaintenanceItem(
        state: State,
        itemToAdd: String,
    ) {
        claim(
            this@MaintenanceAdder,
            state.apply {
                hasAskedForAdding = true
                thingToAdd = itemToAdd
            }
        )
        sendReply(
            "Legge til $itemToAdd som et vedlikeholdspunkt? " +
                    "(for å se liste over alle eksisterende, skriv 'vedlikehold')"
        )
    }

    private fun firstAttempt(conversation: Conversation, message: String, state: State = State()) {
        val matchRemainder = state.extractAsMuchAsPossible(message)

        state.complete()?.run {
            completeMaintenance(conversation)
        } ?: run {
            if (state.vehicle.content == null || state.odometer.content == null) {
                askForNext(conversation, state)
            } else if (matchRemainder.isReasonableMaintenanceItem()) {
                conversation.askToAddMaintenanceItem(state, matchRemainder)
            } else {
                conversation.sendReply("Dette gir ikke mening. ¯\\_(ツ)_/¯")
            }
        }
    }

    private fun State.extractAsMuchAsPossible(message: String): String {
        return StringMatchExtractor(message)
            .apply { extract(keywordRegex) {} }
            .apply { extract(ODOMETER_REGEX) { it.toInt() }.also { odometer.content = it } }
            .apply {
                extract(vehicleRegex) {
                    vehicleService.findVehicle(it)
                }.also { vehicle.content = it }
            }
            .apply {
                extract(maintenanceItemRegex) {
                    findMaintenanceItem(it)
                }.also { maintenanceItem.content = it }
            }.matchRemainder
    }

    private fun State.completeMaintenance(conversation: Conversation) {
        (vehicle.content as Vehicle).enterMaintenance(
            maintenanceItem = maintenanceItem.content as String,
            odometer = odometer.content as Int?,
            enteredBy = conversation.withWhom(),
            source = conversation.getSource()
        ).also {
            conversation.setUndoable(this@MaintenanceAdder, it)
            conversation.sendReply("Registrert ${it.maintenanceItem?.item} ved ${it.odometer}")
        }
    }

    private fun findMaintenanceItem(
        input: String,
    ): String? {
        return book.maintenanceItems().firstOrNull { item ->
            item == input.toMaintenanceItem()
        }
    }

    override fun undo(item: BookEntry) {
        item.delete()
    }

    class State : DataCollectingChatState<State.MaintenanceDataType>() {
        enum class MaintenanceDataType { VEHICLE, ODOMETER, MAINTENANCEITEM }

        var hasAskedForAdding: Boolean = false
        var thingToAdd: String? = null

        override val collectedData = linkedMapOf(
            Pair(MaintenanceDataType.VEHICLE, QueryableDataItem("Hvilken bil? \uD83D\uDE97")),
            Pair(MaintenanceDataType.ODOMETER, QueryableDataItem("Kilometerstand? 🔢", mayBeUnknown = true)),
            Pair(MaintenanceDataType.MAINTENANCEITEM, QueryableDataItem("Hva slags vedlikehold?")),
        )
        val vehicle = collectedData[MaintenanceDataType.VEHICLE]!!
        val odometer = collectedData[MaintenanceDataType.ODOMETER]!!
        val maintenanceItem = collectedData[MaintenanceDataType.MAINTENANCEITEM]!!

        override fun complete(): State? {
            if (vehicle.content == null || maintenanceItem.content == null || (odometer.content == null && !odometer.isUnknown)) {
                return null
            }
            return this
        }
    }
}

private fun String.isReasonableMaintenanceItem(): Boolean {
    return this.length >= 3 && maintenanceItemRegex.matches(this)
}
