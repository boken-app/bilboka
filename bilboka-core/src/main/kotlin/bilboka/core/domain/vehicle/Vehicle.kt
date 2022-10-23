package bilboka.core.domain.vehicle

import bilboka.core.book.service.normaliserTegnkombinasjon
import bilboka.core.domain.book.FuelRecord
import bilboka.core.domain.book.Record
import java.time.LocalDateTime

open class Vehicle(
    open var name: String,
    open var nicknames: Set<String> = setOf(),
    open val tegnkombinasjonNormalisert: String? = null,
    open val odometerUnit: OdometerUnit = OdometerUnit.KILOMETERS,
    open val fuelType: FuelType,
    open var bookEntries: MutableList<Record>? = arrayListOf(),
    open val id: Long? = null
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
