package bilboka.messagebot

import bilboka.core.vehicle.domain.FuelType
import bilboka.integration.autosys.dto.Kjoretoydata
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

    @Test
    fun getInfoAutosys_callsForInfo() {
        every { vehicleService.getAutosysKjoretoydata(any()) } returns Kjoretoydata()

        messagebot.processMessage("autosys-data testbil", registeredSenderID)

        verify {
            botMessenger.sendMessage(
                message = match { msg -> msg.contains("Kjøretøydata fra Autosys") },
                registeredSenderID
            )
            vehicleService.getAutosysKjoretoydata("testbil")
        }
    }

    @Test
    fun getInfoDekkogfelg_callsForInfo() {
        every { vehicleService.getAutosysKjoretoydataByTegnkombinasjon(any()) } returns Kjoretoydata()

        messagebot.processMessage("autosys-dekkogfelg KT65881", registeredSenderID)

        verify {
            botMessenger.sendMessage(
                message = match { msg -> msg.contains("Dekk- og felgdata fra Autosys") },
                registeredSenderID
            )
            vehicleService.getAutosysKjoretoydataByTegnkombinasjon("KT65881")
        }
    }
}
