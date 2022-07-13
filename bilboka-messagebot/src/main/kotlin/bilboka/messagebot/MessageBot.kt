package bilboka.messagebot

import bilboka.messagebot.commands.AddFuelRecord
import bilboka.messagebot.commands.Helper
import bilboka.messagebot.commands.SmallTalk
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MessageBot {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var carBookExecutor: CarBookExecutor

    private val commandRegistry by lazy {
        setOf(
            AddFuelRecord(carBookExecutor),
            SmallTalk(),
            Helper()
        )
    }

    fun processMessage(message: String): String {
        logger.info("Mottok melding $message")

        commandRegistry.forEach {
            if (it.isMatch(message)) {
                return it.execute(message)
            }
        }

        return "Forstod ikke helt hva du mente, prøv igjen."
    }
}
