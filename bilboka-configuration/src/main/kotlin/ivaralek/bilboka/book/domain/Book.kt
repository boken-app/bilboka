package ivaralek.bilboka.book.domain

import ivaralek.bilboka.vehicle.Vehicle

class Book(var vehicle: Vehicle) {

    var records: ArrayList<Record> = ArrayList<Record>()

    fun addRecord(record: Record) {
        records.add(record)
    }
}
