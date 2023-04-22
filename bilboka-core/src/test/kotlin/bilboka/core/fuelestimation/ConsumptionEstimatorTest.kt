package bilboka.core.fuelestimation

import bilboka.core.book.domain.BookEntry
import bilboka.core.book.domain.EntryType
import bilboka.core.vehicle.domain.OdometerUnit
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset.offset
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ConsumptionEstimatorTest {

    @Nested
    inner class Estimation {

        @Test
        fun noEstimateWhenNoAmounts() {
            assertThat(
                ConsumptionEstimator.lastEstimate(
                    listOf(
                        bookEntryWhere {
                            every { type } returns EntryType.MAINTENANCE
                        },
                        bookEntryWhere {
                            every { type } returns EntryType.FUEL
                            every { odometer } returns 1200
                        },
                        bookEntryWhere {
                            every { type } returns EntryType.FUEL
                            every { odometer } returns 1400
                        },
                        bookEntryWhere {
                            every { type } returns EntryType.BASIC
                        },
                    )
                )
            ).isNull()
        }

        @Test
        fun canNotEstimateFromSingleEntry() {
            assertThat(
                ConsumptionEstimator.lastEstimate(
                    listOf(
                        bookEntryWhere {
                            every { type } returns EntryType.BASIC
                        },
                        bookEntryWhere {
                            every { type } returns EntryType.FUEL
                            every { odometer } returns 1200
                            every { amount } returns 200.0
                            every { isFullTank } returns true
                        },
                        bookEntryWhere {
                            every { type } returns EntryType.BASIC
                        },
                    )
                )
            ).isNull()
        }

        @Test
        fun lastEstimateFromTwoFuelEntries() {
            assertThat(
                ConsumptionEstimator.lastEstimate(
                    listOf(
                        bookEntryWhere {
                            every { type } returns EntryType.FUEL
                            every { odometer } returns 1000
                            every { isFullTank } returns true
                        },
                        bookEntryWhere {
                            every { type } returns EntryType.FUEL
                            every { odometer } returns 1100
                            every { amount } returns 100.0
                            every { isFullTank } returns true
                        },
                    )
                )?.amountPerDistanceUnit
            ).isEqualTo(1.0)
        }

        @Test
        fun lastEstimateFromSeveralFuelEntries() {
            assertThat(
                ConsumptionEstimator.lastEstimate(
                    listOf(
                        bookEntryWhere {
                            every { type } returns EntryType.FUEL
                            every { odometer } returns 1000
                            every { amount } returns 50.0
                            every { isFullTank } returns true
                        },
                        bookEntryWhere {
                            every { type } returns EntryType.FUEL
                            every { odometer } returns 1100
                            every { amount } returns 90.0
                            every { isFullTank } returns true
                        },
                        bookEntryWhere {
                            every { type } returns EntryType.FUEL
                            every { odometer } returns 1200
                            every { amount } returns 200.0
                            every { isFullTank } returns true
                        },
                    )
                )?.amountPerDistanceUnit
            ).isEqualTo(2.0)
        }

        @Test
        fun lastEstimateFromSeveralEntries() {
            assertThat(
                ConsumptionEstimator.lastEstimate(
                    listOf(
                        bookEntryWhere {
                            every { type } returns EntryType.FUEL
                            every { odometer } returns 1000
                            every { isFullTank } returns true
                        },
                        bookEntryWhere {
                            every { type } returns EntryType.FUEL
                            every { odometer } returns 1100
                            every { amount } returns 90.0
                            every { isFullTank } returns true
                        },
                        bookEntryWhere {
                            every { type } returns EntryType.MAINTENANCE
                            every { odometer } returns 1100
                        },
                        bookEntryWhere {
                            every { type } returns EntryType.FUEL
                            every { odometer } returns 1200
                            every { amount } returns 200.0
                            every { isFullTank } returns true
                        },
                    )
                )?.amountPerDistanceUnit
            ).isEqualTo(2.0)
        }

        @Test
        fun lastEstimateFromSeveralFuelEntriesWithSomeNotFull() {
            assertThat(
                ConsumptionEstimator.lastEstimate(
                    listOf(
                        bookEntryWhere {
                            every { type } returns EntryType.FUEL
                            every { odometer } returns 1000
                            every { isFullTank } returns true
                        },
                        bookEntryWhere {
                            every { type } returns EntryType.FUEL
                            every { odometer } returns 1100
                            every { amount } returns 80.0
                        },
                        bookEntryWhere {
                            every { type } returns EntryType.FUEL
                            every { odometer } returns 1150
                            every { amount } returns 30.0
                        },
                        bookEntryWhere {
                            every { type } returns EntryType.FUEL
                            every { odometer } returns 1200
                            every { amount } returns 90.0
                            every { isFullTank } returns true
                        },
                    )
                )?.amountPerDistanceUnit
            ).isEqualTo(1.0)
        }

        @Test
        fun lastEstimateWhenLastFullEntryDidNotHaveAmount() {
            assertThat(
                ConsumptionEstimator.lastEstimate(
                    listOf(
                        bookEntryWhere {
                            every { type } returns EntryType.FUEL
                            every { odometer } returns 1000
                            every { amount } returns 50.0
                            every { isFullTank } returns true
                        },
                        bookEntryWhere {
                            every { type } returns EntryType.FUEL
                            every { odometer } returns 1100
                            every { amount } returns 200.0
                        },
                        bookEntryWhere {
                            every { type } returns EntryType.FUEL
                            every { odometer } returns 1200
                            every { amount } returns 100.0
                        },
                        bookEntryWhere {
                            every { odometer } returns 1200
                            every { isFullTank } returns true
                        },
                    )
                )?.amountPerDistanceUnit
            ).isEqualTo(1.5)
        }

        @Test
        fun lastEstimateWhenTwoEntriesHasSameOdo() {
            assertThat(
                ConsumptionEstimator.lastEstimate(
                    listOf(
                        bookEntryWhere {
                            every { type } returns EntryType.FUEL
                            every { odometer } returns 1000
                            every { amount } returns 50.0
                            every { isFullTank } returns true
                        },
                        bookEntryWhere {
                            every { type } returns EntryType.FUEL
                            every { odometer } returns 1100
                            every { amount } returns 20.0
                        },
                        bookEntryWhere {
                            every { type } returns EntryType.FUEL
                            every { odometer } returns 1100
                            every { amount } returns 80.0
                            every { isFullTank } returns true
                        },
                    )
                )?.amountPerDistanceUnit
            ).isEqualTo(1.0)
        }
    }

    @Nested
    inner class Units {

        @Test
        fun litersPer10KmCalculatedFromKilometers() {
            assertThat(
                ConsumptionEstimationResult(
                    1.0, bookEntryWhere { }, bookEntryWhere { }, OdometerUnit.KILOMETERS
                ).litersPer10Km()
            ).isEqualTo(10.0)
        }

        @Test
        fun litersPer10KmCalculatedFromMiles() {
            assertThat(
                ConsumptionEstimationResult(
                    1.0, bookEntryWhere { }, bookEntryWhere { }, OdometerUnit.MILES
                ).litersPer10Km()
            ).isEqualTo(6.21, offset(0.1))
        }
    }

}

fun bookEntryWhere(stuff: BookEntry.() -> Unit): BookEntry {
    return mockk<BookEntry>(relaxed = true).apply {
        stuff()
        every { compareTo(any()) } answers { callOriginal() }
    }
}
