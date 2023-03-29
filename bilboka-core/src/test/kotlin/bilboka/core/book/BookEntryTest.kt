package bilboka.core.book

import bilboka.core.book.domain.BookEntry
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDateTime.now


class BookEntryTest {

    private val correctOrder = listOf(
        bookEntryWhere { every { dateTime } returns now().minusDays(30) },
        bookEntryWhere { every { dateTime } returns now().minusDays(20) },
        bookEntryWhere { every { dateTime } returns now().minusDays(10) },
        bookEntryWhere {
            every { dateTime } returns now().minusHours(10)
            every { odometer } returns 1100
            every { creationTimestamp } returns Instant.now()
        },
        bookEntryWhere {
            every { dateTime } returns now().minusHours(9)
            every { odometer } returns 1000
        },
        bookEntryWhere {
            every { dateTime } returns null
            every { odometer } returns 1200
            every { creationTimestamp } returns Instant.now().minusSeconds(100)
        },
        bookEntryWhere {
            every { dateTime } returns null
            every { odometer } returns 1300
            every { creationTimestamp } returns Instant.now().minusSeconds(102)
        },
        bookEntryWhere {
            every { dateTime } returns now().minusHours(8)
            every { odometer } returns null
            every { creationTimestamp } returns Instant.now().minusSeconds(90)
        },
        bookEntryWhere {
            every { dateTime } returns now().minusHours(8)
            every { odometer } returns 1400
            every { creationTimestamp } returns Instant.now().minusSeconds(80)
        },
        bookEntryWhere {
            every { dateTime } returns now().minusHours(8)
            every { odometer } returns null
            every { creationTimestamp } returns Instant.now().minusSeconds(70)
        },
    )

    @Test
    fun testSorting() {
        assertThat(correctOrder.shuffled().sorted()).containsExactlyElementsOf(correctOrder)
    }
}

fun bookEntryWhere(stuff: BookEntry.() -> Unit): BookEntry {
    return mockk<BookEntry>(relaxed = true).apply {
        stuff()
        every { compareTo(any()) } answers { callOriginal() }
    }
}
