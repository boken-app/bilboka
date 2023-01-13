package bilboka.messagebot

import bilboka.core.vehicle.VehicleNotFoundException
import bilboka.core.vehicle.domain.FuelType
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@ExtendWith(MockKExtension::class)
class FuelEntryGetterTest : AbstractMessageBotTest() {

    @Test
    fun sendGetLastEntry_repliedWithLastEntry() {
        val time = LocalDateTime.of(LocalDate.of(2020, 1, 1), LocalTime.NOON)
        val vehicle = vehicle(name = "En Testbil", fuelType = FuelType.DIESEL)
        every { book.getLastFuelEntry(any()) } returns fuelEntry(
            vehicle = vehicle, dateTime = time, odometer = 1234, amount = 30.0, costNOK = 100.0
        )

        messagebot.processMessage("Siste testbil", senderID)

        verify {
            botMessenger.sendMessage(
                message = "Siste tanking av En Testbil: 30 liter for 100 kr (3,33 kr/l) ${time.format()} ved 1234 km",
                senderID
            )
        }
    }

    @Test
    fun sendGetLastEntryWhenNoEntries_repliesSomethingUseful() {
        every { book.getLastFuelEntry(any()) } returns null

        messagebot.processMessage("Siste testbil", senderID)

        verify {
            botMessenger.sendMessage(
                "Finner ingen tankinger for testbil",
                senderID
            )
        }
    }

    @Test
    fun sendGetLastEntryWhenCarNotFound_repliesSomethingUseful() {
        every { book.getLastFuelEntry(any()) } throws VehicleNotFoundException("Ops", "bil")

        messagebot.processMessage("Siste testbil", senderID)

        verify {
            botMessenger.sendMessage(
                any(),
                senderID
            )
        }
    }

}
