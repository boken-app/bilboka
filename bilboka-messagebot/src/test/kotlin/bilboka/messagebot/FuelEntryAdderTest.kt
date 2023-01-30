package bilboka.messagebot

import bilboka.core.vehicle.VehicleNotFoundException
import bilboka.core.vehicle.domain.FuelType
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class FuelEntryAdderTest : AbstractMessageBotTest() {

    @Test
    fun sendAddFuelRequest_callsAddFuelExecutor() {
        testAddFuelRequest(
            message = "Drivstoff testbil 34567 30l 300kr",
        )
        verify {
            book.addFuelForVehicle(
                vehicleName = "testbil",
                enteredBy = registeredUser,
                dateTime = any(),
                odoReading = 34567,
                amount = 30.0,
                costNOK = 300.0,
                isFull = false,
                source = match { it.isNotEmpty() }
            )
        }
    }

    @Test
    fun sendAddFuelRequestDifferentCase_callsAddFuelExecutor() {
        testAddFuelRequest(
            message = "fylt en testbil 5555 30.2 L 302.0 Kr",
        )
        verify {
            book.addFuelForVehicle(
                vehicleName = "en testbil",
                enteredBy = registeredUser,
                dateTime = any(),
                odoReading = 5555,
                amount = 30.2,
                costNOK = 302.0,
                isFull = false,
                source = match { it.isNotEmpty() }
            )
        }
    }

    @Test
    fun sendAddFuelRequestDifferentCaseWithComma_callsAddFuelExecutor() {
        testAddFuelRequest(
            message = "Hei drivstoff XC 70 1234 km 30,44 l 608,80 kr.. :D",
        )
        verify {
            book.addFuelForVehicle(
                vehicleName = "XC 70",
                enteredBy = registeredUser,
                dateTime = any(),
                odoReading = 1234,
                amount = 30.44,
                costNOK = 608.80,
                isFull = false,
                source = match { it.isNotEmpty() }
            )
        }
    }

    private fun testAddFuelRequest(message: String) {
        every { book.addFuelForVehicle(any(), any(), any(), any(), any(), any(), any()) } returns fuelEntry(
            vehicle = vehicle("Testbil", fuelType = FuelType.BENSIN),
            odometer = 34567,
            costNOK = 123.3,
            amount = 123.32
        )

        messagebot.processMessage(message, registeredSenderID)

        verifySentMessage("Registrert tanking av Testbil ved 34567 km: 123,32 liter for 123,3 kr, 1 kr/l")
    }

    @Test
    fun sendAddFuelRequestForUnknownCar_answersCarUnknown() {
        every {
            book.addFuelForVehicle(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } throws VehicleNotFoundException("Hei!", "test-bil")

        messagebot.processMessage("Drivstoff test-bil 444 30l 300kr", registeredSenderID)

        verifySentMessage("Kjenner ikke til bil test-bil")
    }

}
