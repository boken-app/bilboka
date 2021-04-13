package ivaralek.bilboka.book.domain

import java.time.ZonedDateTime
import java.util.*
import java.util.OptionalDouble.empty
import java.util.OptionalDouble.of

class FuelRecord(
        datetime: ZonedDateTime,
        odometer: Int,
        val amount: OptionalDouble = empty(),
        val costNOK: OptionalDouble = empty(),
        val isFull: Boolean) : Record(datetime, RecordType.FUEL, odometer) {

    fun pricePerLiter(): OptionalDouble {
        return if (amount.isEmpty || costNOK.isEmpty) {
            empty()
        } else {
            of(costNOK.asDouble / amount.asDouble)
        }
    }

}