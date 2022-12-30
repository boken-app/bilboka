package bilboka.core.book

import bilboka.core.H2Test
import bilboka.core.book.domain.Record
import bilboka.core.book.domain.RecordType
import bilboka.core.vehicle.domain.FuelType
import bilboka.core.vehicle.domain.OdometerUnit
import bilboka.core.vehicle.domain.Vehicle
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.transactions.transaction

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDateTime

internal class RecordsIT : H2Test() {

    private val before: Instant = Instant.now()

    private lateinit var testVehicle: Vehicle
    private lateinit var testRecord: Record

    @BeforeEach
    fun createTestData() {
        transaction {
            testVehicle = Vehicle.new {
                name = "testbil"
                fuelType = FuelType.BENSIN
                tegnkombinasjonNormalisert = "AB1234"
                odometerUnit = OdometerUnit.KILOMETERS
            }
            testRecord = Record.new {
                dateTime = LocalDateTime.now()
                type = RecordType.FUEL
                source = "test"
                amount = 12.2
                vehicle = testVehicle
            }
        }
    }

    @Test
    fun recordAssociatesWithVehicle() {
        transaction {
            val fetchedRecord = Record[testRecord.id]

            assertThat(fetchedRecord.vehicle.id).isEqualTo(testVehicle.id)
            assertThat(fetchedRecord.vehicle.records).contains(fetchedRecord)
        }
    }

    @Test
    fun recordGetsGeneratedValues() {
        val fetchedRecord = transaction {
            Record[testRecord.id]
        }
        assertThat(fetchedRecord.creationTimestamp).isAfter(before)
    }

}