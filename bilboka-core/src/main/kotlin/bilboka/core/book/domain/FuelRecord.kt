package bilboka.core.book.domain

import java.time.LocalDateTime

class FuelRecord(
    dateTime: LocalDateTime = LocalDateTime.now(),
    odometer: Int? = null,
    val amount: Double? = null,
    val costNOK: Double? = null,
    val isFull: Boolean = false
) : Record(dateTime, RecordType.FUEL, odometer) {

    fun pricePerLiter(): Double? {
        if (costNOK == null || amount == null) {
            return null
        }
        return "%.2f".format(costNOK / amount).toDouble()
    }

}