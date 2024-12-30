package bilboka.core.fuelestimation

import bilboka.core.book.domain.BookEntry
import bilboka.core.book.domain.sort
import bilboka.core.vehicle.domain.OdometerUnit
import java.time.LocalDateTime

class ConsumptionEstimator(
    entries: Collection<BookEntry>
) {
    private val sortedEntries = SortedEntries(entries)

    fun lastEstimate(odoUnit: OdometerUnit? = null): ConsumptionPointEstimationResult? {
        return estimateOf(TraversableEntries(sortedEntries).atLast(), odoUnit)
    }

    fun estimateAt(
        odo: Int,
        odoUnit: OdometerUnit? = null
    ): ConsumptionPointEstimationResult? {
        val traversableEntries = TraversableEntries(sortedEntries)
        traversableEntries.atFirstAfterOrLastBefore(odo) {
            it.isFullTank == true && it.odometer != null
        }
        return estimateOf(traversableEntries, odoUnit)
    }

    fun closestSpotEstimateBetween(
        firstOdo: Int,
        lastOdo: Int,
        odoUnit: OdometerUnit? = null
    ): ConsumptionPointEstimationResult? {
        val traversableEntries = TraversableEntries(sortedEntries)
        traversableEntries.atFirstAfterOrLastBefore(lastOdo) {
            it.isFullTank == true && it.odometer != null
        }
        return estimateOf(traversableEntries, odoUnit) { (it.odometer ?: firstOdo) > firstOdo }
    }

    fun continousEstimateBetween(
        firstOdo: Int,
        lastOdo: Int,
        odoUnit: OdometerUnit? = null
    ): ContinousEstimationResult? {
        return estimateRange(firstOdo, lastOdo, odoUnit)
            .takeIf { it.isNotEmpty() }
            ?.let {
                ContinousEstimationResult(
                    odoStart = firstOdo,
                    odoEnd = lastOdo,
                    amountEstimate = it.sumOf { res -> res.amountEstimate }
                        .plus(estimateExtrapolationBackwards(firstOdo, it.first()) { amountPerDistance() })
                        .plus(estimateExtrapolationForwards(lastOdo, it.last()) { amountPerDistance() }),
                    costEstimate = it.sumOf { res -> res.costEstimate ?: 0.0 }
                        .plus(estimateExtrapolationBackwards(firstOdo, it.first()) { costPerDistance() ?: 0.0 })
                        .plus(estimateExtrapolationForwards(lastOdo, it.last()) { costPerDistance() ?: 0.0 }),
                    odometerUnit = it.first().odometerUnit
                )
            }
    }

    fun estimateRange(
        firstOdo: Int,
        lastOdo: Int,
        odoUnit: OdometerUnit? = null
    ): List<ConsumptionPointEstimationResult> {
        val traversableEntries = sortedEntries
            .filter { it.isFullTank == true && it.odometer != null }
            .let { TraversableEntries(it) }
            .apply { atFirstAfterOrLastBefore(lastOdo) }

        val estimateRange = mutableListOf<ConsumptionPointEstimationResult>()

        while (traversableEntries.hasCurrent()
            && (traversableEntries.current().odometer?.takeIf { it >= firstOdo } != null) || estimateRange.isEmpty()
        ) {
            estimateOf(traversableEntries, odoUnit)?.let { estimateRange.add(it) }
            traversableEntries.previous()
        }

        return estimateRange.reversed()
    }

    private fun estimateExtrapolationBackwards(
        extOdo: Int,
        estimation: ConsumptionPointEstimationResult,
        valuePerDistance: ConsumptionPointEstimationResult.() -> Double
    ): Double {
        val leftDiff = estimation.estimatedFrom.odometer!! - extOdo
        return estimation.valuePerDistance() * leftDiff
    }

    private fun estimateExtrapolationForwards(
        extOdo: Int,
        estimation: ConsumptionPointEstimationResult,
        valuePerDistance: ConsumptionPointEstimationResult.() -> Double
    ): Double {
        val rightDiff = extOdo - estimation.estimatedAt.odometer!!
        return estimation.valuePerDistance() * rightDiff
    }


    fun closestSpotEstimateBetween(
        firstTime: LocalDateTime,
        lastTime: LocalDateTime,
        odoUnit: OdometerUnit? = null
    ): ConsumptionPointEstimationResult? {
        val traversableEntries = TraversableEntries(sortedEntries)
        traversableEntries.atFirstAfterOrLastBefore(lastTime) {
            it.isFullTank == true && it.odometer != null
        }
        return estimateOf(traversableEntries, odoUnit) { (it.dateTime ?: firstTime) > firstTime }
    }

    fun estimateAt(
        dateTime: LocalDateTime,
        odoUnit: OdometerUnit? = null
    ): ConsumptionPointEstimationResult? {
        val traversableEntries = TraversableEntries(sortedEntries)
        traversableEntries.atFirstAfterOrLastBefore(dateTime) {
            it.isFullTank == true && it.dateTime != null
        }
        return estimateOf(traversableEntries, odoUnit)
    }

    private fun estimateOf(
        selectedEntry: TraversableEntries,
        odoUnit: OdometerUnit? = null,
        estimateWhile: (entry: BookEntry) -> Boolean = { false }
    ): ConsumptionPointEstimationResult? {
        var totalAmountFilled = 0.0
        var totalCost = 0.0
        var estimateFrom: BookEntry? = null
        var estimateTo: BookEntry? = null
        val traversable = TraversableEntries.of(selectedEntry)

        while (traversable.hasCurrent() && estimateFrom == null) {
            traversable.current().apply {
                if (isFullTank == true && odometer != null) {
                    if (estimateTo == null) {
                        estimateTo = this
                    } else if (!estimateWhile(this)) { // Continue estimation further back if still true
                        estimateFrom = this
                    }
                }

                if (amount != null && estimateFrom == null && estimateTo != null) {
                    totalAmountFilled += amount ?: 0.0
                    if (costNOK != null) {
                        totalCost += costNOK ?: 0.0
                    }
                }
            }
            traversable.previous()
        }

        if (estimateFrom != null) {
            return ConsumptionPointEstimationResult(
                amountEstimate = totalAmountFilled,
                costEstimate = totalCost,
                estimatedAt = estimateTo!!,
                estimatedFrom = estimateFrom!!,
                odometerUnit = odoUnit
            )
        }

        return null
    }
}

class SortedEntries(
    source: Collection<BookEntry>
) : ArrayList<BookEntry>(source.sort())

class TraversableEntries(
    private val sortedContent: SortedEntries
) : Collection<BookEntry> {
    private var cursor = -1

    constructor(unsortedContent: Collection<BookEntry>) : this(SortedEntries(unsortedContent))

    companion object {
        fun of(other: TraversableEntries): TraversableEntries {
            return TraversableEntries(other.sortedContent).apply {
                cursor = other.cursor
            }
        }
    }

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

    fun atFirstAfterOrLastBefore(odo: Int, condition: (entry: BookEntry) -> Boolean = { true }): BookEntry? {
        return atFirstAfter(odo, condition) ?: atLastBefore(odo, condition)
    }

    fun atFirstAfterOrLastBefore(
        dateTime: LocalDateTime,
        condition: (entry: BookEntry) -> Boolean = { true }
    ): BookEntry? {
        return atFirstAfter(dateTime, condition) ?: atLastBefore(dateTime, condition)
    }

    fun atFirstAfter(odo: Int, condition: (entry: BookEntry) -> Boolean = { true }): BookEntry? {
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

    fun atFirstAfter(dateTime: LocalDateTime, condition: (entry: BookEntry) -> Boolean = { true }): BookEntry? {
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

    fun atLastBefore(odo: Int, condition: (entry: BookEntry) -> Boolean = { true }): BookEntry? {
        atEnd()
        while (hasPrevious()) {
            previous()
            if ((current().odometer ?: odo) <= odo && condition(current())) {
                return current()
            }
        }
        previous()
        return null
    }

    fun atLastBefore(dateTime: LocalDateTime, condition: (entry: BookEntry) -> Boolean = { true }): BookEntry? {
        atEnd()
        while (hasPrevious()) {
            previous()
            if ((current().dateTime ?: dateTime) <= dateTime && condition(current())) {
                return current()
            }
        }
        previous()
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
    fun atLast(): TraversableEntries {
        cursor = sortedContent.lastIndex
        return this
    }

    fun atFirst(): TraversableEntries {
        cursor = 0
        return this
    }

    fun atEnd(): TraversableEntries {
        cursor = sortedContent.lastIndex + 1
        return this
    }

    fun atStart(): TraversableEntries {
        cursor = -1
        return this
    }
}
