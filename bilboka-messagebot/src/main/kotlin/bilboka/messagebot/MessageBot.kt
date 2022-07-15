package bilboka.messagebot

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
    lateinit var carBookExecutor: CarBookExecutor

    private val commandRegistry by lazy {
        setOf(
            FuelRecordAdder(botMessenger, carBookExecutor),
            FuelRecordGetter(botMessenger, carBookExecutor),
            SmallTalk(botMessenger),
            Helper(botMessenger)
        )
    }

    fun processMessage(message: String, senderID: String) {
        logger.info("Mottok melding $message")
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
