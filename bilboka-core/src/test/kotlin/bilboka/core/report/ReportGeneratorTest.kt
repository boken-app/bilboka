package bilboka.core.report

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream


internal class ReportGeneratorTest {
    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        val CREATE_FILE = false
        val LOCATION = "\\bilboka_test\\testrapport.pdf"
    }

    @Test
    fun testReport() {
        val report = ReportGenerator().generateReport("test-input")
        saveFile(report)
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
