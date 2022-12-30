package bilboka.messagebot

import bilboka.core.vehicle.VehicleNotFoundException
import bilboka.core.vehicle.domain.FuelType
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class FuelRecordAdderTest : AbstractMessageBotTest() {

    @Test
    fun sendAddFuelRequest_callsAddFuelExecutor() {
        testAddFuelRequest(
            message = "Drivstoff testbil 34567 30l 300kr",
            name = "testbil"
        )
        verify { book.addFuelForVehicle("testbil", 34567, 30.0, 300.0, false) }
    }

    @Test
    fun sendAddFuelRequestDifferentCase_callsAddFuelExecutor() {
        testAddFuelRequest(
            message = "fylt en testbil 5555 30.2 L 302.0 Kr",
            name = "en testbil"
        )
        verify { book.addFuelForVehicle("en testbil", 5555, 30.2, 302.0, false) }
    }

    @Test
    fun sendAddFuelRequestDifferentCaseWithComma_callsAddFuelExecutor() {
        testAddFuelRequest(
            message = "Hei drivstoff XC 70 1234 km 30,44 l 608,80 kr.. :D",
            name = "XC 70"
        )
        verify { book.addFuelForVehicle("XC 70", 1234, 30.44, 608.80, false) }
    }

    private fun testAddFuelRequest(message: String, name: String) {
        every { book.addFuelForVehicle(any(), any(), any(), any()) } returns fuelRecord(
            vehicle = vehicle("testbil", fuelType = FuelType.BENSIN),
            odometer = 34567,
            costNOK = 123.3,
            amount = 123.32
        )

        messagebot.processMessage(message, senderID)

        verifySentMessage("Registrert tanking av $name ved 34567: 123,32 liter for 123,3 kr, 1 kr/l")
    }

    @Test
    fun sendAddFuelRequestForUnknownCar_answersCarUnknown() {
        every { book.addFuelForVehicle(any(), any(), any(), any()) } throws VehicleNotFoundException("Hei!", "test-bil")

        messagebot.processMessage("Drivstoff test-bil 444 30l 300kr", senderID)

        verifySentMessage("Kjenner ikke til bil test-bil")
        confirmVerified(botMessenger)
    }

}
