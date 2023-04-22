package bilboka.core.fuelestimation

import bilboka.core.book.domain.BookEntry
import bilboka.core.book.domain.sort
import java.time.Period

// TODO Lage total-estimator som returnerer samling med estimater fra tidenes morgen
object ConsumptionEstimator {

    fun lastEstimate(entries: Collection<BookEntry>): ConsumptionEstimationResult? {
        var totalAmountFilled = 0.0

        var startEntry: BookEntry? = null
        var endEntry: BookEntry? = null
        val iterator = entries.sort().reversed().iterator()

        while (iterator.hasNext() && startEntry == null) {
            iterator.next().apply {
                if (isFullTank == true && odometer != null) {
                    if (endEntry == null) {
                        endEntry = this
                    } else {
                        startEntry = this
                    }
                }

                if (amount != null && startEntry == null && endEntry != null) {
                    totalAmountFilled += amount ?: 0.0
                }
            }
        }

        if (startEntry != null) {
            return ConsumptionEstimationResult(
                consumption = totalAmountFilled / (endEntry?.odometer!! - startEntry?.odometer!!),
                estimatedAt = endEntry!!,
                estimatedFrom = startEntry!!
            )
        }

        return null
    }
}

data class ConsumptionEstimationResult(
    val consumption: Double,
    val estimatedAt: BookEntry,
    val estimatedFrom: BookEntry
) {
    val estimationPeriod = estimatedFrom.dateTime?.let { from ->
        estimatedAt.dateTime?.let { to ->
            Period.between(
                from.toLocalDate(),
                to.toLocalDate()
            )
        }
    }
}
