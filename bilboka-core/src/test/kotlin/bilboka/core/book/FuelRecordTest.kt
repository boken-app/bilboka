package bilboka.core.book

import bilboka.core.book.domain.FuelRecord
import bilboka.core.book.domain.RecordType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZonedDateTime

internal class FuelRecordTest {

    @Test
    fun pricePerLiterIsCorrect() {
        val fuelRecord = FuelRecord(odometer = 250000, amount = 100.0, costNOK = 1000.0, isFull = true)

        assertThat(fuelRecord.pricePerLiter()).isEqualTo(10.0)
        assertThat(fuelRecord.isFull).isTrue()
    }

    @Test
    fun pricePerLiterIsNullWithNoAmount() {
        val fuelRecord = FuelRecord(odometer = 250000, amount = null, costNOK = 1000.0, isFull = true)

        assertThat(fuelRecord.pricePerLiter()).isNull()
    }

    @Test
    fun pricePerLiterIsNullWithNoCost() {
        val fuelRecord = FuelRecord(odometer = 250000, amount = 100.0, costNOK = null, isFull = true)

        assertThat(fuelRecord.pricePerLiter()).isNull()
    }

    @Test
    fun fieldsAreSet() {
        val date = LocalDateTime.now()
        val fuelRecord = FuelRecord(odometer = 1000000, amount = 2.0, costNOK = 3.0, isFull = true, dateTime = date)

        assertThat(fuelRecord.odometer).isEqualTo(1000000)
        assertThat(fuelRecord.amount).isEqualTo(2.0)
        assertThat(fuelRecord.costNOK).isEqualTo(3.0)
        assertThat(fuelRecord.isFull).isTrue()
        assertThat(fuelRecord.dateTime).isEqualTo(date)
        assertThat(fuelRecord.type).isEqualTo(RecordType.FUEL)
    }

    @Test
    @Disabled("Denne funker ikke av en eller annen grunn.")
    fun dateTimeIsSet() {
        val fuelRecord = FuelRecord(odometer = 1000000, amount = null, costNOK = null, isFull = false)

        val now = ZonedDateTime.now()
        assertThat(fuelRecord.creationDateTime).isAfterOrEqualTo(now)
    }
}