package bilboka.core.book.domain

import java.time.LocalDateTime

class MaintenanceRecord(date: LocalDateTime?, odometer: Int?) : Record(date, RecordType.MAINTENANCE, odometer) {

}
