package bilboka.core.report

import bilboka.core.book.domain.BookEntry
import bilboka.core.book.domain.EntryType
import bilboka.core.book.domain.EventType
import bilboka.core.book.domain.MaintenanceItem
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import kotlin.random.Random.Default.nextInt


internal class ReportGeneratorTest {
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        const val CREATE_FILE = false
        const val FILE_PREFIX = "testrapport"
    }

    @Test
    fun testReport() {
        val report = ReportGenerator().generateReport("En testrapport!", mockEntries())
        saveFile(report)
    }

    private fun mockEntries(): List<BookEntry> {
        return mutableListOf<BookEntry>().apply {
            repeat(10) {
                mockk<BookEntry>(relaxed = true).apply {
                    every { dateTime } returns LocalDateTime.now().minusDays(nextInt(until = 10).toLong())
                    every { odometer } returns 12345
                    every { type } returns EntryType.FUEL
                    every { amount } returns 34.8
                    every { costNOK } returns 456.9
                    every { maintenanceItem } returns null
                    every { isFullTank } returns true
                }.also { add(it) }
            }
            repeat(4) {
                mockk<BookEntry>(relaxed = true).apply {
                    every { dateTime } returns LocalDateTime.now().minusDays(nextInt(until = 10).toLong())
                    every { odometer } returns 23566
                    every { type } returns EntryType.MAINTENANCE
                    every { amount } returns null
                    every { costNOK } returns 456.9
                    every { maintenanceItem } returns mockk<MaintenanceItem>().apply { every { item } returns "TING" }
                    every { isFullTank } returns true
                    every { comment } returns "Heisann"
                }.also { add(it) }
            }
            repeat(4) {
                mockk<BookEntry>(relaxed = true).apply {
                    every { dateTime } returns LocalDateTime.now().minusDays(nextInt(until = 10).toLong())
                    every { odometer } returns null
                    every { type } returns EntryType.EVENT
                    every { amount } returns null
                    every { event } returns EventType.EU_KONTROLL_OK
                    every { comment } returns "Jauda"
                }.also { add(it) }
            }
            mockk<BookEntry>(relaxed = true).apply {
                every { dateTime } returns null
                every { odometer } returns 23566
                every { type } returns EntryType.EVENT
                every { event } returns EventType.SERVICE
                every { comment } returns "Ingen dato her nei"
            }.also { add(it) }
        }.shuffled()
    }

    private fun saveFile(report: ByteArray) {
        if (CREATE_FILE) {
            val filename = Paths.get("build/tmp/test", "report_test")
            Files.createDirectories(filename)

            Files.createTempFile(filename, FILE_PREFIX, ".pdf")
                .let { Files.write(it, report) }
                .also { logger.warn("Generert testfil: $it") }
        }
    }
}
