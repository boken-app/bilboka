package ivaralek.bilboka.book

import ivaralek.bilboka.book.domain.Book
import ivaralek.bilboka.book.domain.FuelRecord
import ivaralek.bilboka.book.domain.MaintenanceRecord
import ivaralek.bilboka.vehicle.Vehicle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime
import java.util.OptionalDouble.empty

internal class BookTest {

    @Test
    fun addRecordAddsRecord() {
        var book = Book(Vehicle(""))

        var record1 = FuelRecord(ZonedDateTime.now(), 23300, empty(), empty(), true)
        var record2 = MaintenanceRecord(ZonedDateTime.now().minusDays(1), 40000)

        book.addRecord(record1)
        book.addRecord(record2)

        assertThat(book.records).contains(record1)
        assertThat(book.records).contains(record2)
        assertThat(book.records).hasSize(2)
    }
}
