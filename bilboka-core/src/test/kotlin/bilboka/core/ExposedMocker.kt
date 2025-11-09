package bilboka.core

import bilboka.core.book.domain.BookEntry
import io.mockk.every
import io.mockk.mockk
import org.jetbrains.exposed.sql.SizedIterable

fun mockedEntry(mockFn: BookEntry.() -> Unit): BookEntry = mockk<BookEntry>(relaxed = true) { mockFn() }

fun mockedBookEntries(vararg entries: BookEntry): SizedIterable<BookEntry> {
    return mockk<SizedIterable<BookEntry>>(relaxed = true) {
        every { iterator() } returns entries.iterator()
    }
}
