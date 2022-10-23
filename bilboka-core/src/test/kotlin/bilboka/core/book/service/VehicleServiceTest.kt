package bilboka.core.book.service

import bilboka.core.domain.vehicle.FuelType
import bilboka.core.domain.vehicle.Vehicle
import bilboka.core.repository.InMemoryStorage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class VehicleServiceTest {

    val vehicleService: VehicleService = VehicleService(InMemoryStorage())

    @Test
    @Disabled
    fun makeNewVehicle() {
        val bil = vehicleService.addVehicle(Vehicle(name = "Testbil", fuelType = FuelType.DIESEL))

        assertThat(vehicleService.findVehicle("Testbil")).isEqualTo(bil)
        assertThat(vehicleService.findVehicle("Testbil")?.bookEntries).isNotNull

    }

}