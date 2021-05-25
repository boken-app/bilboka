package ivaralek.bilboka.core.book.domain

import ivaralek.bilboka.core.vehicle.Vehicle

class Book(var vehicle: Vehicle) {

    var records: ArrayList<Record> = ArrayList<Record>()

    fun addRecord(record: Record) {
        records.add(record)
    }
}
