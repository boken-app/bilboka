package bilboka.messenger.service

import bilboka.messagebot.MessageBot
import bilboka.messenger.consumer.MessengerWebhookConsumer
import bilboka.messenger.dto.FacebookEntry
import bilboka.messenger.dto.FacebookMessage
import bilboka.messenger.dto.FacebookMessaging
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class FacebookMessageHandlerTest {

    @MockK
    lateinit var consumer: MessengerWebhookConsumer

    @MockK
    lateinit var messageBot: MessageBot

    @InjectMockKs
    var responderService = FacebookMessageHandler()

    @Test
    fun messageResponder() {
        val slot = slot<FacebookMessaging>()
        val reply = "Et svar"

        justRun { consumer.sendMessage(capture(slot)) }
        every { messageBot.processMessage(any()) } returns reply

        val testMessage = "Hei test!"

        responderService.handleMessage(
            messageWithText(testMessage)
        )

        verify { consumer.sendMessage(any()) }
        verify { messageBot.processMessage(testMessage) }

        assertThat(slot.captured.message?.text).isEqualTo(reply)
    }

    private fun messageWithText(testMessage: String) = FacebookEntry(
        time = 3L,
        id = "1",
        messaging = listOf(
            FacebookMessaging(
                sender = mapOf(Pair("id", "123")),
                timestamp = 2L,
                message = FacebookMessage(
                    text = testMessage
                )
            )
        )
    )

}