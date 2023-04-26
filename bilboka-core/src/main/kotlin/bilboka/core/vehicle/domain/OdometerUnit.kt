package bilboka.core.vehicle.domain

import kotlin.math.roundToInt

enum class OdometerUnit(val displayValue: String) {
    KILOMETERS("km"),
    MILES("mi");

    override fun toString(): String {
        return displayValue
    }

    fun conversionToKilometers(): Double {
        return when (this) {
            KILOMETERS -> 1.0
            MILES -> 1.609344
        }
    }

    fun convertToKilometers(odo: Int): Int {
        return conversionToKilometers().times(odo).roundToInt()
    }
}
