package bilboka.core

import bilboka.core.book.service.CarBookService
import bilboka.core.config.BilbokaCoreConfig
import bilboka.core.domain.vehicle.FuelType
import bilboka.core.domain.vehicle.Vehicle
import bilboka.core.repository.VehicleRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest(classes = [CarBookService::class, BilbokaCoreConfig::class, VehicleRepository::class])
class CarBookIT {

    @Autowired
    lateinit var carBookService: CarBookService

    @BeforeEach
    fun initiateCars() {

    }

    @Test
    fun bookExistsForXC70() {
        carBookService.addVehicle(Vehicle("xc70", fuelType = FuelType.DIESEL))

        val book = carBookService.getBookForVehicle("xc70")
        assertThat(book).isNotNull
    }

    @Test
    fun addFuelForXC70_succeeds() {
        carBookService.addVehicle(Vehicle("760", fuelType = FuelType.BENSIN))

        carBookService.getVehicle("760")
            ?.addFuel(
                amount = 12.4,
                costNOK = 22.43
            )
        assertThat(carBookService.getBookForVehicle("760").records).isNotEmpty
    }
}
