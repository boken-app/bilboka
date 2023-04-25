package bilboka.core.book

import bilboka.core.book.domain.BookEntry
import bilboka.core.book.domain.sort
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.abs

fun Collection<BookEntry>.entryClosestTo(
    targetTime: LocalDateTime,
    filter: ((entry: BookEntry) -> Boolean)? = { true }
): BookEntry? {
    return this.entryClosestTo(
        target = targetTime,
        getParameter = { dateTime },
        distanceFunction = { first, last -> Duration.between(first, last).seconds },
        filter
    )
}

fun Collection<BookEntry>.entryClosestTo(
    targetOdo: Int,
    filter: ((entry: BookEntry) -> Boolean)? = { true }
): BookEntry? {
    return this.entryClosestTo(
        target = targetOdo,
        getParameter = { odometer },
        distanceFunction = { first, last -> (last - first).toLong() },
        filter
    )
}

private fun <T> Collection<BookEntry>.entryClosestTo(
    target: T,
    getParameter: BookEntry.() -> T?,
    distanceFunction: (first: T, last: T) -> Long,
    filter: ((entry: BookEntry) -> Boolean)?
): BookEntry? {
    val entriesBackwards = sort().reversed().filter(filter ?: { true })

    var previousEntry: BookEntry? = entriesBackwards.firstOrNull { it.getParameter() != null }
    var previousDistance = previousEntry?.let { distanceFunction(target, it.getParameter()!!) }

    if (previousDistance != null) {
        entriesBackwards.forEach {
            it.getParameter()?.run {
                val diff = distanceFunction(target, this)
                if (diff < 0) {
                    return if (abs(diff) < abs(previousDistance!!)) it else previousEntry
                }
                previousDistance = diff
                previousEntry = it
            }
        }
    }

    return previousEntry
}
