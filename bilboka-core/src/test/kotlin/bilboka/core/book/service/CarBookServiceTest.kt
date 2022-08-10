package bilboka.core.book.service

import bilboka.core.domain.book.FuelRecord
import bilboka.core.domain.vehicle.FuelType
import bilboka.core.domain.vehicle.Vehicle
import bilboka.core.repository.InMemoryStorage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime.now

internal class CarBookServiceTest {

    val carBookService: CarBookService = CarBookService(InMemoryStorage())

    @Test
    fun makeNewVehicle() {
        val bil = carBookService.addVehicle(Vehicle(name = "Testbil", fuelType = FuelType.DIESEL))

        assertThat(carBookService.getVehicle("Testbil")).isEqualTo(bil)
        assertThat(carBookService.getVehicle("Testbil")?.bookEntries).isNotNull

    }

    @Test
    fun addFuelRecordToBook() {
        val vehicle = Vehicle(name = "Testbil", fuelType = FuelType.DIESEL)
        carBookService.addVehicle(vehicle)

        val fuelToAdd = FuelRecord(now(), vehicle, 300000, 12.4, 13.37, false, FuelType.DIESEL)
        carBookService.addRecordForVehicle(fuelToAdd, "Testbil")

        assertThat(carBookService.getBookForVehicle("Testbil").records).contains(fuelToAdd)
    }
}