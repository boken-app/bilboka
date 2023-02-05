package bilboka.core.vehicle

import bilboka.core.H2Test
import bilboka.core.vehicle.domain.FuelType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class VehicleServiceIT : H2Test() {

    private val vehicleService: VehicleService = VehicleService()

    @Test
    fun vehicleExistsAfterSave() {
        vehicleService.addVehicle("760", fuelType = FuelType.DIESEL)

        val vehicle = vehicleService.getVehicle("760")
        assertThat(vehicle).isNotNull
    }

    @Test
    fun vehicleHasData() {
        vehicleService.addVehicle("760", fuelType = FuelType.BENSIN, tankVol = 80)

        val vehicle = vehicleService.getVehicle("760")
        assertThat(vehicle.fuelType).isEqualTo(FuelType.BENSIN)
        assertThat(vehicle.tankVolume).isEqualTo(80)
    }

    @Test
    fun canMakeNewVehicle() {
        val bil = vehicleService.addVehicle("Testbil", fuelType = FuelType.DIESEL)

        assertThat(vehicleService.getVehicle("Testbil").name).isEqualTo(bil.name)
    }

    @Test
    fun canFindVehicleByTegnkombinasjon() {
        val bil = vehicleService.addVehicle(name = "Testbil2", tegnkombinasjon = "AB456", fuelType = FuelType.DIESEL)

        assertThat(vehicleService.getVehicle("ab 456").name).isEqualTo(bil.name)
    }


}