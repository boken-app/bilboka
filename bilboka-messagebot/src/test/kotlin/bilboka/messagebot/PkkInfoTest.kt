package bilboka.messagebot

import bilboka.core.book.domain.BookEntry
import bilboka.core.vehicle.domain.FuelType
import bilboka.core.vehicle.domain.Vehicle
import bilboka.integration.autosys.dto.PeriodiskKjoretoyKontroll
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

class PkkInfoTest : AbstractMessageBotTest() {
    private val vehicle = vehicle(name = "En Testbil", fuelType = FuelType.DIESEL)

    @Test
    fun getInfoWhenUpdated_getsInfo() {
        val lastPkkDate = LocalDateTime.now().minusDays(100)
        mockWithLastPKK(vehicle, lastPkkDate)
        every { vehicleService.getPKKFromAutosys(any()) } returns mockk<PeriodiskKjoretoyKontroll>().apply {
            every { sistGodkjent } returns lastPkkDate.toLocalDate()
        }

        messagebot.processMessage("PKK testbil", registeredSenderID)

        verify {
            botMessenger.sendMessage(
                message = match { msg ->
                    msg.contains("Registrert siste godkjente EU-kontroll") &&
                            msg.contains(lastPkkDate.formatAsDate())
                },
                registeredSenderID
            )
        }
    }

    @Test
    fun getInfoWhenNoneFromAutosys_getsInfo() {
        val lastPkkDate = LocalDateTime.now().minusDays(100)
        mockWithLastPKK(vehicle, lastPkkDate)
        every { vehicleService.getPKKFromAutosys(any()) } returns null

        messagebot.processMessage("PKK testbil", registeredSenderID)

        verify {
            botMessenger.sendMessage(
                message = match { msg ->
                    msg.contains("Registrert siste godkjente EU-kontroll") &&
                            msg.contains(lastPkkDate.formatAsDate())
                },
                registeredSenderID
            )
        }
    }

    @Test
    fun getInfoWhenNewerFromAutosys_asksForUpdating() {
        val lastPkkDateBilboka = LocalDateTime.now().minusYears(2)
        val lastPkkDateAutosys = LocalDate.now().minusDays(10)
        mockWithLastPKK(vehicle, lastPkkDateBilboka)
        every { vehicleService.getPKKFromAutosys(any()) } returns mockk<PeriodiskKjoretoyKontroll>().apply {
            every { sistGodkjent } returns lastPkkDateAutosys
        }

        messagebot.processMessage("PKK testbil", registeredSenderID)

        verify {
            botMessenger.sendMessage(
                message = match { msg ->
                    msg.contains("Fant nyere i Autosys") &&
                            msg.contains(lastPkkDateBilboka.formatAsDate()) &&
                            msg.contains(lastPkkDateAutosys.format())
                },
                registeredSenderID
            )
            botMessenger.sendOptions(
                message = match { msg ->
                    msg.contains("Oppdatere med EU-godkjenning fra Autosys?")
                },
                any(),
                registeredSenderID
            )
        }
    }

    @Test
    fun noEntryFromBilbokaButFromAutosys_asksForUpdating() {
        val lastPkkDateAutosys = LocalDate.now().minusDays(10)
        mockWithLastPKK(vehicle, null)
        every { vehicleService.getPKKFromAutosys(any()) } returns mockk<PeriodiskKjoretoyKontroll>().apply {
            every { sistGodkjent } returns lastPkkDateAutosys
        }

        messagebot.processMessage("PKK testbil", registeredSenderID)

        verify {
            botMessenger.sendMessage(
                message = match { msg ->
                    msg.contains("Ingen registert EU-godkjenning i bilboka") &&
                            msg.contains("Fant PKK-dato i Autosys") &&
                            msg.contains(lastPkkDateAutosys.format())
                },
                registeredSenderID
            )
            botMessenger.sendOptions(
                message = match { msg ->
                    msg.contains("Oppdatere med EU-godkjenning fra Autosys?")
                },
                any(),
                registeredSenderID
            )
        }
    }

    @Test
    fun noEntryFromEither_repliesNone() {
        every { vehicleService.getVehicle(any()) } returns vehicle
        every { vehicle.lastPKK() } returns null
        every { vehicleService.getPKKFromAutosys(any()) } returns mockk<PeriodiskKjoretoyKontroll>().apply {
            every { sistGodkjent } returns null
        }

        messagebot.processMessage("PKK testbil", registeredSenderID)

        verify {
            botMessenger.sendMessage(
                message = match { msg ->
                    msg.contains("Ingen registert EU-godkjenning")
                },
                registeredSenderID
            )
        }
    }

    private fun mockWithLastPKK(vehicle: Vehicle, lastPkkDate: LocalDateTime?) {
        every { vehicleService.getVehicle(any()) } returns vehicle
        every { vehicle.lastPKK() } returns mockk<BookEntry>(relaxed = true).apply {
            every { dateTime } returns lastPkkDate
            every { this@apply.vehicle } returns vehicle
        }
    }
}
