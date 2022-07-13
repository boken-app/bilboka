package bilboka.messagebot

import bilboka.messagebot.commands.AddFuelRecordCommand
import bilboka.messagebot.commands.Helper
import bilboka.messagebot.commands.SmallTalk
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class MessageBot {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val commandRegistry = setOf(
        AddFuelRecordCommand(),
        SmallTalk(),
        Helper()
    )

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
