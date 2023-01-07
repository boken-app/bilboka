package bilboka.messagebot

import bilboka.core.vehicle.domain.FuelType
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class VehicleInfoTest : AbstractMessageBotTest() {

    @Test
    fun getInfo_getsInfo() {
        val vehicle = vehicle(name = "En Testbil", fuelType = FuelType.DIESEL)
        every { vehicleService.findVehicle(any()) } returns vehicle

        messagebot.processMessage("Info testbil", senderID)

        verify {
            botMessenger.sendMessage(
                message = match { msg -> msg.contains("Bil-navn: En Testbil") },
                senderID
            )
            vehicleService.findVehicle("testbil")
        }
        confirmVerified(botMessenger)
    }
}
