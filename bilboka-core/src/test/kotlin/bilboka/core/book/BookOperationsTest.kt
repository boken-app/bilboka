package bilboka.core.book

import bilboka.core.book.domain.BookEntry
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime.now

class BookOperationsTest {

    @Nested
    inner class ClosestToDate {

        var firstOne: BookEntry
        var aYearAgo: BookEntry
        var almostAYearAgo: BookEntry
        var yesterday: BookEntry

        val bookEntries = listOf(
            bookEntryWhere { every { dateTime } returns now().minusYears(1).minusWeeks(1) }.also { firstOne = it },
            bookEntryWhere {
                every { dateTime } returns now().minusYears(1)
                every { odometer } returns null
            }.also { aYearAgo = it },
            bookEntryWhere { every { dateTime } returns now().minusYears(1).plusDays(1) }.also { almostAYearAgo = it },
            bookEntryWhere { every { dateTime } returns now().minusDays(1) }.also { yesterday = it },
            bookEntryWhere { every { dateTime } returns null },
        )

        @Test
        fun noEntries() {
            assertThat(listOf<BookEntry>().entryClosestTo(now())).isNull()
        }

        @Test
        fun closestToNow_IsYesterday() {
            assertThat(bookEntries.entryClosestTo(now())).isEqualTo(yesterday)
        }

        @Test
        fun exactlyAYearAgo() {
            assertThat(bookEntries.entryClosestTo(now().minusYears(1))).isEqualTo(aYearAgo)
        }

        @Test
        fun aDayBeforeAYearAgo_ReturnsAYearAgo() {
            assertThat(bookEntries.entryClosestTo(now().minusYears(1).minusDays(1))).isEqualTo(aYearAgo)
        }

        @Test
        fun closestToTwoYearsAgo_isFirstOne() {
            assertThat(bookEntries.entryClosestTo(now().minusYears(2))).isEqualTo(firstOne)
        }

        @Test
        fun closestToTomorrow_isYesterday() {
            assertThat(bookEntries.entryClosestTo(now().plusDays(1))).isEqualTo(yesterday)
        }

        @Test
        fun exactlyAYearAgoButWithOdo() {
            assertThat(bookEntries.entryClosestTo(now().minusYears(1)) { it.odometer != null })
                .isEqualTo(almostAYearAgo)
        }
    }

    @Nested
    inner class ClosestToOdometer {

        var firstOne: BookEntry
        var oneKAgo: BookEntry
        var justBefore: BookEntry

        val bookEntries = listOf(
            bookEntryWhere { every { odometer } returns 500 }.also { firstOne = it },
            bookEntryWhere { every { odometer } returns 2000 }.also { oneKAgo = it },
            bookEntryWhere { every { odometer } returns 2100 },
            bookEntryWhere { every { odometer } returns 2900 }.also { justBefore = it },
            bookEntryWhere { every { odometer } returns null },
        )

        @Test
        fun noEntries() {
            assertThat(listOf<BookEntry>().entryClosestTo(3000)).isNull()
        }

        @Test
        fun closestToNow_IsYesterday() {
            assertThat(bookEntries.entryClosestTo(3000)).isEqualTo(justBefore)
        }

        @Test
        fun exactlyAYearAgo() {
            assertThat(bookEntries.entryClosestTo(2000)).isEqualTo(oneKAgo)
        }

        @Test
        fun aDayBeforeAYearAgo_ReturnsAYearAgo() {
            assertThat(bookEntries.entryClosestTo(1900)).isEqualTo(oneKAgo)
        }

        @Test
        fun closestToTwoYearsAgo_isFirstOne() {
            assertThat(bookEntries.entryClosestTo(10)).isEqualTo(firstOne)
        }

        @Test
        fun closestToTomorrow_isYesterday() {
            assertThat(bookEntries.entryClosestTo(3400)).isEqualTo(justBefore)
        }
    }

    private fun bookEntryWhere(stuff: BookEntry.() -> Unit): BookEntry {
        return mockk<BookEntry>(relaxed = true).apply {
            stuff()
            every { compareTo(any()) } answers { callOriginal() }
        }
    }

}
