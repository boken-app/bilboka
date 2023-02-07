package bilboka.messagebot

import bilboka.core.vehicle.VehicleNotFoundException
import bilboka.core.vehicle.domain.FuelType
import io.mockk.Called
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class FuelEntryAdderTest : AbstractMessageBotTest() {

    @Test
    fun sendAddFuelRequest_callsAddFuelExecutor() {
        mockVehicle("testbil")
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
        mockVehicle("testbil")
        testAddFuelRequest(
            message = "fylt testbil 55556 30.2 L 302.0 Kr",
        )
        verify {
            book.addFuelForVehicle(
                vehicleName = "testbil",
                enteredBy = registeredUser,
                dateTime = any(),
                odoReading = 55556,
                amount = 30.2,
                costNOK = 302.0,
                isFull = false,
                source = match { it.isNotEmpty() }
            )
        }
    }

    @Test
    fun sendAddFuelRequestDifferentOrder_callsAddFuelExecutor() {
        mockVehicle("testbil")
        testAddFuelRequest(
            message = "fylt testbil 55556 302.0 Kr 30.2 L",
        )
        verify {
            book.addFuelForVehicle(
                vehicleName = "testbil",
                enteredBy = registeredUser,
                dateTime = any(),
                odoReading = 55556,
                amount = 30.2,
                costNOK = 302.0,
                isFull = false,
                source = match { it.isNotEmpty() }
            )
        }
    }

    @Test
    fun sendAddFuelRequestDifferentCaseWithComma_callsAddFuelExecutor() {
        mockVehicle("XC 70")
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

    @Test
    fun sendAddFuelRequestSpecialCharacters_doesNotCallService() {
        messagebot.processMessage("Drivstoff \"\"[$] 34567 30l 300kr", registeredSenderID)
        messagebot.processMessage("Drivstoff &[$]* 34567 30l 300kr", unregisteredSenderID)
        verify {
            vehicleService wasNot Called
            book wasNot Called
        }
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
        every { vehicleService.getVehicle(any()) } throws VehicleNotFoundException("Hei!", "test-bil")
        every { vehicleService.findVehicle(any()) } throws VehicleNotFoundException("Hei!", "test-bil")

        messagebot.processMessage("Drivstoff test-bil 444 30l 300kr", registeredSenderID)

        verifySentMessage("Kjenner ikke til bil test-bil \uD83D\uDC40")
    }

    private fun testAddFuelRequest(message: String) {
        every { book.addFuelForVehicle(any(), any(), any(), any(), any(), any(), any()) } returns fuelEntry(
            vehicle = vehicle("Testbil", fuelType = FuelType.BENSIN),
            odometer = 34567,
            costNOK = 123.3,
            amount = 123.32
        )

        messagebot.processMessage(message, registeredSenderID)

        verifySentMessage("⛽ Registrert tanking av Testbil ved 34567 km: 123,32 liter for 123,3 kr, 1 kr/l")
    }

    private fun mockVehicle(name: String) {
        val vehicle = vehicle(name = name, fuelType = FuelType.DIESEL)
        every { vehicleService.getVehicle(any()) } throws VehicleNotFoundException("Fy!", name)
        every { vehicleService.findVehicle(any()) } returns null
        every { vehicleService.getVehicle(name) } returns vehicle
        every { vehicleService.findVehicle(name) } returns vehicle
    }

}
