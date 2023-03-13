package bilboka.messagebot

import bilboka.core.vehicle.domain.FuelType
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test

class VehicleInfoTest : AbstractMessageBotTest() {

    @Test
    fun getInfo_getsInfo() {
        val vehicle = vehicle(name = "En Testbil", fuelType = FuelType.DIESEL)
        every { vehicleService.getVehicle(any()) } returns vehicle

        messagebot.processMessage("Info testbil", registeredSenderID)

        verify {
            botMessenger.sendMessage(
                message = match { msg -> msg.contains("Bil-navn: En Testbil") },
                registeredSenderID
            )
            vehicleService.getVehicle("testbil")
        }
    }
}
