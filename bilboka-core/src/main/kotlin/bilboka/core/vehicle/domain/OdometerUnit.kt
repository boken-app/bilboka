package bilboka.core.vehicle.domain

enum class OdometerUnit(val displayValue: String) {
    KILOMETERS("km"),
    MILES("mi");

    override fun toString(): String {
        return displayValue
    }
}
