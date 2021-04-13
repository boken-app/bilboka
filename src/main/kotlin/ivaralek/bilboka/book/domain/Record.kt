package ivaralek.bilboka.book.domain

import java.time.ZonedDateTime

open class Record(val datetime: ZonedDateTime, val type: RecordType, val odometer: Int) : Comparable<Record> {


    // TODO denne er tentativ
    override fun compareTo(other: Record): Int {
        return datetime.compareTo(other.datetime)
    }


}
