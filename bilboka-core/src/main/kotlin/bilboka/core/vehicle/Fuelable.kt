package bilboka.core.vehicle

import java.time.LocalDateTime

interface Fuelable {

    fun addFuel(
        dateTime: LocalDateTime? = LocalDateTime.now(),
        odometer: Int? = null,
        amount: Double? = null,
        costNOK: Double? = null,
        isFull: Boolean = false
    )

    fun fuelType(): FuelType

}