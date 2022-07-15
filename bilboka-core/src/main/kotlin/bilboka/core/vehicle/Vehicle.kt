package bilboka.core.vehicle

import bilboka.core.book.domain.Book
import bilboka.core.book.domain.FuelRecord

class Vehicle(
    var name: String,
    var nicknames: Set<String> = setOf(),
    var book: Book? = null
) : Fuelable {

    override fun addFuel(amount: Double, costNOK: Double, isFull: Boolean) {
        book().addRecord(
            FuelRecord(
                amount = amount,
                costNOK = costNOK,
                isFull = isFull
            )
        )
    }

    fun isCalled(calledName: String): Boolean {
        return calledName.lowercase() == name.lowercase() || nicknames.map { it.lowercase() }
            .contains(calledName.lowercase())
    }

    fun book(): Book {
        return book ?: throw IllegalStateException("Mangler bok for bil $name")
    }
}
