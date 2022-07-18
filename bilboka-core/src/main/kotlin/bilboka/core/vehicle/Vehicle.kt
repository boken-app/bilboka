package bilboka.core.vehicle

import bilboka.core.book.domain.Book
import bilboka.core.book.domain.FuelRecord
import java.time.LocalDateTime

class Vehicle(
    var book: Book? = null,
    var name: String,
    var nicknames: Set<String> = setOf(),
    val tegnkombinasjonNormalisert: String? = null,
    val odometerUnit: OdometerUnit = OdometerUnit.KILOMETERS,
    val fuelType: FuelType
) : Fuelable {

    override fun addFuel(dateTime: LocalDateTime?, odometer: Int?, amount: Double?, costNOK: Double?, isFull: Boolean) {
        book().addRecord(
            FuelRecord(
                dateTime = dateTime ?: LocalDateTime.now(),
                odometer = odometer,
                amount = amount,
                costNOK = costNOK,
                isFull = isFull,
                fuelType = fuelType()
            )
        )
    }

    override fun fuelType(): FuelType {
        return fuelType
    }

    fun isCalled(calledName: String): Boolean {
        return calledName.lowercase() == name.lowercase()
                || nicknames.map { it.lowercase() }.contains(calledName.lowercase())
                || hasTegnkombinasjon(calledName)
    }

    fun hasTegnkombinasjon(tegnkombinasjon: String): Boolean {
        return tegnkombinasjon
            .replace(" ", "")
            .replace("-", "")
            .uppercase() == tegnkombinasjonNormalisert
    }

    fun book(): Book {
        return book ?: throw IllegalStateException("Mangler bok for bil $name")
    }

}
