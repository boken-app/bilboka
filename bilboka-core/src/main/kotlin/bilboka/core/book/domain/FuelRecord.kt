package bilboka.core.book.domain

import bilboka.core.vehicle.FuelType
import java.time.LocalDateTime

class FuelRecord(
    dateTime: LocalDateTime = LocalDateTime.now(),
    odometer: Int? = null,
    val amount: Double? = null,
    val costNOK: Double? = null,
    val isFull: Boolean = false,
    val fuelType: FuelType
) : Record(dateTime, RecordType.FUEL, odometer) {

    fun pricePerLiter(): Double? {
        if (costNOK == null || amount == null) {
            return null
        }
        return (costNOK / amount)
    }

}