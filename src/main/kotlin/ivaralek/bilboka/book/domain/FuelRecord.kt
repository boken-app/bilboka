package ivaralek.bilboka.book.domain

import java.time.LocalDate

class FuelRecord(
        date: LocalDate?,
        odometer: Int? = null,
        val amount: Double? = null,
        val costNOK: Double? = null,
        val isFull: Boolean) : Record(date, RecordType.FUEL, odometer) {

    fun pricePerLiter(): Double? {
        if (costNOK == null || amount == null) {
            return null
        }
        return costNOK / amount
    }

}