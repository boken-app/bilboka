package ivaralek.bilboka.core.book.domain

import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.ZonedDateTime.now

open class Record(
    val date: LocalDate? = LocalDate.now(),
    val type: RecordType,
    val odometer: Int? = null
) {
    val creationDateTime: ZonedDateTime = now()
}
