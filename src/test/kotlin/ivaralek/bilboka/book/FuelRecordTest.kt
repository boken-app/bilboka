package ivaralek.bilboka.book

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime
import java.util.OptionalDouble.of

internal class FuelRecordTest {

    @Test
    fun pricePerLiterIsCorrect() {
        val fuelRecord = FuelRecord(ZonedDateTime.now(), of(100.0), of(1000.0))

        assertThat(fuelRecord.pricePerLiter().asDouble).isEqualTo(10.0)
    }
}