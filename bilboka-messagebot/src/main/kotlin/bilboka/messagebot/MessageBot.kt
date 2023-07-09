package bilboka.messagebot

import bilboka.core.ImpossibleBilbokaActionException
import bilboka.core.book.Book
import bilboka.core.user.UserService
import bilboka.core.vehicle.VehicleNotFoundException
import bilboka.core.vehicle.VehicleService
import bilboka.messagebot.commands.*
import bilboka.messagebot.commands.common.ChatCommand
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.sql.Connection

internal const val FALLBACK_MESSAGE =
    "Forstod ikke helt hva du mente. Prøv igjen eller skriv 'hjelp' om du trenger informasjon."

@Component
class MessageBot {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var botMessenger: BotMessenger

    @Autowired
    private lateinit var vehicleService: VehicleService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var book: Book

    private val commandRegistry by lazy {
        setOf(
            FuelEntryAdder(book, vehicleService, userService),
            LastEntryGetter(book, vehicleService, userService),
            MaintenanceItems(book, userService),
            MaintenanceAdder(book, vehicleService, userService),
            FuelPriceStatistics(book, userService),
            SmallTalk(),
            Helper(),
            VehicleInfo(vehicleService, userService),
            VehicleInfoAutosys(vehicleService, userService),
            VehicleInfoDekkOgFelg(vehicleService, userService),
            TankEstimate(vehicleService, userService),
            ReportGetter(book, vehicleService, userService),
            UserInfo(),
            RegisterUser(userService),
            UndoLast(userService),
        )
    }

    fun processMessage(message: String, senderID: String) {
        logger.debug("[meldingslogg] Mottok melding '$message'")
        try {
            val conversation = findConversationOrInitiateNew(senderID)
            conversation.validate(message)
            transaction(Connection.TRANSACTION_SERIALIZABLE, 2) { runCommands(message, conversation) }
        } catch (e: StopRepeatingYourselfException) {
            botMessenger.sendMessage(
                "Nå sendte du det samme to ganger. 🧐 Om det var meningen, send på nytt etter 10 sekunder.",
                senderID
            )
        } catch (e: VehicleNotFoundException) {
            botMessenger.sendMessage("\uD83D\uDC40 Kjenner ikke til bil ${e.vehicleName}", senderID)
        } catch (e: ImpossibleChatActionException) {
            botMessenger.sendMessage("🤖 Gikk ikke! ${e.message}", senderID)
        } catch (e: ImpossibleBilbokaActionException) {
            botMessenger.sendMessage("Gikk ikke! ${e.message}", senderID)
        } catch (e: Exception) {
            logger.error("Feil ved prosessering av melding '$message'", e)
            botMessenger.sendMessage("Det skjedde noe feil. 😵 (${e.message})", senderID)
        }
    }

    private fun runCommands(message: String, conversation: Conversation) {
        var noMatches = true

        commandRegistry.forEach {
            if (conversation.claimedBy(it) ||
                (noMatches && it.isMatch(message) && it.byValidUser(conversation.senderID))
            ) {
                logger.debug("Matchet chatregel: ${it.javaClass.name}")
                if (it !is UndoLast) {
                    conversation.resetUndoable()
                }
                it.execute(
                    conversation,
                    message
                )
                noMatches = false
            }
        }

        if (noMatches) {
            logger.debug("Matchet ingen chatregel")
            // TODO prøve å legge til som kommentar
            botMessenger.sendMessage(
                FALLBACK_MESSAGE,
                conversation.senderID
            )
        }
    }

    private fun ChatCommand.byValidUser(senderID: String): Boolean {
        return this.validUser(botMessenger.sourceID, senderID)
            .also { if (!it) logger.warn("Uregistrert bruker $senderID fra ${botMessenger.sourceID} prøver å gjøre bilbok-ting") }
    }

    private fun findConversationOrInitiateNew(senderID: String): Conversation {
        logger.debug("${ConversationBank.size()} aktive samtaler")
        return ConversationBank.find(senderID, botMessenger.sourceID)
            ?: ConversationBank.initiate(
                Conversation(
                    user = userService.findUserByRegistration(botMessenger.sourceID, senderID),
                    senderID = senderID,
                    botMessenger = botMessenger
                ).also { logger.debug("Startet ny samtale med ${it.senderID} (${if (it.user != null) "kjent" else "ukjent"} bruker)") }
            )
    }

    fun reset() {
        ConversationBank.reset()
    }

    private object ConversationBank {
        private val conversations = mutableMapOf<String, Conversation>()

        fun find(sender: String, sourceID: String): Conversation? {
            return conversations[key(sender, sourceID)]
        }

        fun initiate(conversation: Conversation): Conversation {
            conversations[key(conversation.senderID, conversation.getSource())] = conversation
            return conversation
        }

        private fun key(sender: String, sourceID: String): String {
            return "$sourceID-$sender"
        }

        fun reset() {
            conversations.clear()
        }

        fun size(): Int {
            return conversations.size
        }
    }

}
