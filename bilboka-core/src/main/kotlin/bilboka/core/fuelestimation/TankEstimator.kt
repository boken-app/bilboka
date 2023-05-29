package bilboka.core.fuelestimation

import bilboka.core.book.domain.BookEntry
import bilboka.core.book.domain.EntryType
import bilboka.core.book.domain.sort

object TankEstimator {

    fun estimate(entries: Collection<BookEntry>, tankVolume: Double, currentOdo: Int): TankEstimateResult? {
        val lastEstimate = ConsumptionEstimator.lastEstimate(entries) ?: return null
        val lastFull = entries.lastFull()
        val fueledSinceFull = lastFull?.let { entries.fuelFilledAfter(it) } ?: 0.0

        return lastFull?.odometer
            ?.also { if (it > currentOdo) throw TankEstimationException("Estimatpunkt angitt til å være før siste full tank") }
            ?.let { ((currentOdo - it) * lastEstimate.amountPerDistanceUnit) }
            ?.let { consumedSinceFull ->
                makeResult(
                    litersFromFull = consumedSinceFull - fueledSinceFull,
                    tankVolume = tankVolume,
                    consumptionPerDistance = lastEstimate.amountPerDistanceUnit,
                    accuracy = 1 - (consumedSinceFull / (tankVolume + consumedSinceFull))
                )
            }
    }

    private fun Collection<BookEntry>.lastFull(): BookEntry? {
        return sort().lastOrNull { it.isFullTank == true && it.odometer != null }
    }

    private fun Collection<BookEntry>.fuelFilledAfter(lastFull: BookEntry): Double {
        sort().run {
            var sum: Double = 0.0
            forEach {
                if (it > lastFull && it.type == EntryType.FUEL) {
                    sum += it.amount ?: 0.0
                }
            }
            return sum
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

class TankEstimationException(message: String) : IllegalStateException(message)
