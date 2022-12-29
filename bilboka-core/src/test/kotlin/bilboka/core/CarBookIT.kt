package bilboka.core

import bilboka.core.book.service.VehicleService
import bilboka.core.domain.book.RecordType
import bilboka.core.domain.vehicle.FuelType
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
    fun vehicleExistsAfterSave() {
        vehicleService.addVehicle("760", FuelType.DIESEL)

        val vehicle = vehicleService.findVehicle("760")
        assertThat(vehicle).isNotNull
    }

    @Test
    fun addFuelForXC70_succeeds() {
        vehicleService.addVehicle("xc70", FuelType.BENSIN)

        book.addFuelForVehicle(
            "xc70",
            1234,
            amount = 12.4,
            costNOK = 22.43
        )

        val vehicle = vehicleService.findVehicle("xc70")
        assertThat(vehicle.lastRecord(RecordType.FUEL)).isNotNull
    }
}
