package bilboka.core.book.service

import bilboka.core.book.domain.FuelRecord
import bilboka.core.repository.InMemoryStorage
import bilboka.core.vehicle.Vehicle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime.now

internal class CarBookServiceTest {

    val carBookService: CarBookService = CarBookService(InMemoryStorage())

    @Test
    fun makeNewVehicle() {
        val bil = carBookService.addVehicle(Vehicle("Testbil"))

        assertThat(carBookService.getVehicle("Testbil")).isEqualTo(bil)
        assertThat(carBookService.getVehicle("Testbil")?.book).isNotNull

    }

    @Test
    fun addFuelRecordToBook() {
        carBookService.addVehicle(Vehicle("Testbil"))

        val fuelToAdd = FuelRecord(now(), 300000, 12.4, 13.37, false)
        carBookService.addRecordForVehicle(fuelToAdd, "Testbil")

        assertThat(carBookService.getBookForVehicle("Testbil")?.records).contains(fuelToAdd)
    }
}