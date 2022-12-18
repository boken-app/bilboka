package bilboka.core.book.service

import bilboka.core.H2Test
import bilboka.core.domain.vehicle.FuelType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class VehicleServiceTest : H2Test() {

    val vehicleService: VehicleService = VehicleService()

    @Test
    fun makeNewVehicle() {
        val bil = vehicleService.addVehicle("Testbil", FuelType.DIESEL)

        assertThat(vehicleService.findVehicle("Testbil").name).isEqualTo(bil.name)
    }


}