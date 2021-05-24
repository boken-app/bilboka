package ivaralek.bilboka.core.vehicle

interface Fuelable {

    abstract fun addFuel(amount: Int, isFull: Boolean)

}