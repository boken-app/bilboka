package bilboka.configuration

import bilboka.core.book.service.CarBookService
import bilboka.core.domain.book.FuelRecord
import bilboka.core.domain.vehicle.FuelType
import bilboka.core.domain.vehicle.Vehicle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime.now
import java.time.ZonedDateTime

@SpringBootTest
internal class BookIT(@Autowired val carBookService: CarBookService) {

    @Test
    fun saveAndGetFromStorage() {
        val testTime = ZonedDateTime.now()
        val bil = carBookService.addVehicle(Vehicle(name = "testbil", fuelType = FuelType.DIESEL))

        val bookByVehicle = carBookService.getBookForVehicle("testbil")
        assertThat(bookByVehicle).isNotNull
        assertThat(bookByVehicle.vehicle).isEqualTo(bil)

        val bookByName = carBookService.getBookForVehicle(bil.name)
        assertThat(bookByName).isNotNull
        assertThat(bookByName.vehicle).isEqualTo(bil)

        val record = FuelRecord(now(), bil, 1234, 10.0, 190.1, false, FuelType.DIESEL)
        carBookService.addRecordForVehicle(record, "testbil")

        assertThat(bookByVehicle.records).hasSize(1)
        assertThat(bookByVehicle.records).contains(record)
        assertThat(bookByVehicle.records.first().creationDateTime).isAfterOrEqualTo(testTime)
    }
}