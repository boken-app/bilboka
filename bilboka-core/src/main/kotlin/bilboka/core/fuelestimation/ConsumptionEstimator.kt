package bilboka.core.fuelestimation

import bilboka.core.book.domain.BookEntry
import bilboka.core.book.domain.sort
import bilboka.core.vehicle.domain.OdometerUnit
import java.time.LocalDateTime
import java.time.Period

// TODO Lage total-estimator som returnerer samling med estimater fra tidenes morgen
object ConsumptionEstimator {

    fun lastEstimate(entries: Collection<BookEntry>, odoUnit: OdometerUnit? = null): ConsumptionEstimationResult? {
        val sortedEntries = SortedTraversableEntries(entries)
        return estimateAt(sortedEntries.atLast(), odoUnit)
    }

    fun estimateAt(
        entries: Collection<BookEntry>,
        odo: Int,
        odoUnit: OdometerUnit? = null
    ): ConsumptionEstimationResult? {
        val sortedEntries = SortedTraversableEntries(entries)
        sortedEntries.atFirstAfter(odo) {
            it.isFullTank == true && it.odometer != null
        }
        return estimateAt(sortedEntries, odoUnit)
    }

    fun estimateBetween(
        entries: Collection<BookEntry>,
        firstOdo: Int,
        lastOdo: Int,
        odoUnit: OdometerUnit? = null
    ): ConsumptionEstimationResult? {
        val sortedEntries = SortedTraversableEntries(entries)
        sortedEntries.atFirstAfter(lastOdo) {
            it.isFullTank == true && it.odometer != null
        }
        return estimateAt(sortedEntries, odoUnit) { (it.odometer ?: firstOdo) > firstOdo }
    }

    fun estimateBetween(
        entries: Collection<BookEntry>,
        firstTime: LocalDateTime,
        lastTime: LocalDateTime,
        odoUnit: OdometerUnit? = null
    ): ConsumptionEstimationResult? {
        val sortedEntries = SortedTraversableEntries(entries)
        sortedEntries.atFirstAfter(lastTime) {
            it.isFullTank == true && it.odometer != null
        }
        return estimateAt(sortedEntries, odoUnit) { (it.dateTime ?: firstTime) > firstTime }
    }

    fun estimateAt(
        entries: Collection<BookEntry>,
        dateTime: LocalDateTime,
        odoUnit: OdometerUnit? = null
    ): ConsumptionEstimationResult? {
        val sortedEntries = SortedTraversableEntries(entries)
        sortedEntries.atFirstAfter(dateTime) {
            it.isFullTank == true && it.dateTime != null
        }
        return estimateAt(sortedEntries, odoUnit)
    }

    fun estimateAt(
        selectedEntry: SortedTraversableEntries,
        odoUnit: OdometerUnit? = null,
        estimateWhile: (entry: BookEntry) -> Boolean = { false }
    ): ConsumptionEstimationResult? {
        var totalAmountFilled = 0.0
        var estimateFrom: BookEntry? = null
        var estimateTo: BookEntry? = null

        while (selectedEntry.hasCurrent() && estimateFrom == null) {
            selectedEntry.current().apply {
                if (isFullTank == true && odometer != null) {
                    if (estimateTo == null) {
                        estimateTo = this
                    } else if (!estimateWhile(this)) { // Continue estimation further back if still true
                        estimateFrom = this
                    }
                }

                if (amount != null && estimateFrom == null && estimateTo != null) {
                    totalAmountFilled += amount ?: 0.0
                }
            }
            selectedEntry.previous()
        }

        if (estimateFrom != null) {
            return ConsumptionEstimationResult(
                amountPerDistanceUnit = totalAmountFilled / (estimateTo?.odometer!! - estimateFrom?.odometer!!),
                estimatedAt = estimateTo!!,
                estimatedFrom = estimateFrom!!,
                odometerUnit = odoUnit
            )
        }

        return null
    }
}

class SortedTraversableEntries(
    source: Collection<BookEntry>
) : Collection<BookEntry> {
    private val sortedContent = source.sort()
    private var cursor = -1

    override val size: Int
        get() = sortedContent.size

    override fun isEmpty(): Boolean {
        return sortedContent.isEmpty()
    }

    override fun iterator(): Iterator<BookEntry> {
        return sortedContent.iterator()
    }

    override fun containsAll(elements: Collection<BookEntry>): Boolean {
        return sortedContent.containsAll(elements)
    }

    override fun contains(element: BookEntry): Boolean {
        return sortedContent.contains(element)
    }

    fun atFirstAfter(odo: Int, condition: (entry: BookEntry) -> Boolean): BookEntry? {
        atStart()
        while (hasNext()) {
            next()
            if ((current().odometer ?: odo) >= odo && condition(current())) {
                return current()
            }
        }
        next()
        return null
    }

    fun atFirstAfter(dateTime: LocalDateTime, condition: (entry: BookEntry) -> Boolean): BookEntry? {
        atStart()
        while (hasNext()) {
            next()
            if ((current().dateTime ?: dateTime) >= dateTime && condition(current())) {
                return current()
            }
        }
        next()
        return null
    }

    fun current() = sortedContent[cursor]
    fun previous(): BookEntry? {
        cursor--
        return if (hasCurrent()) {
            current()
        } else null
    }

    fun next(): BookEntry? {
        cursor++
        return if (hasCurrent()) {
            current()
        } else null
    }

    fun hasCurrent(): Boolean {
        return cursor in 0..sortedContent.lastIndex
    }

    fun hasPrevious() = cursor > 0
    fun hasNext() = cursor < sortedContent.lastIndex
    fun atLast(): SortedTraversableEntries {
        cursor = sortedContent.lastIndex
        return this
    }

    fun atFirst(): SortedTraversableEntries {
        cursor = 0
        return this
    }

    fun atEnd(): SortedTraversableEntries {
        cursor = sortedContent.lastIndex + 1
        return this
    }

    fun atStart(): SortedTraversableEntries {
        cursor = -1
        return this
    }
}

data class ConsumptionEstimationResult(
    val amountPerDistanceUnit: Double,
    val estimatedAt: BookEntry,
    val estimatedFrom: BookEntry,
    val odometerUnit: OdometerUnit?
) {
    val estimationPeriod = estimatedFrom.dateTime?.let { from ->
        estimatedAt.dateTime?.let { to ->
            Period.between(
                from.toLocalDate(),
                to.toLocalDate()
            )
        }
    }

    fun litersPer10Km(): Double {
        return odometerUnit?.run {
            amountPerDistanceUnit * (10 / conversionToKilometers())
        } ?: throw IllegalStateException("Mangler enhet for konvertering til kilometer")
    }
}
