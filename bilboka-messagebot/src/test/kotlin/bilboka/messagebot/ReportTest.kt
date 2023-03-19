package bilboka.messagebot

import io.mockk.Called
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test


internal class ReportTest : AbstractMessageBotTest() {

    @Test
    fun getReport() {
        every { book.getReport(any()) } returns "rapport-test".toByteArray()
        mockVehicle("databil")

        messagebot.processMessage("rapport databil", registeredSenderID)

        verify { book.getReport(any()) }
        verify { botMessenger.sendPdf(any(), any(), registeredSenderID) }
    }

    @Test
    fun getReportNotRegistered() {
        messagebot.processMessage("rapport", unregisteredSenderID)

        verify { book wasNot Called }
    }

}
