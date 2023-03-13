package bilboka.messagebot

import bilboka.messagebot.commands.DEFAULT_HELP_MESSAGE
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.Test

class SmalltalkTest : AbstractMessageBotTest() {

    @Test
    fun sendHei_returnsHei() {
        messagebot.processMessage("Hei", registeredSenderID)

        verify { botMessenger.sendMessage("Hei", registeredSenderID) }
    }

    @Test
    fun sendSkjer_returnsIkkenospes() {
        messagebot.processMessage("Skjer?", registeredSenderID)

        verify { botMessenger.sendMessage("Ikke noe spes. Der?", registeredSenderID) }
    }

    @Test
    fun sendSkjerThenSomethingElse_returnsCool() {
        messagebot.processMessage("Skjer?", registeredSenderID)
        messagebot.processMessage("Holder på med noe greier", registeredSenderID)

        verifyOrder {
            botMessenger.sendMessage("Ikke noe spes. Der?", registeredSenderID)
            botMessenger.sendMessage("Cool", registeredSenderID)
        }
    }

    @Test
    fun sendSkjerThenSomethingMatcinghOtherRule_returnsOtherRuleResponse() {
        messagebot.processMessage("Skjer?", registeredSenderID)
        messagebot.processMessage("Stuff skjer", registeredSenderID)
        messagebot.processMessage("Help", registeredSenderID)

        verifyOrder {
            botMessenger.sendMessage("Ikke noe spes. Der?", registeredSenderID)
            botMessenger.sendMessage("Cool", registeredSenderID)
            botMessenger.sendMessage(DEFAULT_HELP_MESSAGE, registeredSenderID)
        }
    }

    @Test
    fun sendSkjerByDifferentPersons_returnsIndependentResponses() {
        messagebot.processMessage("Skjer?", registeredSenderID)
        messagebot.processMessage("Skjer?", unregisteredSenderID)
        messagebot.processMessage("Jada så joda", registeredSenderID)
        messagebot.processMessage("Stuff skjer", unregisteredSenderID)

        verifyOrder {
            botMessenger.sendMessage("Ikke noe spes. Der?", registeredSenderID)
            botMessenger.sendMessage("Ikke noe spes. Der?", unregisteredSenderID)
            botMessenger.sendMessage("Cool", registeredSenderID)
            botMessenger.sendMessage("Cool", unregisteredSenderID)
        }
    }

    @Test
    fun sendSomethingStrange_returnsDefaultMessage() {
        messagebot.processMessage("Her kommer en rar melding", registeredSenderID)

        verify { botMessenger.sendMessage(FALLBACK_MESSAGE, registeredSenderID) }
    }
}
