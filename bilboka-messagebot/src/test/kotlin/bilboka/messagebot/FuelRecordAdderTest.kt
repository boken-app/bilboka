package bilboka.messagebot

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

@ExtendWith(MockKExtension::class)
class FuelRecordAdderTest : AbstractMessageBotTest() {

    @Test
    fun sendAddFuelRequest_callsAddFuelExecutor() {
        testAddFuelRequest(
            message = "Drivstoff testbil 34567 30l 300kr",
        )
        verify { book.addFuelForVehicle("testbil", 34567, 30.0, 300.0, false) }
    }

    @Test
    fun sendAddFuelRequestDifferentCase_callsAddFuelExecutor() {
        testAddFuelRequest(
            message = "fylt en testbil 5555 30.2 L 302.0 Kr"
        )
        verify { book.addFuelForVehicle("en testbil", 5555, 30.2, 302.0, false) }
    }

    @Test
    fun sendAddFuelRequestDifferentCaseWithComma_callsAddFuelExecutor() {
        testAddFuelRequest(
            message = "Hei drivstoff XC 70 1234 km 30,44 l 608,80 kr.. :D"
        )
        verify { book.addFuelForVehicle("XC 70", 1234, 30.44, 608.80, false) }
    }

    private fun testAddFuelRequest(message: String) {
        every { book.addFuelForVehicle(any(), any(), any(), any()) } returns FuelRecord(
            vehicle = Vehicle("testbil", fuelType = FuelType.BENSIN),
            odometer = 34567,
            costNOK = 123.3,
            amount = 123.32
        )

        messagebot.processMessage(message, senderID)

        verifySentMessage("Registrert tanking av testbil ved 34567 km: 123,32 liter for 123,3 kr, 1 kr/l")
    }

    @Test
    fun sendAddFuelRequestForUnknownCar_answersCarUnknown() {
        every { book.addFuelForVehicle(any(), any(), any(), any()) } throws VehicleNotFoundException("Hei!", "test-bil")

        messagebot.processMessage("Drivstoff test-bil 444 30l 300kr", senderID)

        verifySentMessage("Kjenner ikke til bil test-bil")
        confirmVerified(botMessenger)
    }

    private fun verifySentMessage(message: String) {
        verify {
            botMessenger.sendMessage(
                message,
                senderID
            )
        }
    }

}
