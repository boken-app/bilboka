package bilboka.core.fuelestimation

import bilboka.core.book.domain.BookEntry
import bilboka.core.book.domain.sort
import bilboka.core.vehicle.domain.OdometerUnit
import java.time.Period

// TODO Lage total-estimator som returnerer samling med estimater fra tidenes morgen
object ConsumptionEstimator {

    fun lastEstimate(entries: Collection<BookEntry>, odoUnit: OdometerUnit? = null): ConsumptionEstimationResult? {
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
                amountPerDistanceUnit = totalAmountFilled / (endEntry?.odometer!! - startEntry?.odometer!!),
                estimatedAt = endEntry!!,
                estimatedFrom = startEntry!!,
                odometerUnit = odoUnit
            )
        }

        return null
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
