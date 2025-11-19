package bilboka.messagebot

import bilboka.core.book.domain.BookEntry
import bilboka.core.vehicle.domain.FuelType
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class PkkUpdaterTest : AbstractMessageBotTest() {
    private val vehicle = vehicle(name = "En Testbil", fuelType = FuelType.DIESEL)

    @Test
    fun updateInfoWhenNothingToDo_repliesUpdated() {
        every { vehicleService.getVehicle(any()) } returns vehicle
        every { book.refreshPKK(any(), any(), any(), any()) } returns null

        messagebot.processMessage("oppdater-eu-fra-autosys testbil", registeredSenderID)
        verifySentMessageContains("Kilometerstand?")

        messagebot.processMessage("123345", registeredSenderID)
        verifySentMessage("Ingenting å oppdatere")
    }

    @Test
    fun updateInfo_asksForOdoAndUpdates() {
        every { vehicleService.getVehicle(any()) } returns vehicle

        val oppdatertDato = LocalDateTime.now().minusDays(4)
        val kilometerstand = 123345
        every { book.refreshPKK(any(), any(), any(), any()) } returns mockk<BookEntry>().apply {
            every { dateTime } returns oppdatertDato
            every { odometer } returns kilometerstand
        }

        messagebot.processMessage("oppdater-eu-fra-autosys testbil", registeredSenderID)
        verifySentMessageContains("Kilometerstand?")

        messagebot.processMessage(kilometerstand.toString(), registeredSenderID)
        verifySentMessageContains("Oppdatert EU-kontroll:")
        verifySentMessageContains(oppdatertDato.formatAsDate())
        verifySentMessageContains("$kilometerstand km")

        verify {
            book.refreshPKK(vehicle, 123345, registeredUser, messengerSourceID)
        }
    }

    @Test
    fun updateInfo_worksWithUnknownOdo() {
        every { vehicleService.getVehicle(any()) } returns vehicle

        every { book.refreshPKK(any(), any(), any(), any()) } returns mockk(relaxed = true)

        messagebot.processMessage("oppdater-eu-fra-autosys testbil", registeredSenderID)
        verifySentMessageContains("Kilometerstand?")

        messagebot.processMessage("Ukjent", registeredSenderID)
        verifySentMessageContains("Oppdatert")

        verify {
            book.refreshPKK(vehicle, null, registeredUser, messengerSourceID)
        }
    }

    @Test
    fun updateInfo_odoNotANumber() {
        every { vehicleService.getVehicle(any()) } returns vehicle

        every { book.refreshPKK(any(), any(), any(), any()) } returns mockk()

        messagebot.processMessage("oppdater-eu-fra-autosys testbil", registeredSenderID)
        verifySentMessageContains("Kilometerstand?")

        messagebot.processMessage("heisann", registeredSenderID)

        verify {
            book wasNot Called
        }
    }

}
