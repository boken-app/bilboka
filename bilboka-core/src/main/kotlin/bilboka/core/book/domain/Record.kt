package bilboka.core.book.domain

import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.ZonedDateTime.now

open class Record(
    val date: LocalDateTime? = LocalDateTime.now(),
    val type: RecordType,
    val odometer: Int? = null
) {
    val creationDateTime: ZonedDateTime = now()
}
