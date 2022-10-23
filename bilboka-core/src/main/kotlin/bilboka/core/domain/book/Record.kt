package bilboka.core.domain.book

import bilboka.core.domain.vehicle.Vehicle
import java.time.LocalDateTime
import java.time.ZonedDateTime

open class Record(
    open val dateTime: LocalDateTime? = LocalDateTime.now(),
    open val type: RecordType,
    open val odometer: Int? = null,
    open val vehicle: Vehicle,
    open val id: Long? = null,
    open val creationDateTime: ZonedDateTime? = null
) {
    // val creationDateTime: ZonedDateTime = now()
}
