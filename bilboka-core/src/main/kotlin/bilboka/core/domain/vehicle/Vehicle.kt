package bilboka.core.domain.vehicle

import bilboka.core.domain.book.FuelRecord
import bilboka.core.domain.book.Record
import java.time.LocalDateTime
import javax.persistence.*

@Entity
open class Vehicle(
    var name: String,
    @ElementCollection
    var nicknames: Set<String> = setOf(),
    val tegnkombinasjonNormalisert: String? = null,
    val odometerUnit: OdometerUnit = OdometerUnit.KILOMETERS,
    val fuelType: FuelType,
    @OneToMany(mappedBy = "vehicle")
    var bookEntries: MutableList<Record>? = arrayListOf(),
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) : Fuelable {

    override fun addFuel(dateTime: LocalDateTime?, odometer: Int?, amount: Double?, costNOK: Double?, isFull: Boolean) {
        bookEntries?.add(
            FuelRecord(
                dateTime = dateTime ?: LocalDateTime.now(),
                odometer = odometer,
                amount = amount,
                costNOK = costNOK,
                isFull = isFull,
                fuelType = fuelType(),
                vehicle = this
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


}
