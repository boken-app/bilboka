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
        processMessagaAndAssertReply(
            message = "vedlikehold",
            reply = { it.contains("BREMSEKLOSSER") },
        )
    }

    @Test
    fun canGetLastMaintenance() {
        processMessagaAndAssertReply(
            message = "bytte bremseskiver xc70 45677",
            reply = { it.contains("Legge til bremseskiver som et vedlikeholdspunkt?") },
        )
        processMessagaAndAssertReply(
            message = "ja",
            reply = { it.contains("Registrert") },
        )
        processMessagaAndAssertReply(
            message = "Siste bremseskiver xc70",
            reply = { it.contains("45677") },
        )
    }

    @Test
    fun canRegisterExistingMaintenance() {
        processMessagaAndAssertReply(
            message = "bytte vindusviskere xc70 45677",
            reply = { it.contains("Legge til vindusviskere som et vedlikeholdspunkt?") },
        )
        processMessagaAndAssertReply(
            message = "ja",
            reply = { it.contains("Registrert") },
        )
        processMessagaAndAssertReply(
            message = "bytte vindusviskere xc70 45800",
            reply = { it.contains("Registrert VINDUSVISKERE ved 45800") },
        )
    }

    @Test
    fun repliesMissingCar() {
        processMessagaAndAssertReply(
            message = "bytte vindusviskere 45677",
            reply = { it.contains("Mangler bil") },
        )
    }

    @Test
    fun repliesMissingOdo() {
        processMessagaAndAssertReply(
            message = "bytte vindusviskere xc70",
            reply = { it.contains("Mangler kilometerstand") },
        )
    }

}
