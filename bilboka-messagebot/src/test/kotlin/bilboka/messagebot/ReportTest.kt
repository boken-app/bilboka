package bilboka.messagebot

import io.mockk.Called
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test


internal class ReportTest : AbstractMessageBotTest() {

    @Test
    fun getReport() {
        val vehicle = mockVehicle("databil")
        every { book.getReport(vehicle) } returns "rapport-test".toByteArray()

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
