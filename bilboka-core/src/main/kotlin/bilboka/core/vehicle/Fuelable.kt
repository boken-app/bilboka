package bilboka.core.vehicle

interface Fuelable {

    fun addFuel(amount: Double, costNOK: Double, isFull: Boolean = false)

}