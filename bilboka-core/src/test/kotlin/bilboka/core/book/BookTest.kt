package bilboka.core.book

import bilboka.core.domain.book.Book
import bilboka.core.domain.book.FuelRecord
import bilboka.core.domain.book.MaintenanceRecord
import bilboka.core.domain.vehicle.FuelType
import bilboka.core.domain.vehicle.Vehicle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime.now

internal class BookTest {

    @Test
    fun addRecordAddsRecord() {
        val book = Book(Vehicle(name = "", fuelType = FuelType.DIESEL))

        val record1 = FuelRecord(now(), book.vehicle, 23300, null, null, true, FuelType.DIESEL)
        val record2 = MaintenanceRecord(now().minusDays(1), book.vehicle, 40000)

        book.addRecord(record1)
        book.addRecord(record2)

        assertThat(book.records).contains(record1)
        assertThat(book.records).contains(record2)
        assertThat(book.records).hasSize(2)
    }
}
