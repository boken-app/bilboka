package bilboka.messagebot

import bilboka.core.domain.book.Book
import bilboka.core.domain.book.FuelRecord
import bilboka.core.domain.vehicle.FuelType
import bilboka.core.domain.vehicle.Vehicle
import bilboka.core.vehicle.VehicleNotFoundException
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@ExtendWith(MockKExtension::class)
class FuelRecordGetterTest : AbstractMessageBotTest() {

    @Test
    fun sendGetLastRecord_repliedWithLastRecord() {
        val time = LocalDateTime.of(LocalDate.of(2020, 1, 1), LocalTime.NOON)
        val book = Book(
            Vehicle(name = "En Testbil", fuelType = FuelType.DIESEL)
        )
        book.addRecord(
            FuelRecord(
                dateTime = time, odometer = 1234, amount = 30.0, costNOK = 100.0, fuelType = FuelType.DIESEL
            )
        )
        every { carBookExecutor.getBookForVehicle(any()) } returns book

        messagebot.processMessage("Siste testbil", senderID)

        verify {
            botMessenger.sendMessage(
                message = "Siste tanking av En Testbil: 30 liter for 100 kr (3,33 kr/l) ${time.format()} ved 1234 km",
                senderID
            )
        }
        confirmVerified(botMessenger)
    }

    @Test
    fun sendGetLastRecordWhenNoRecords_repliesSomethingUseful() {
        every { carBookExecutor.getBookForVehicle(any()) } returns Book(
            Vehicle(
                name = "En Testbil",
                fuelType = FuelType.DIESEL
            )
        )

        messagebot.processMessage("Siste testbil", senderID)

        verify {
            botMessenger.sendMessage(
                "Finner ingen tankinger for En Testbil",
                senderID
            )
        }
        confirmVerified(botMessenger)
    }

    @Test
    fun sendGetLastRecordWhenCarNotFound_repliesSomethingUseful() {
        every { carBookExecutor.getBookForVehicle(any()) } throws VehicleNotFoundException("Ops", "bil")

        messagebot.processMessage("Siste testbil", senderID)

        verify {
            botMessenger.sendMessage(
                any(),
                senderID
            )
        }
        confirmVerified(botMessenger)
    }

}
