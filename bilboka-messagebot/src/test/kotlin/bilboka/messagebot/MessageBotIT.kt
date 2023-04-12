package bilboka.messagebot;

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MessageBotIT : AbstractMessageBotIT() {

    @Test
    fun canSayHei() {
        processMessagaAndAssertReply(
            message = "Hei",
            reply = "Hei"
        )
    }

    @Test
    fun canInfoWithNorwegianLetters() {
        processMessagaAndAssertReply(
            message = "Info blå testbil",
            reply = { it.contains("Bil-navn: blå testbil") },
        )
    }

    @Test
    fun reportGeneration() {
        processMessagaAndAssertReply(
            message = "tnk xc70 56789 34l 456kr",
            reply = { it.contains("Registrert tanking") }
        )
        skipFullTankQuestion()
        messageBot.processMessage("rapport xc70", validSender)

        Assertions.assertThat(testMessenger.fileSent).isNotNull
        Assertions.assertThat(testMessenger.recipient).isEqualTo(validSender)
    }

    @Nested
    inner class UserTests {
        @Test
        fun sendRegisterRequestRegisteredUser_saysAlreadyRegisteredAndIsReadyForOtherStuff() {
            processMessagaAndAssertReply(
                message = "registrer",
                reply = { it.contains("Du er allerede registrert") },
            )
            processMessagaAndAssertReply(
                message = "hei",
                reply = "Hei"
            )
        }

        @Test
        fun sendRegisterRequestUnregisteredUser_canRegister() {
            processMessagaAndAssertReply(
                message = "registrer",
                reply = { it.contains("Klar for registrering! Skriv din hemmelige kode") },
                sender = "3333"
            )
            processMessagaAndAssertReply(
                message = keyForNewUser,
                reply = { it.contains("Du er registrert!") },
                sender = "3333"
            )
            processMessagaAndAssertReply(
                message = "hei",
                reply = "Hei",
                sender = "3333"
            )
        }

        @Test
        fun usersCanRunRegisteringIndependently() {
            processMessagaAndAssertReply(
                message = "registrer",
                sender = "238845",
                reply = { it.contains("Klar for registrering! Skriv din hemmelige kode") }
            )
            processMessagaAndAssertReply(
                message = "registrer",
                sender = "838845",
                reply = { it.contains("Klar for registrering! Skriv din hemmelige kode") }
            )
            processMessagaAndAssertReply(
                message = "hei",
                reply = "Hei"
            )
        }

        @Test
        fun brukerinfo() {
            processMessagaAndAssertReply(
                message = "brukerinfo",
                reply = "Du er registrert! \n" +
                        "Brukernavn: tester_user"
            )
        }
    }

}
