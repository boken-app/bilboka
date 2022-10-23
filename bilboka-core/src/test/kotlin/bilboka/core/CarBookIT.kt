package bilboka.core

import bilboka.core.book.service.VehicleService
import bilboka.core.domain.vehicle.FuelType
import bilboka.core.domain.vehicle.Vehicle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [VehicleService::class, Book::class])
@Disabled
class CarBookIT {

    @Autowired
    lateinit var vehicleService: VehicleService

    @Autowired
    lateinit var book: Book

    @Test
    fun vehicleExistsAfterSave() {
        vehicleService.addVehicle(Vehicle("760", fuelType = FuelType.DIESEL))

        val vehicle = vehicleService.findVehicle("760")
        assertThat(vehicle).isNotNull
    }

    @Test
    fun addFuelForXC70_succeeds() {
        val vehicle =
            vehicleService.addVehicle(Vehicle(name = "xc70", nicknames = setOf("crosser"), fuelType = FuelType.BENSIN))

        book.addFuelForVehicle(
            "crosser",
            1234,
            amount = 12.4,
            costNOK = 22.43
        )

        assertThat(vehicle.bookEntries).isNotEmpty
    }
}
