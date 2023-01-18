package bilboka.core

import bilboka.core.book.Book
import bilboka.core.book.domain.EntryType
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.FuelType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [VehicleService::class, Book::class])
class CarBookIT : H2Test() {

    @Autowired
    lateinit var vehicleService: VehicleService

    @Autowired
    lateinit var book: Book

    @Test
    fun addFuelForXC70_succeeds() {
        vehicleService.addVehicle("Xc70", fuelType = FuelType.BENSIN)

        book.addFuelForVehicle(
            vehicleName = "XC70",
            odoReading = 1234,
            amount = 12.4,
            costNOK = 22.43,
            source = "test"
        )

        val vehicle = vehicleService.findVehicle("xc70")
        assertThat(vehicle.lastEntry(EntryType.FUEL)).isNotNull
    }
}
