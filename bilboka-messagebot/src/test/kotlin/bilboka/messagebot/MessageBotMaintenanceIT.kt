package bilboka.messagebot;

import org.junit.jupiter.api.Test

class MessageBotMaintenanceIT : AbstractMessageBotIT() {

    @Test
    fun canGetMaintenanceOptions() {
        processMessagaAndAssertReply(
            message = "bytte bremseklosser xc70 45677",
            reply = { it.contains("Legge til bremseklosser som et vedlikeholdspunkt?") },
        )
        processMessagaAndAssertReply(
            message = "ja",
            reply = { it.contains("Registrert") },
        )
    }

}
