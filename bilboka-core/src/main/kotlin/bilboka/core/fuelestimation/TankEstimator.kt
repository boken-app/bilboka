package bilboka.core.fuelestimation

import bilboka.core.book.domain.BookEntry
import bilboka.core.book.domain.sort

object TankEstimator {

    fun estimate(entries: Collection<BookEntry>, tankVolume: Double, currentOdo: Int): TankEstimateResult? {
        val lastEstimate = ConsumptionEstimator.lastEstimate(entries) ?: return null

        return entries.lastFullAt()
            ?.also { if (it > currentOdo) throw TankEstimationException("Estimatpunkt angitt til å være før siste full tank") }
            ?.let { (currentOdo - it) * lastEstimate.amountPerDistanceUnit }
            ?.let {
                makeResult(
                    litersConsumed = it,
                    tankVolume = tankVolume,
                    consumptionPerDistance = lastEstimate.amountPerDistanceUnit
                )
            }
    }

    private fun Collection<BookEntry>.lastFullAt(): Int? {
        return sort().lastOrNull { it.isFullTank == true && it.odometer != null }?.odometer
    }

    private fun makeResult(
        litersConsumed: Double,
        tankVolume: Double,
        consumptionPerDistance: Double
    ): TankEstimateResult {
        val litersFromEmpty = litersFromEmpty(tankVolume, litersConsumed)
        return TankEstimateResult(
            fillRatio = litersFromEmpty / tankVolume,
            litersFromFull = litersConsumed,
            litersFromEmpty = litersFromEmpty,
            distanceFromEmpty = litersFromEmpty / consumptionPerDistance
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
    val distanceFromEmpty: Double
) {
    fun percentFull(): Double {
        return fillRatio * 100
    }
}

class TankEstimationException(message: String) : IllegalStateException(message)
