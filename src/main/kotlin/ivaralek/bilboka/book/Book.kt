package ivaralek.bilboka.book

import ivaralek.bilboka.vehicle.Vehicle

class Book(private var vehicle: Vehicle) {

    var records: ArrayList<Record> = ArrayList<Record>()

    fun addRecord(record: Record) {
        records.add(record)
    }
}
