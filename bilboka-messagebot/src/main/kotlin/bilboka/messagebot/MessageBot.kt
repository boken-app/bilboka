package bilboka.messagebot

import bilboka.core.Book
import bilboka.core.vehicle.VehicleNotFoundException
import bilboka.messagebot.commands.FuelRecordAdder
import bilboka.messagebot.commands.FuelRecordGetter
import bilboka.messagebot.commands.Helper
import bilboka.messagebot.commands.SmallTalk
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

internal const val DEFAULT_MESSAGE =
    "Forstod ikke helt hva du mente. Prøv igjen eller skriv 'hjelp' om du trenger informasjon."

@Component
class MessageBot {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var botMessenger: BotMessenger

    @Autowired
    lateinit var book: Book

    private val commandRegistry by lazy {
        setOf(
            FuelRecordAdder(botMessenger, book),
            FuelRecordGetter(botMessenger, book),
            SmallTalk(botMessenger),
            Helper(botMessenger)
        )
    }

    fun processMessage(message: String, senderID: String) {
        logger.info("Mottok melding $message")
        try {
            runCommands(message, senderID)
        } catch (e: VehicleNotFoundException) {
            botMessenger.sendMessage("Kjenner ikke til bil ${e.vehicleName}", senderID)
        } catch (e: RuntimeException) {
            logger.error("Feil ved prosessering av melding '$message'", e)
            botMessenger.sendMessage("Det skjedde noe feil. (${e.message})", senderID)
        }
    }

    private fun runCommands(message: String, senderID: String) {
        var noMatches = true

        commandRegistry.forEach {
            if (noMatches && it.isMatch(message)) {
                it.execute(senderID, message)
                noMatches = false
            } else {
                it.resetState()
            }
        }

        if (noMatches) {
            botMessenger.sendMessage(
                DEFAULT_MESSAGE,
                senderID
            )
        }
    }
}
