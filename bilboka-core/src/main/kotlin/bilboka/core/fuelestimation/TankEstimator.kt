package bilboka.core.fuelestimation

import bilboka.core.ImpossibleBilbokaActionException
import bilboka.core.book.domain.BookEntry
import bilboka.core.book.domain.EntryType
import bilboka.core.book.domain.sort

object TankEstimator {

    fun estimate(entries: Collection<BookEntry>, tankVolume: Double, currentOdo: Int): TankEstimateResult? {
        val lastEstimate = ConsumptionEstimator.lastEstimate(entries) ?: return null

        val sortedEntries = entries.sort()
        val lastFull = sortedEntries.lastOrNull { it.isFullTank == true && it.odometer != null }

        val litersFromFull = lastFull?.let {
            entries.amountFromFullEstimatedFrom(it, lastEstimate.amountPerDistanceUnit, currentOdo, tankVolume)
        } ?: 0.0

        return lastFull?.odometer
            ?.let { currentOdo - it }
            ?.also { if (it < 0) throw TankEstimationException("Estimatpunkt angitt til å være før siste full tank") }
            ?.let { drivenSinceLastConfirmedFull ->
                val consumptionSinceLastConfirmedFull =
                    lastEstimate.amountPerDistanceUnit * drivenSinceLastConfirmedFull
                makeResult(
                    litersFromFull = litersFromFull,
                    tankVolume = tankVolume,
                    consumptionPerDistance = lastEstimate.amountPerDistanceUnit,
                    accuracy = 1 - (consumptionSinceLastConfirmedFull / (tankVolume + consumptionSinceLastConfirmedFull))
                )
            }
    }

    private fun Collection<BookEntry>.amountFromFullEstimatedFrom(
        lastFull: BookEntry,
        consumptionPerDistance: Double,
        estimationPointOdo: Int,
        tankVolume: Double
    ): Double {
        sort().run {
            var currentEstimateLeftToFull = 0.0
            var lastOdometer =
                lastFull.odometer ?: throw IllegalStateException("Mangler kilometerstand på siste fulle tank")

            forEach {
                if (it > lastFull) {
                    val odoAtCurrent = it.odometer

                    if (odoAtCurrent != null) {
                        currentEstimateLeftToFull += (odoAtCurrent - lastOdometer) * consumptionPerDistance
                        currentEstimateLeftToFull = currentEstimateLeftToFull.coerceIn(0.0, tankVolume)
                        lastOdometer = odoAtCurrent
                    }
                    if (it.type == EntryType.FUEL) {
                        currentEstimateLeftToFull -= it.amount ?: 0.0
                        currentEstimateLeftToFull = currentEstimateLeftToFull.coerceIn(0.0, tankVolume)
                    }
                }
            }
            return (currentEstimateLeftToFull + (estimationPointOdo - lastOdometer) * consumptionPerDistance)
                .coerceIn(0.0, tankVolume)
        }
    }

    private fun makeResult(
        litersFromFull: Double,
        tankVolume: Double,
        consumptionPerDistance: Double,
        accuracy: Double
    ): TankEstimateResult {
        val litersFromEmpty = litersFromEmpty(tankVolume, litersFromFull)
        return TankEstimateResult(
            fillRatio = litersFromEmpty / tankVolume,
            litersFromFull = litersFromFull,
            litersFromEmpty = litersFromEmpty,
            distanceFromEmpty = litersFromEmpty / consumptionPerDistance,
            accuracy = accuracy
        )
    }

    private fun litersFromEmpty(tankVolume: Double, litersFromFull: Double): Double {
        return (tankVolume - litersFromFull)
            .also { if (it < 0) throw TankEstimationException("Estimert forbruk er strørre enn angitt tankvolum") }
    }
}

data class TankEstimateResult(
    val fillRatio: Double,
    val litersFromFull: Double,
    val litersFromEmpty: Double,
    val distanceFromEmpty: Double,
    val accuracy: Double
) {
    fun percentFull(): Double {
        return fillRatio * 100
    }
}

class TankEstimationException(message: String) : ImpossibleBilbokaActionException(message)
