package bilboka.core.domain.book

import bilboka.core.domain.vehicle.Vehicle
import java.time.LocalDateTime

class FuelRecord(
    dateTime: LocalDateTime = LocalDateTime.now(),
    vehicle: Vehicle,
    odometer: Int? = null,
    val amount: Double? = null,
    val costNOK: Double? = null,
    val isFull: Boolean = false
) : Record(dateTime, RecordType.FUEL, odometer, vehicle = vehicle) {

    fun pricePerLiter(): Double? {
        if (costNOK == null || amount == null) {
            return null
        }
        return (costNOK / amount)
    }

}