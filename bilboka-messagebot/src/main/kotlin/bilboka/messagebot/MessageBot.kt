package bilboka.messagebot

import bilboka.core.book.Book
import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleNotFoundException
import bilboka.core.vehicle.VehicleService
import bilboka.messagebot.commands.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.time.Instant.now

internal const val FALLBACK_MESSAGE =
    "Forstod ikke helt hva du mente. Prøv igjen eller skriv 'hjelp' om du trenger informasjon."

@Component
class MessageBot {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var botMessenger: BotMessenger

    @Autowired
    lateinit var vehicleService: VehicleService

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var book: Book

    private val commandRegistry by lazy {
        setOf(
            FuelEntryAdder(book, userService),
            FuelEntryGetter(book, userService),
            SmallTalk(),
            Helper(),
            VehicleInfo(vehicleService, userService),
            UserInfo(),
            RegisterUser(userService)
        )
    }

    private val conversationBank = mutableMapOf<String, Conversation>()

    fun processMessage(message: String, senderID: String) {
        logger.info("Mottok melding $message")
        try {
            DuplicateBuster.catchDuplicates(message, senderID)
            transaction { runCommands(message, senderID) }
        } catch (e: DuplicateChatMessageException) {
            botMessenger.sendMessage(
                "Nå sendte du det samme to ganger. Om det var meningen, vent 10 sekunder og send igjen.",
                senderID
            )
        } catch (e: VehicleNotFoundException) {
            botMessenger.sendMessage("Kjenner ikke til bil ${e.vehicleName}", senderID)
        } catch (e: Exception) {
            logger.error("Feil ved prosessering av melding '$message'", e)
            botMessenger.sendMessage("Det skjedde noe feil. (${e.message})", senderID)
        }
    }

    private fun runCommands(message: String, senderID: String) {
        var noMatches = true

        commandRegistry.forEach {
            if (noMatches && it.isMatch(message) && it.byValidUser(senderID)) {
                it.execute(
                    findConversationOrCreateNew(senderID),
                    message
                )
                noMatches = false
            } else {
                it.resetState()
            }
        }

        if (noMatches) {
            botMessenger.sendMessage(
                FALLBACK_MESSAGE,
                senderID
            )
        }
    }

    private fun findConversationOrCreateNew(senderID: String) =
        (conversationBank[conversationKey(senderID, botMessenger.sourceID)]
            ?: Conversation(
                user = userService.findUserByRegistration(botMessenger.sourceID, senderID),
                senderID = senderID,
                botMessenger = botMessenger
            )
                .also { conversationBank[conversationKey(senderID, botMessenger.sourceID)] = it })

    private fun conversationKey(sender: String, sourceID: String): String {
        return "$sourceID-$sender"
    }

    private fun ChatCommand.byValidUser(senderID: String) =
        this.validUser(botMessenger.sourceID, senderID)
            .also { if (!it) logger.warn("Uregistrert bruker $senderID fra ${botMessenger.sourceID} prøver å gjøre bilbok-ting") }

    object DuplicateBuster {
        private val timeout = Duration.ofSeconds(10)
        private var last: String? = null
        private var lastTime: Instant = now().minus(timeout)

        fun catchDuplicates(message: String, sender: String) {
            if (isDuplicate(message, sender)) {
                throw DuplicateChatMessageException()
            } else {
                updateLastWith(message, sender)
            }
        }

        private fun isDuplicate(message: String, sender: String) =
            last == identifier(message, sender) && now().isBefore(lastTime.plus(timeout))

        private fun updateLastWith(message: String, sender: String) {
            lastTime = now()
            last = identifier(message, sender)
        }

        private fun identifier(message: String, sender: String) = "$sender:$message"
    }

    class DuplicateChatMessageException : RuntimeException()
}
