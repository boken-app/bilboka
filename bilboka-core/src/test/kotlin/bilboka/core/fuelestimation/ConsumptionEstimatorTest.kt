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
import java.time.LocalDate
import java.time.LocalTime.NOON

class ConsumptionEstimatorTest {

    @Nested
    inner class LastEstimate {

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
    inner class EstimateAtOdo {
        val entries = listOf(
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

        @Test
        fun estimateAtLastOne() {
            assertThat(
                ConsumptionEstimator.estimateAt(
                    entries,
                    1200
                )?.amountPerDistanceUnit
            ).isEqualTo(2.0)
        }

        @Test
        fun estimateBeforeLastOne_isSameAsLastOne() {
            assertThat(
                ConsumptionEstimator.estimateAt(
                    entries,
                    1150
                )?.amountPerDistanceUnit
            ).isEqualTo(2.0)
        }

        @Test
        fun estimateAtMiddleOne_isOtherEstimate() {
            assertThat(
                ConsumptionEstimator.estimateAt(
                    entries,
                    1100
                )?.amountPerDistanceUnit
            ).isEqualTo(0.9)
        }

        @Test
        fun estimateBeforeMiddleOne_isSameAsMiddleOne() {
            assertThat(
                ConsumptionEstimator.estimateAt(
                    entries,
                    1001
                )?.amountPerDistanceUnit
            ).isEqualTo(0.9)
        }

        @Test
        fun estimateAfterLastOne_isSameAsLast() {
            assertThat(
                ConsumptionEstimator.estimateAt(
                    entries,
                    1250
                )?.amountPerDistanceUnit
            ).isEqualTo(2.0)
        }

        @Test
        fun estimateBeforeFirstOne_isUnknown() {
            assertThat(
                ConsumptionEstimator.estimateAt(
                    entries,
                    900
                )?.amountPerDistanceUnit
            ).isNull()
        }
    }

    @Nested
    inner class EstimateAtTime {
        val entries = listOf(
            bookEntryWhere {
                every { type } returns EntryType.FUEL
                every { dateTime } returns LocalDate.of(2020, 1, 1).atTime(NOON)
                every { odometer } returns 1000
                every { isFullTank } returns true
            },
            bookEntryWhere {
                every { type } returns EntryType.FUEL
                every { dateTime } returns LocalDate.of(2020, 2, 1).atTime(NOON)
                every { odometer } returns 1100
                every { amount } returns 90.0
                every { isFullTank } returns true
            },
            bookEntryWhere {
                every { type } returns EntryType.MAINTENANCE
                every { dateTime } returns LocalDate.of(2020, 2, 10).atTime(NOON)
                every { odometer } returns 1100
            },
            bookEntryWhere {
                every { type } returns EntryType.FUEL
                every { dateTime } returns LocalDate.of(2020, 3, 10).atTime(NOON)
                every { odometer } returns 1200
                every { amount } returns 200.0
                every { isFullTank } returns true
            },
        )

        @Test
        fun estimateAtLastOne() {
            assertThat(
                ConsumptionEstimator.estimateAt(
                    entries,
                    LocalDate.of(2020, 3, 10).atTime(NOON)
                )?.amountPerDistanceUnit
            ).isEqualTo(2.0)
        }

        @Test
        fun estimateBeforeLastOne_isSameAsLastOne() {
            assertThat(
                ConsumptionEstimator.estimateAt(
                    entries,
                    LocalDate.of(2020, 2, 2).atTime(NOON)
                )?.amountPerDistanceUnit
            ).isEqualTo(2.0)
        }

        @Test
        fun estimateAtMiddleOne_isOtherEstimate() {
            assertThat(
                ConsumptionEstimator.estimateAt(
                    entries,
                    LocalDate.of(2020, 2, 1).atTime(NOON)
                )?.amountPerDistanceUnit
            ).isEqualTo(0.9)
        }

        @Test
        fun estimateBeforeMiddleOne_isSameAsMiddleOne() {
            assertThat(
                ConsumptionEstimator.estimateAt(
                    entries,
                    LocalDate.of(2020, 1, 15).atTime(NOON)
                )?.amountPerDistanceUnit
            ).isEqualTo(0.9)
        }

        @Test
        fun estimateAfterLastOne_isSameAsLast() {
            assertThat(
                ConsumptionEstimator.estimateAt(
                    entries,
                    LocalDate.of(2020, 5, 1).atTime(NOON)
                )?.amountPerDistanceUnit
            ).isEqualTo(2.0)
        }

        @Test
        fun estimateBeforeFirstOne_isUnknown() {
            assertThat(
                ConsumptionEstimator.estimateAt(
                    entries,
                    LocalDate.of(2019, 2, 1).atTime(NOON)
                )?.amountPerDistanceUnit
            ).isNull()
        }
    }

    @Nested
    inner class EstimateBetween {
        val entries = listOf(
            bookEntryWhere {
                every { type } returns EntryType.FUEL
                every { dateTime } returns LocalDate.of(2020, 1, 1).atTime(NOON)
                every { odometer } returns 1000
                every { isFullTank } returns true
            },
            bookEntryWhere {
                every { type } returns EntryType.FUEL
                every { dateTime } returns LocalDate.of(2020, 2, 1).atTime(NOON)
                every { odometer } returns 1100
                every { amount } returns 90.0
                every { isFullTank } returns true
            },
            bookEntryWhere {
                every { type } returns EntryType.MAINTENANCE
                every { dateTime } returns LocalDate.of(2020, 2, 10).atTime(NOON)
                every { odometer } returns 1100
            },
            bookEntryWhere {
                every { type } returns EntryType.FUEL
                every { dateTime } returns LocalDate.of(2020, 3, 10).atTime(NOON)
                every { odometer } returns 1200
                every { amount } returns 200.0
                every { isFullTank } returns true
            },
            bookEntryWhere {
                every { type } returns EntryType.EVENT
                every { dateTime } returns LocalDate.of(2020, 3, 20).atTime(NOON)
                every { odometer } returns 1300
            },
        )

        @Test
        fun estimateAtCloseToLast() {
            assertThat(
                ConsumptionEstimator.estimateBetween(
                    entries,
                    1110,
                    1200
                )?.amountPerDistanceUnit
            ).isEqualTo(2.0)
        }

        @Test
        fun estimateAll() {
            assertThat(
                ConsumptionEstimator.estimateBetween(
                    entries,
                    1000,
                    1200
                )?.amountPerDistanceUnit
            ).isEqualTo(1.45)
        }

        @Test
        fun estimateFirst() {
            assertThat(
                ConsumptionEstimator.estimateBetween(
                    entries,
                    1000,
                    1100
                )?.amountPerDistanceUnit
            ).isEqualTo(0.9)
        }

        @Test
        fun estimateAtCloseToLastTime() {
            assertThat(
                ConsumptionEstimator.estimateBetween(
                    entries,
                    LocalDate.of(2020, 2, 10).atTime(NOON),
                    LocalDate.of(2020, 3, 10).atTime(NOON)
                )?.amountPerDistanceUnit
            ).isEqualTo(2.0)
        }

        @Test
        fun estimateAllTimes() {
            assertThat(
                ConsumptionEstimator.estimateBetween(
                    entries,
                    LocalDate.of(2020, 1, 1).atTime(NOON),
                    LocalDate.of(2020, 3, 10).atTime(NOON)
                )?.amountPerDistanceUnit
            ).isEqualTo(1.45)
        }

        @Test
        fun estimateFirstTime() {
            assertThat(
                ConsumptionEstimator.estimateBetween(
                    entries,
                    LocalDate.of(2020, 1, 1).atTime(NOON),
                    LocalDate.of(2020, 2, 1).atTime(NOON)
                )?.amountPerDistanceUnit
            ).isEqualTo(0.9)
        }

        @Test
        fun estimateAtOtherEventAfterLastFuel_staysLikeLast() {
            assertThat(
                ConsumptionEstimator.estimateBetween(
                    entries,
                    LocalDate.of(2020, 2, 10).atTime(NOON),
                    LocalDate.of(2020, 3, 20).atTime(NOON)
                )?.amountPerDistanceUnit
            ).isEqualTo(2.0)
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

private fun bookEntryWhere(stuff: BookEntry.() -> Unit): BookEntry {
    return mockk<BookEntry>(relaxed = true).apply {
        stuff()
        every { compareTo(any()) } answers { callOriginal() }
    }
}
