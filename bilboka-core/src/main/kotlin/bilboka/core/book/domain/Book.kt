package bilboka.core.book.domain

import bilboka.core.vehicle.Vehicle

class Book(var vehicle: Vehicle) {

    var records: ArrayList<Record> = ArrayList()

    fun addRecord(record: Record) {
        records.add(record)
    }

    fun getLastFuelRecord(): FuelRecord? {
        return records.lastOrNull { record -> record is FuelRecord } as FuelRecord?
    }

}
