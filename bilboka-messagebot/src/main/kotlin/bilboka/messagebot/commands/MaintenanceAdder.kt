package bilboka.messagebot.commands

import bilboka.core.book.Book
import bilboka.core.book.domain.BookEntry
import bilboka.core.book.toMaintenanceItem
import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleService
import bilboka.messagebot.Conversation
import bilboka.messagebot.commands.common.CarBookCommand
import bilboka.messagebot.commands.common.ChatState
import bilboka.messagebot.commands.common.ODOMETER_REGEX
import bilboka.messagebot.commands.common.Undoable
import kotlin.text.RegexOption.IGNORE_CASE

internal class MaintenanceAdder(
    private val book: Book,
    private val vehicleService: VehicleService,
    userService: UserService
) : CarBookCommand(userService), Undoable<BookEntry> {
    private val matcher = Regex(
        "(?:bytte|vedlikehold|skifte|skift|bytt|ny|nytt|nye)\\s+(\\w+)\\s+([\\wæøå]+([\\s-]+?[\\wæøå]+)?)\\s+($ODOMETER_REGEX)",
        IGNORE_CASE
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
                conversation.sendReply("Neivel")
                return
            }
        }

        val base = Extractor(messageToProcess, {
            Regex("(?:bytte|vedlikehold|skifte|skift|bytt|ny|nytt|nye)", IGNORE_CASE).findAll(it)
        }, {})
        base.getExtracted()

        val odoExtractor = Extractor(base.getRemaining(), {
            ODOMETER_REGEX.findAll(it)
        }) {
            it.toInt()
        }
        val odometer = odoExtractor.getExtracted()

        val vehicleExtractor = Extractor(odoExtractor.getRemaining(),
            {
                Regex("([\\wæøå]+([\\s-]+?[\\wæøå]+)?)", IGNORE_CASE)
                    .findAll(it)
            }) {
            vehicleService.findVehicle(it)
        }
        val vehicle = vehicleExtractor.getExtracted()

        val maintItemExtractor = Extractor(vehicleExtractor.getRemaining(), {
            Regex("[\\s\\wæøå-]+", IGNORE_CASE)
                .findAll(it)
        }) {
            book.maintenanceItems().firstOrNull { item ->
                item == it.toMaintenanceItem()
            }
        }

        val maintItem = maintItemExtractor.getExtracted()

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
                    thingToAdd = maintItemExtractor.getRemaining(),
                    prevMsg = messageToProcess
                )
            )
            conversation.sendReply(
                "Legge til ${maintItemExtractor.getRemaining()} som et vedlikeholdspunkt? " +
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

class Extractor<T>(
    private val stringSource: String,
    val matchResults: (String) -> Sequence<MatchResult>,
    private val extractor: (String) -> T?
) {
    private var matched: String? = null

    fun getExtracted(): T? {
        return matchResults(stringSource).firstNotNullOfOrNull {
            tryToFindMatch(extractor, it)
        }
    }

    private fun tryToFindMatch(extractor: (String) -> T?, it: MatchResult): T? {
        it.groupValues.forEach {
            val extracted = extractor(it)
            if (extracted != null) {
                matched = it
                return extracted
            }
        }
        return null
    }

    fun getRemaining(): String {
        return matched?.let { stringSource.lowercase().split(it).joinToString("").trim() } ?: stringSource
    }
}
