package bilboka.core.domain.vehicle

enum class OdometerUnit(val displayValue: String) {
    KILOMETERS("km"),
    MILES("mi");

    override fun toString(): String {
        return displayValue
    }
}
