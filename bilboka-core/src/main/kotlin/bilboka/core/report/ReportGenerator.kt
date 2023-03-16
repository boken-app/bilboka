package bilboka.core.report

import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.xhtmlrenderer.pdf.ITextRenderer
import java.io.ByteArrayOutputStream

@Service
class ReportGenerator {
    companion object {
        val TEMPLATE_NAME = "report_template"
    }

    fun generateReport(input: String): ByteArray {
        return generatePdfFromHtml(parseThymeleafTemplate(Context().apply {
            setVariable("msg", "(fra rapportgenerator)")
            setVariable("input", input)
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
