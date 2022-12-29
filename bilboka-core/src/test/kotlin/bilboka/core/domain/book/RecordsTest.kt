package bilboka.core.domain.book

import bilboka.core.H2Test
import bilboka.core.domain.vehicle.FuelType
import bilboka.core.domain.vehicle.OdometerUnit
import bilboka.core.domain.vehicle.Vehicle
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.transactions.transaction

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class RecordsTest : H2Test() {

    lateinit var testVehicle: Vehicle

    @BeforeEach
    fun createVehicle() {
        testVehicle = transaction {
            Vehicle.new {
                this.name = "testbil"
                this.fuelType = FuelType.BENSIN
                this.tegnkombinasjonNormalisert = "AB1234"
                this.odometerUnit = OdometerUnit.KILOMETERS
            }
        }
    }

    @Test
    fun recordCanBeAddedAndAssociatesWithVehicle() {
        val record = transaction {
            Record.new {
                dateTime = LocalDateTime.now()
                type = RecordType.FUEL
                source = "test"
                amount = 12.2
                vehicle = testVehicle
            }
        }
        transaction {
            val fetchedRecord = Record[record.id]

            assertThat(fetchedRecord).isNotNull
            assertThat(fetchedRecord.vehicle.id).isEqualTo(testVehicle.id)
            assertThat(fetchedRecord.vehicle.records).contains(fetchedRecord)
        }
    }

}