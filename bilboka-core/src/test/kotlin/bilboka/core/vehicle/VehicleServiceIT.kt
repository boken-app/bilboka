package bilboka.core.vehicle

import bilboka.core.H2Test
import bilboka.core.vehicle.domain.FuelType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class VehicleServiceIT : H2Test() {

    private val vehicleService: VehicleService = VehicleService()

    @Test
    fun vehicleExistsAfterSave() {
        vehicleService.addVehicle("760", FuelType.DIESEL)

        val vehicle = vehicleService.findVehicle("760")
        assertThat(vehicle).isNotNull
    }

    @Test
    fun canMakeNewVehicle() {
        val bil = vehicleService.addVehicle("Testbil", FuelType.DIESEL)

        assertThat(vehicleService.findVehicle("Testbil").name).isEqualTo(bil.name)
    }

    @Test
    fun canFindVehicleByTegnkombinasjon() {
        val bil = vehicleService.addVehicle(name = "Testbil2", tegnkombinasjon = "AB456", fuelType = FuelType.DIESEL)

        assertThat(vehicleService.findVehicle("ab 456").name).isEqualTo(bil.name)
    }


}