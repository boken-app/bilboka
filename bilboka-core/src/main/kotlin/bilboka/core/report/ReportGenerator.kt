package bilboka.core.report

import bilboka.core.book.domain.BookEntry
import bilboka.core.book.domain.EntryType
import bilboka.core.book.domain.sort
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.xhtmlrenderer.pdf.ITextRenderer
import java.io.ByteArrayOutputStream
import java.time.format.DateTimeFormatter.ofPattern

@Service
class ReportGenerator {
    companion object {
        const val TEMPLATE_NAME = "report_template"
    }

    fun generateReport(header: String, entries: List<BookEntry>): ByteArray {
        return generatePdfFromHtml(parseThymeleafTemplate(Context().apply {
            setVariable("header", header)
            setVariable("entries", entries.sort().map { it.toReportEntry() })
        }))
    }

    private fun parseThymeleafTemplate(context: Context): String {
        val templateEngine = TemplateEngine()
        ClassLoaderTemplateResolver().apply {
            suffix = ".html"
            templateMode = TemplateMode.HTML
        }.let {
            templateEngine.setTemplateResolver(it)
        }

        return templateEngine.process(TEMPLATE_NAME, context)
    }

    private fun generatePdfFromHtml(html: String): ByteArray {
        ByteArrayOutputStream().use {
            ITextRenderer().apply {
                setDocumentFromString(html)
                layout()
                createPDF(it)
            }
            return it.toByteArray()
        }
    }

}

fun BookEntry.toReportEntry(): ReportEntry {
    return ReportEntry(
        date = dateTime?.format(ofPattern("dd.MM.yyyy")) ?: "(ukjent)",
        odometer = odometer?.toString() ?: "(ukjent)",
        type = findType(),
        liters = amount?.toString() ?: "-",
        costNOK = costNOK?.toString() ?: "-",
        isFullTank = isFullTank?.let { if (it) "Ja" else "Nei" } ?: "-",
        comment = comment ?: ""
    )
}

fun BookEntry.findType(): String {
    return when (type) {
        EntryType.MAINTENANCE -> maintenanceItem?.item ?: "Udefinert vedlikehold"
        EntryType.EVENT -> event?.name ?: "Udefinert hendelse"
        EntryType.BASIC -> "-"
        else -> type.name
    }
}

class ReportEntry(
    val date: String,
    val odometer: String,
    val type: String,
    val liters: String,
    val costNOK: String,
    val isFullTank: String,
    val comment: String
)
