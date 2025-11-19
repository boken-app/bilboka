package bilboka.messagebot.commands

import bilboka.core.book.Book
import bilboka.core.book.domain.BookEntry
import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.Vehicle
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.*
import bilboka.messagebot.formatAsDate
import bilboka.messagebot.orUkjent

internal class PKKUpdater(
    private val vehicleService: VehicleService,
    private val book: Book,
    userService: UserService
) : CarBookCommand(userService), Undoable<BookEntry> {
    private val matcher = Regex(
        "(oppdater-eu-fra-autosys)\\s+${VEHICLE_REGEX.pattern}",
        RegexOption.IGNORE_CASE
    )

    override fun isMatch(message: String): Boolean {
        return matcher.containsMatchIn(message)
    }

    override fun execute(conversation: Conversation, message: String) {
        conversation.withdrawClaim<State>(this)?.apply {
            if (odometer.wasJustQueried) {
                saveOdometerIfKnown(message)
            }
            this.complete()?.let {
                book.refreshPKK(
                    vehicle,
                    odometer.takeIf { !it.isUnknown }?.content as Int?,
                    conversation.user,
                    conversation.getSource()
                )?.let {
                    conversation.setUndoable(this@PKKUpdater, it)
                    conversation.sendReply("Oppdatert EU-kontroll: ${it.dateTime.formatAsDate()} ved ${it.odometer.orUkjent()} ${vehicle.odometerUnit}")
                } ?: conversation.sendReply("Ingenting å oppdatere")
            }
        } ?: queryForOdometer(message, conversation)
    }

    private fun State.saveOdometerIfKnown(message: String) {
        if (message.saysUnknown()) {
            odometer.isUnknown = true
        } else {
            with(StringMatchExtractor(message)) {
                extract(ODOMETER_REGEX) { it.toInt() }
                    .also { odometer.content = it }
            }
        }
        odometer.wasJustQueried = false
    }

    private fun queryForOdometer(
        message: String,
        conversation: Conversation
    ) {
        val values = matcher.find(message)!!.groupValues
        val vehicleName = values[2]

        val vehicle = vehicleService.getVehicle(vehicleName)

        with(State(vehicle)) {
            odometer.wasJustQueried = true
            conversation.claim(this@PKKUpdater, this)
            conversation.sendReply(odometer.query)
        }
    }

    class State(
        val vehicle: Vehicle
    ) : ChatState() {
        val odometer = QueryableDataItem("Kilometerstand? 🔢", mayBeUnknown = true)

        fun complete(): State? {
            if (odometer.isMissing() && !odometer.isUnknown) {
                return null
            }
            return this
        }
    }

    override fun undo(item: BookEntry) {
        item.delete()
    }
}
