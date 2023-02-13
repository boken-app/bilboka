package bilboka.messagebot.commands

import bilboka.core.book.Book
import bilboka.core.book.domain.BookEntry
import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleService
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.CarBookCommand
import bilboka.messagebot.commands.common.ChatState
import bilboka.messagebot.commands.common.ODOMETER_REGEX
import bilboka.messagebot.commands.common.Undoable

internal class MaintenanceAdder(
    private val book: Book,
    private val vehicleService: VehicleService,
    userService: UserService
) : CarBookCommand(userService), Undoable<BookEntry> {
    private val matcher = Regex(
        "(?:bytte|vedlikehold|skifte|bytt|ny|nytt)\\s+(\\w+)\\s+([\\wæøå]+([\\s-]+?[\\wæøå]+)?)\\s+($ODOMETER_REGEX)",
        RegexOption.IGNORE_CASE
    )

    override fun isMatch(message: String): Boolean {
        return matcher.containsMatchIn(message)
    }

    override fun execute(conversation: Conversation, message: String) {
        // TODO her er det mye som må ryddes!
        var messageToProcess = message
        conversation.withdrawClaim<State>(this)?.let {
            if (it.hasAskedForAdding && message.lowercase() == "ja") {
                book.addMaintenanceItem(it.thingToAdd)
                messageToProcess = it.prevMsg
            } else {
                return
            }
        }

        val matchResult = matcher.find(messageToProcess)
        val maintItem = matchResult?.groupValues?.get(1)
        val vehicle = matchResult?.groupValues?.get(2)

        if (book.maintenanceItems().contains(maintItem?.uppercase())) {
            val enteredMaintenance = vehicle?.let {
                vehicleService.getVehicle(it)
            }?.enterMaintenance(
                maintenanceItem = maintItem!!,
                odometer = ODOMETER_REGEX.find(messageToProcess)
                    ?.let { (it.groups[1] ?: it.groups[2])?.value?.toInt() },
                enteredBy = conversation.withWhom(),
                source = conversation.getSource()
            )
            conversation.setUndoable(this, enteredMaintenance!!)
            conversation.sendReply("Registrert ${enteredMaintenance.maintenanceItem} ved ${enteredMaintenance.odometer}")
        } else if (maintItem != null) {
            conversation.claim(
                this,
                State(hasAskedForAdding = true, thingToAdd = maintItem, prevMsg = messageToProcess)
            )
            conversation.sendReply(
                "Legge til $maintItem som et vedlikeholdspunkt? " +
                        "(for å se liste over alle eksisterende, skriv 'vedlikehold')"
            )
        } else {
            conversation.sendReply("Nå ble det krøll her")
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
