package bilboka.core.fuelestimation

import bilboka.core.book.domain.BookEntry
import bilboka.core.vehicle.domain.OdometerUnit
import java.time.Period

interface FuelEstimationResult {
    val odometerUnit: OdometerUnit?
    val amountEstimate: Double
    val costEstimate: Double?
    fun estimationDistance(): Int

    fun amountPerDistance(): Double {
        return amountEstimate / estimationDistance()
    }

    fun costPerDistance(): Double? {
        return costEstimate?.div(estimationDistance())
    }

    fun litersPer10Km(): Double {
        return odometerUnit?.run {
            amountPerDistance() * (10 / conversionToKilometers())
        } ?: throw IllegalStateException("Mangler enhet for konvertering til kilometer")
    }
}

data class ConsumptionPointEstimationResult(
    val estimatedAt: BookEntry,
    val estimatedFrom: BookEntry,
    override val amountEstimate: Double,
    override val costEstimate: Double? = null,
    override val odometerUnit: OdometerUnit?
) : FuelEstimationResult {
    val estimationPeriod = estimatedFrom.dateTime?.let { from ->
        estimatedAt.dateTime?.let { to ->
            Period.between(
                from.toLocalDate(),
                to.toLocalDate()
            )
        }
    }

    override fun estimationDistance(): Int {
        return estimatedAt.odometer!! - estimatedFrom.odometer!!
    }
}

data class ContinousEstimationResult(
    val odoStart: Int,
    val odoEnd: Int,
    override val costEstimate: Double? = null,
    override val amountEstimate: Double,
    override val odometerUnit: OdometerUnit?
) : FuelEstimationResult {
    override fun estimationDistance(): Int {
        return odoEnd - odoStart
    }
}
