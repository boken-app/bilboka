package bilboka.core.book

import bilboka.core.book.domain.Record
import bilboka.core.book.domain.RecordType
import bilboka.core.vehicle.domain.Vehicle
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class FuelRecordTest {

    private val testbil = mockk<Vehicle>(relaxed = true)

    @Test
    fun pricePerLiterIsCorrect() {
        val fuelRecord = fuelRecord(
            vehicle = testbil,
            odometer = 250000,
            amount = 100.0,
            costNOK = 1000.0,
            isFull = true,
        )

        assertThat(fuelRecord.pricePerLiter()).isEqualTo(10.0)
        assertThat(fuelRecord.isFullTank).isTrue
    }

    @Test
    fun pricePerLiterIsNullWithNoAmount() {
        val fuelRecord = fuelRecord(
            vehicle = testbil,
            odometer = 250000,
            amount = null,
            costNOK = 1000.0,
            isFull = true,
        )

        assertThat(fuelRecord.pricePerLiter()).isNull()
    }

    @Test
    fun pricePerLiterIsNullWithNoCost() {
        val fuelRecord = fuelRecord(
            vehicle = testbil,
            odometer = 250000,
            amount = 100.0,
            costNOK = null,
            isFull = true,
        )

        assertThat(fuelRecord.pricePerLiter()).isNull()
    }

    @Test
    fun fieldsAreSet() {
        val date = LocalDateTime.now()
        val fuelRecord = fuelRecord(
            vehicle = testbil,
            dateTime = date,
            odometer = 1000000,
            amount = 2.0,
            costNOK = 3.0,
            isFull = true,
        )

        assertThat(fuelRecord.odometer).isEqualTo(1000000)
        assertThat(fuelRecord.amount).isEqualTo(2.0)
        assertThat(fuelRecord.costNOK).isEqualTo(3.0)
        assertThat(fuelRecord.isFullTank).isTrue()
        assertThat(fuelRecord.dateTime).isEqualTo(date)
        assertThat(fuelRecord.type).isEqualTo(RecordType.FUEL)
    }

    private fun fuelRecord(
        vehicle: Vehicle,
        dateTime: LocalDateTime = LocalDateTime.now(),
        odometer: Int,
        amount: Double?,
        costNOK: Double?,
        isFull: Boolean
    ): Record {
        val record = mockk<Record>()
        every { record.vehicle } returns vehicle
        every { record.dateTime } returns dateTime
        every { record.type } returns RecordType.FUEL
        every { record.odometer } returns odometer
        every { record.amount } returns amount
        every { record.costNOK } returns costNOK
        every { record.isFullTank } returns isFull
        every { record.isFullTank } returns isFull
        every { record.pricePerLiter() } answers { callOriginal() }
        return record
    }
}
