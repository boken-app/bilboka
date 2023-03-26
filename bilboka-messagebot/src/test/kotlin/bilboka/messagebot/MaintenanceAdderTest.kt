package bilboka.messagebot

import bilboka.core.vehicle.domain.Vehicle
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class MaintenanceAdderTest : AbstractMessageBotTest() {

    @Test
    fun addMaintenance_doesCallToVehicle() {
        val mockVehicle = mockVehicle("testbilen")
        mockMaintenanceFor(mockVehicle, "PANSER")

        messagebot.processMessage("Bytt panser testbilen 34456", registeredSenderID)

        verify { botMessenger.sendMessage(message = match { it.contains("Ekstra kommentar?") }, any()) }

        messagebot.processMessage("nei", registeredSenderID)

        verify {
            mockVehicle.enterMaintenance(
                maintenanceItem = "PANSER",
                odometer = 34456,
                comment = null,
                source = any(),
                enteredBy = any(),
                dateTime = any()
            )
        }
    }

    @Test
    fun addMaintenanceWithExtraComment_setsAsComment() {
        val mockVehicle = mockVehicle("testbilen1")
        mockMaintenanceFor(mockVehicle, "OVERLEDNING")

        messagebot.processMessage("Bytt overledning testbilen1 45680 grønn og fin", registeredSenderID)

        verify { botMessenger.sendMessage(message = match { it.contains("Registrert") }, any()) }

        verify {
            mockVehicle.enterMaintenance(
                maintenanceItem = "OVERLEDNING",
                odometer = 45680,
                comment = "grønn og fin",
                source = any(),
                enteredBy = any(),
                dateTime = any()
            )
        }
    }

    private fun mockMaintenanceFor(mockVehicle: Vehicle, item: String) {
        every { book.maintenanceItems() } returns setOf(item)
        every {
            mockVehicle.enterMaintenance(any(), any(), any(), any(), any(), any(), any(), any(), any())
        } returns mockk(relaxed = true)
    }
}
