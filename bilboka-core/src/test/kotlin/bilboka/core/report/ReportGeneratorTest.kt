package bilboka.core.report

import bilboka.core.book.domain.BookEntry
import bilboka.core.book.domain.EntryType
import bilboka.core.book.domain.MaintenanceItem
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime


internal class ReportGeneratorTest {
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        val CREATE_FILE = false
        val LOCATION = "\\bilboka_test\\testrapport.pdf"
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
                    every { dateTime } returns LocalDateTime.now()
                    every { type } returns EntryType.MAINTENANCE
                    every { amount } returns null
                    every { costNOK } returns 456.9
                    every { maintenanceItem } returns mockk<MaintenanceItem>().apply { every { item } returns "TING" }
                    every { isFullTank } returns true
                }.also { add(it) }
            }
        }
    }

    private fun saveFile(report: ByteArray) {
        if (CREATE_FILE) {
            FileOutputStream(System.getProperty("user.home") + File.separator.toString() + LOCATION).use {
                it.write(report)
                logger.warn("Generert testfil: $LOCATION")
            }
        }
    }
}
