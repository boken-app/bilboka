package ivaralek.bilboka.book.domain

import java.time.ZonedDateTime

class MaintenanceRecord(datetime: ZonedDateTime, odometer: Int) : Record(datetime, RecordType.MAINTENANCE, odometer) {

}
