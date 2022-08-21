package bilboka.core.domain.vehicle

import bilboka.core.book.service.normaliserTegnkombinasjon
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
) {

    fun addFuel(
        odometer: Int?,
        amount: Double?,
        costNOK: Double?,
        isFull: Boolean = false,
        dateTime: LocalDateTime = LocalDateTime.now()
    ): FuelRecord {
        val fuelRecord = FuelRecord(
            dateTime = dateTime,
            odometer = odometer,
            amount = amount,
            costNOK = costNOK,
            isFull = isFull,
            vehicle = this
        )
        bookEntries?.add(
            fuelRecord
        )
        return fuelRecord
    }

    fun fuelType(): FuelType {
        return fuelType
    }

    fun isCalled(calledName: String): Boolean {
        return calledName.lowercase() == name.lowercase()
                || nicknames.map { it.lowercase() }.contains(calledName.lowercase())
                || hasTegnkombinasjon(calledName)
    }

    fun hasTegnkombinasjon(tegnkombinasjon: String): Boolean {
        return tegnkombinasjon.normaliserTegnkombinasjon() == tegnkombinasjonNormalisert
    }

}
