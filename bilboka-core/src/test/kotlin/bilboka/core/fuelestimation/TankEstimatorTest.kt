package bilboka.core.fuelestimation

import bilboka.core.book.domain.BookEntry
import bilboka.core.book.domain.EntryType
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TankEstimatorTest {

    private var lastFullAt: Int = 0

    private val entriesGivingTwoLitersPerUnit = listOf(
        bookEntryWhere {
            every { type } returns EntryType.FUEL
            every { odometer } returns 1000
            every { isFullTank } returns true
        },
        bookEntryWhere {
            every { type } returns EntryType.FUEL
            every { odometer } returns 1050.also { lastFullAt = it }
            every { amount } returns 100.0
            every { isFullTank } returns true
        },
    )

    private val entriesGivingTwoLitersPerUnitWith10LiterExtraFilled = listOf(
        bookEntryWhere {
            every { type } returns EntryType.FUEL
            every { odometer } returns 1000
            every { isFullTank } returns true
        },
        bookEntryWhere {
            every { type } returns EntryType.FUEL
            every { odometer } returns 1050.also { lastFullAt = it }
            every { amount } returns 100.0
            every { isFullTank } returns true
        },
        bookEntryWhere {
            every { type } returns EntryType.FUEL
            every { odometer } returns 1060
            every { amount } returns 10.0
            every { isFullTank } returns false
        },
    )

    @Test
    fun estimateNullWithoutData() {
        assertThat(TankEstimator.estimate(emptyList(), 10.0, 1000)).isNull()
    }

    @Test
    fun twoLitersPerDistance_consumes20LitersAfter10Distance() {
        val estimate = TankEstimator.estimate(entriesGivingTwoLitersPerUnit, 90.0, lastFullAt + 10)
        assertThat(estimate).isNotNull
        assertThat(estimate?.litersFromFull).isEqualTo(20.0)
    }

    @Test
    fun twoLitersPerDistanceAnd90LiterTank_consumes60LitersAndHas30LeftAfter30Distance() {
        val estimate =
            TankEstimator.estimate(entries = entriesGivingTwoLitersPerUnit, 90.0, currentOdo = lastFullAt + 30)
        assertThat(estimate).isNotNull
        assertThat(estimate?.litersFromFull).isEqualTo(60.0)
        assertThat(estimate?.litersFromEmpty).isEqualTo(30.0)
    }

    @Test
    fun twoLitersPerDistanceAnd80LiterTank_consumes60LitersAndHas25PercentLeftAfter30Distance() {
        val estimate =
            TankEstimator.estimate(entries = entriesGivingTwoLitersPerUnit, 80.0, currentOdo = lastFullAt + 30)
        assertThat(estimate).isNotNull
        assertThat(estimate?.litersFromFull).isEqualTo(60.0)
        assertThat(estimate?.percentFull()).isEqualTo(25.0)
    }

    @Test
    fun twoLitersPerDistanceAnd90LiterTank_consumes60LitersAndHas15DistanceLeftAfter30Distance() {
        val estimate =
            TankEstimator.estimate(entries = entriesGivingTwoLitersPerUnit, 90.0, currentOdo = lastFullAt + 30)
        assertThat(estimate?.distanceFromEmpty).isEqualTo(15.0)
    }

    @Test
    fun filledAfterLastFullTank_addsThatToTankEstimate() {
        val estimate =
            TankEstimator.estimate(
                entries = entriesGivingTwoLitersPerUnitWith10LiterExtraFilled,
                80.0,
                currentOdo = lastFullAt + 30
            )
        assertThat(estimate).isNotNull
        assertThat(estimate?.litersFromFull).isEqualTo(50.0)
        assertThat(estimate?.litersFromEmpty).isEqualTo(30.0)
    }

    @Test
    fun odoBeforeLastFull_throwsException() {
        assertThrows<TankEstimationException> {
            TankEstimator.estimate(
                entriesGivingTwoLitersPerUnit,
                90.0,
                lastFullAt - 10
            )
        }
    }

    @Test
    fun odoAfterEmpty_throwsException() {
        assertThrows<TankEstimationException> {
            TankEstimator.estimate(
                entriesGivingTwoLitersPerUnit,
                90.0,
                lastFullAt + 50
            )
        }
    }

    @Test
    fun lotsOfFillingsGiveLowAccuracy() {
        val estimate =
            TankEstimator.estimate(
                entries = listOf(
                    bookEntryWhere {
                        every { type } returns EntryType.FUEL
                        every { odometer } returns 1000
                        every { isFullTank } returns true
                    },
                    bookEntryWhere {
                        every { type } returns EntryType.FUEL
                        every { odometer } returns 1050.also { lastFullAt = it }
                        every { amount } returns 5.0
                        every { isFullTank } returns true
                    },
                    bookEntryWhere {
                        every { type } returns EntryType.FUEL
                        every { odometer } returns 1060
                        every { amount } returns 10.0
                        every { isFullTank } returns false
                    },
                    bookEntryWhere {
                        every { type } returns EntryType.FUEL
                        every { odometer } returns 2160
                        every { amount } returns 100.0
                        every { isFullTank } returns false
                    },
                    bookEntryWhere {
                        every { type } returns EntryType.FUEL
                        every { odometer } returns 3160
                        every { amount } returns 100.0
                        every { isFullTank } returns false
                    },
                    bookEntryWhere {
                        every { type } returns EntryType.FUEL
                        every { odometer } returns 4360
                        every { amount } returns 100.0
                        every { isFullTank } returns false
                    },
                ),
                120.0,
                currentOdo = 4370
            )
        assertThat(estimate).isNotNull
        assertThat(estimate?.accuracy).isLessThan(0.5)
    }

    @Test
    fun littleFillingsGiveHighAccuracy() {
        val estimate = TankEstimator.estimate(
            listOf(
                bookEntryWhere {
                    every { type } returns EntryType.FUEL
                    every { odometer } returns 1000
                    every { isFullTank } returns true
                },
                bookEntryWhere {
                    every { type } returns EntryType.FUEL
                    every { odometer } returns 1050.also { lastFullAt = it }
                    every { amount } returns 100.0
                    every { isFullTank } returns true
                },
            ), 90.0, lastFullAt + 10
        )

        assertThat(estimate?.litersFromFull).isEqualTo(20.0)
        assertThat(estimate?.accuracy).isGreaterThan(0.5)
        assertThat(estimate?.accuracy).isNotEqualTo(1.0)
    }

    @Test
    fun estimateAtFullGivesFullAccuracy() {
        val estimate = TankEstimator.estimate(entriesGivingTwoLitersPerUnit, 90.0, lastFullAt)

        assertThat(estimate?.litersFromFull).isEqualTo(0.0)
        assertThat(estimate?.accuracy).isEqualTo(1.0)
    }

    @Test
    fun filledMoreThanEstimatedLeft_cutsDownFullTankEstimate() {
        val entries = listOf(
            bookEntryWhere {
                every { type } returns EntryType.FUEL
                every { odometer } returns 1000
                every { isFullTank } returns true
            },
            bookEntryWhere {
                every { type } returns EntryType.FUEL
                every { odometer } returns 1100.also { lastFullAt = it }
                every { amount } returns 100.0
                every { isFullTank } returns true
            },
            bookEntryWhere {
                every { type } returns EntryType.FUEL
                every { odometer } returns 1200
                every { amount } returns 120.0
                every { isFullTank } returns false
            },
        )
        val estimateAtLastFueling = TankEstimator.estimate(entries, 150.0, 1200)
        assertThat(estimateAtLastFueling?.litersFromFull).isEqualTo(0.0)
        val estimateAtSomewhereAfterLastFueling = TankEstimator.estimate(entries, 150.0, 1300)
        assertThat(estimateAtSomewhereAfterLastFueling?.litersFromFull).isEqualTo(100.0)
    }
}

private fun bookEntryWhere(stuff: BookEntry.() -> Unit): BookEntry {
    return mockk<BookEntry>(relaxed = true).apply {
        stuff()
        every { compareTo(any()) } answers { callOriginal() }
    }
}
