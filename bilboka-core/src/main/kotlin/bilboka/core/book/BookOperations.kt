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
        filter,
        getParameter = { dateTime }
    ) { itemTime -> Duration.between(targetTime, itemTime).seconds }
}

fun Collection<BookEntry>.entryClosestTo(
    targetOdo: Int,
    filter: ((entry: BookEntry) -> Boolean)? = { true }
): BookEntry? {
    return this.entryClosestTo(
        filter,
        getParameter = { odometer }
    ) { itemOdo -> (itemOdo - targetOdo).toLong() }
}

private fun <T> Collection<BookEntry>.entryClosestTo(
    filter: ((entry: BookEntry) -> Boolean)?,
    getParameter: BookEntry.() -> T?,
    distanceFunction: (item: T) -> Long
): BookEntry? {
    val entriesBackwards = sort().reversed().filter(filter ?: { true })

    var previousEntry: BookEntry? = entriesBackwards.firstOrNull { it.getParameter() != null }
    var previousDistance = previousEntry?.let { distanceFunction(it.getParameter()!!) }

    if (previousDistance != null) {
        entriesBackwards.forEach {
            it.getParameter()?.run {
                val diff = distanceFunction(this)
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
