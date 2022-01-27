package bilboka.core.book.domain

import bilboka.core.vehicle.Vehicle

class Book(var vehicle: Vehicle) {

    var records: ArrayList<Record> = ArrayList<Record>()

    fun addRecord(record: Record) {
        records.add(record)
    }
}
