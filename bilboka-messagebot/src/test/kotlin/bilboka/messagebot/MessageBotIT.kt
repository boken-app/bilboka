package bilboka.messagebot;

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class MessageBotIT : AbstractMessageBotIT() {

    @Test
    fun sendAddFuelRequest() {
        processMessagaAndAssertReply(
            message = "Drivstoff en testbil 34567 30l 300kr",
            reply = "Registrert tanking av en testbil ved 34567 km: 30 liter for 300 kr, 10 kr/l"
        )
    }

    @Test
    fun sendAddFuelRequestDifferentCase() {
        processMessagaAndAssertReply(
            message = "fylt en testbil 5555 30.2 L 302.0 Kr",
            reply = "Registrert tanking av en testbil ved 5555 km: 30,2 liter for 302 kr, 10 kr/l"
        )
    }

    @Test
    fun sendAddFuelRequestDifferentCaseWithComma() {
        processMessagaAndAssertReply(
            message = "Hei drivstoff XC 70 1234 km 30,44 l 608,80 kr.. :D",
            reply = "Registrert tanking av xc 70 ved 1234 km: 30,44 liter for 608,8 kr, 20 kr/l"
        )
    }

    @Test
    fun sendAddFuelRequestNickname() {
        processMessagaAndAssertReply(
            message = "Hei drivstoff crosser 1235 km 30.44 l 608.80 kr",
            reply = "Registrert tanking av xc 70 ved 1235 km: 30,44 liter for 608,8 kr, 20 kr/l"
        )
    }

    @Test
    fun sendAddFuelRequestUnknownCar() {
        processMessagaAndAssertReply(
            message = "Drivstoff tullebil 34567 30l 300kr",
            reply = "Kjenner ikke til bil tullebil"
        )
    }

    @Test
    fun canSayHei() {
        processMessagaAndAssertReply(
            message = "Hei",
            reply = "Hei"
        )
    }

    @Test
    fun sendAddFuelRequestInvalidUser_pretendsToNotUnderstand() {
        processMessagaAndAssertReply(
            message = "Drivstoff en testbil 34567 30l 300kr",
            reply = FALLBACK_MESSAGE,
            sender = "5678"
        )
    }

    @Test
    fun sendGetLastFueling() {
        processMessagaAndAssertReply(
            message = "drivstoff XC 70 1234 km 30,44 l 608,80 kr",
            reply = { it.contains("Registrert tanking") }
        )
        processMessagaAndAssertReply(
            message = "Siste xc70",
            reply = { it.contains("Siste tanking av xc 70: 30,44 liter for 608,8 kr (20 kr/l)") }
        )
    }

    @Test
    fun sendGetLastFuelingInvalidUser_pretendsToNotUnderstand() {
        processMessagaAndAssertReply(
            message = "Siste xc70",
            reply = FALLBACK_MESSAGE,
            sender = "5678"
        )
    }

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
    @Disabled // TODO dette må fikses på generell basis
    fun usersCanRunRegisteringIndependently() {
        processMessagaAndAssertReply(
            message = "registrer",
            sender = "238845",
            reply = "Klar for registrering! Skriv din hemmelige kode."
        )
        processMessagaAndAssertReply(
            message = "registrer",
            sender = "838845",
            reply = "Klar for registrering! Skriv din hemmelige kode."
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

    @Test
    fun canUndoEvenOnSecondTry() {
        processMessagaAndAssertReply(
            message = "Drivstoff en testbil 35589 30l 300kr",
            reply = "Registrert tanking av en testbil ved 35589 km: 30 liter for 300 kr, 10 kr/l"
        )
        processMessagaAndAssertReply(
            message = "Drivstoff en testbil 35592 20l 200kr",
            reply = { it.contains("Registrert tanking av en testbil ved 35592 km: 20 liter for 200 kr") }
        )
        processMessagaAndAssertReply(
            message = "Anfgre",
            reply = FALLBACK_MESSAGE
        )
        processMessagaAndAssertReply(
            message = "Angre",
            reply = "Angret"
        )
        processMessagaAndAssertReply(
            message = "Siste en testbil",
            reply = { it.contains("Siste tanking av en testbil: 30 liter for 300 kr") }
        )
    }

    @Test
    fun canUndoOnlyOnce() {
        processMessagaAndAssertReply(
            message = "Drivstoff en testbil 36590 30l 300kr",
            reply = "Registrert tanking av en testbil ved 36590 km: 30 liter for 300 kr, 10 kr/l"
        )
        processMessagaAndAssertReply(
            message = "Drivstoff en testbil 36592 20l 200kr",
            reply = { it.contains("Registrert tanking av en testbil ved 36592 km: 20 liter for 200 kr") }
        )
        processMessagaAndAssertReply(
            message = "Angre",
            reply = "Angret"
        )
        processMessagaAndAssertReply(
            message = "Siste en testbil",
            reply = { it.contains("Siste tanking") }
        )
        processMessagaAndAssertReply(
            message = "Angre",
            reply = { it.contains("Ingen handling å angre") }
        )
    }

    @Test
    fun canNotUndoAfterAnotherMessage() {
        processMessagaAndAssertReply(
            message = "Drivstoff en testbil 37589 30l 300kr",
            reply = "Registrert tanking av en testbil ved 37589 km: 30 liter for 300 kr, 10 kr/l"
        )
        processMessagaAndAssertReply(
            message = "Drivstoff en testbil 37592 20l 200kr",
            reply = { it.contains("Registrert tanking av en testbil ved 37592 km: 20 liter for 200 kr") }
        )
        processMessagaAndAssertReply(
            message = "Siste en testbil",
            reply = { it.contains("Siste tanking av en testbil: 20 liter for 200 kr (10 kr/l)") }
        )
        processMessagaAndAssertReply(
            message = "Hei",
            reply = "Hei"
        )
        processMessagaAndAssertReply(
            message = "Angre",
            reply = { it.contains("Ingen handling å angre") }
        )
        processMessagaAndAssertReply(
            message = "Siste en testbil",
            reply = { it.contains("Siste tanking av en testbil: 20 liter for 200 kr (10 kr/l)") }
        )
    }

}
