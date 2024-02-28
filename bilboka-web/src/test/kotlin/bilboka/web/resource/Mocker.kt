package bilboka.web.resource

import bilboka.core.book.domain.BookEntry
import bilboka.core.book.domain.EntryType
import io.mockk.every
import io.mockk.mockk
import org.jetbrains.exposed.sql.SizedIterable
import java.time.Instant
import java.time.LocalDateTime

object Mocker {
    fun mockedBookEntries(count: Int): SizedIterable<BookEntry> {
        val entries = (1..count).map { index ->
            mockk<BookEntry>(relaxed = true) {
                every { dateTime } returns LocalDateTime.now().minusDays(index.toLong() * 5)
                every { vehicle } returns mockk(relaxed = true)
                every { odometer } returns 123456 + index * 1000
                every { type } returns if (index % 2 == 0) EntryType.MAINTENANCE else EntryType.FUEL
                every { amount } returns 50.0 + index * 10
                every { costNOK } returns 750.0 + index * 100
                every { maintenanceItem } returns if (index % 2 == 0) mockk(relaxed = true) else null
                every { comment } returns "Entry $index comment"
                every { enteredBy } returns mockk(relaxed = true)
                every { source } returns if (index % 2 == 0) "Automatic" else "Manual"
                every { creationTimestamp } returns Instant.now()
            }
        }
        return mockk<SizedIterable<BookEntry>>(relaxed = true) {
            every { iterator() } returns entries.iterator()
        }
    }
}
