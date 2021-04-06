package ivaralek.bilboka.book

import java.time.ZonedDateTime

open class Record(val datetime: ZonedDateTime, val type: RecordType) : Comparable<Record> {


    override fun compareTo(other: Record): Int {
        return datetime.compareTo(other.datetime)
    }


}
