package bilboka.messagebot;

import org.junit.jupiter.api.Test

class MessageBotMaintenanceIT : AbstractMessageBotIT() {

    @Test
    fun canGetMaintenanceOptions() {
        processMessagaAndAssertReply(
            message = "vedlikehold",
            reply = { it.contains("Eksisterende vedlikeholdspunkt:") },
        )
    }

}
