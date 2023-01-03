package bilboka.core.book

import bilboka.core.H2Test
import bilboka.core.book.domain.BookEntry
import bilboka.core.book.domain.EntryType
import bilboka.core.vehicle.domain.FuelType
import bilboka.core.vehicle.domain.OdometerUnit
import bilboka.core.vehicle.domain.Vehicle
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.transactions.transaction

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDateTime

internal class BookEntriesIT : H2Test() {

    private val before: Instant = Instant.now()

    private lateinit var testVehicle: Vehicle
    private lateinit var testBookEntry: BookEntry

    @BeforeEach
    fun createTestData() {
        transaction {
            testVehicle = Vehicle.new {
                name = "testbil"
                fuelType = FuelType.BENSIN
                tegnkombinasjonNormalisert = "AB1234"
                odometerUnit = OdometerUnit.KILOMETERS
            }
            testBookEntry = BookEntry.new {
                dateTime = LocalDateTime.now()
                type = EntryType.FUEL
                source = "test"
                amount = 12.2
                vehicle = testVehicle
            }
        }
    }

    @Test
    fun entryAssociatesWithVehicle() {
        transaction {
            val fetchedEntry = BookEntry[testBookEntry.id]

            assertThat(fetchedEntry.vehicle.id).isEqualTo(testVehicle.id)
            assertThat(fetchedEntry.vehicle.bookEntries).contains(fetchedEntry)
        }
    }

    @Test
    fun entryGetsGeneratedValues() {
        val fetchedEntry = transaction {
            BookEntry[testBookEntry.id]
        }
        assertThat(fetchedEntry.creationTimestamp).isAfter(before)
    }

}