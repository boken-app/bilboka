package bilboka.messagebot

import bilboka.core.book.domain.FuelRecord
import bilboka.core.vehicle.FuelType
import bilboka.core.vehicle.Vehicle
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
            name = "testbil",
            message = "Drivstoff testbil 34567 30l 300kr",
            answer = "Registrert tanking av testbil ved 34567 km: 30 liter for 300 kr, 10 kr/l"
        )
    }

    @Test
    fun sendAddFuelRequestDifferentCase_callsAddFuelExecutor() {
        testAddFuelRequest(
            name = "en testbil",
            message = "fylt en testbil 5555 30.2 L 302.0 Kr",
            answer = "Registrert tanking av en testbil ved 5555 km: 30,2 liter for 302 kr, 10 kr/l"
        )
    }

    @Test
    fun sendAddFuelRequestDifferentCaseWithComma_callsAddFuelExecutor() {
        testAddFuelRequest(
            name = "XC 70",
            message = "Hei drivstoff XC 70 1234 km 30,44 l 608,80 kr.. :D",
            answer = "Registrert tanking av XC 70 ved 1234 km: 30,44 liter for 608,8 kr, 20 kr/l"
        )
    }

    private fun testAddFuelRequest(name: String, message: String, answer: String) {
        every { carBookExecutor.addRecordToVehicle(any(), any()) } returns Vehicle(
            name = name,
            fuelType = FuelType.DIESEL
        )

        messagebot.processMessage(message, senderID)

        verifySentMessage(answer)
        verify { carBookExecutor.addRecordToVehicle(ofType(FuelRecord::class), name) }
        confirmVerified(botMessenger, carBookExecutor)
    }

    @Test
    fun sendAddFuelRequestForUnknownCar_answersCarUnknown() {
        every { carBookExecutor.addRecordToVehicle(any(), any()) } throws VehicleNotFoundException("Hei!", "test-bil")

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
