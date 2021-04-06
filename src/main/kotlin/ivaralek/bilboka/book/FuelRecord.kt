package ivaralek.bilboka.book

import java.time.ZonedDateTime
import java.util.*
import java.util.OptionalDouble.empty
import java.util.OptionalDouble.of

class FuelRecord(
        datetime: ZonedDateTime,
        val amount: OptionalDouble = empty(),
        val costNOK: OptionalDouble = empty()) : Record(datetime, RecordType.FUEL) {

    fun pricePerLiter(): OptionalDouble {
        return if (amount.isEmpty || costNOK.isEmpty) {
            empty()
        } else {
            of(costNOK.asDouble / amount.asDouble)
        }
    }

}