package bilboka.messenger.service

import bilboka.messenger.consumer.MessengerWebhookConsumer
import bilboka.messenger.dto.FacebookEntry
import bilboka.messenger.dto.FacebookMessage
import bilboka.messenger.dto.FacebookMessaging
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
internal class MessageResponderServiceTest {

    @MockK
    lateinit var consumer: MessengerWebhookConsumer

    @InjectMockKs
    var responderService = MessageResponderService()

    @Test
    fun messageResponder() {
        val slot = slot<FacebookMessage>()

        justRun { consumer.sendMessage(capture(slot)) }

        val testMessage = "Hei test!"

        responderService.handleMessage(
            messageWithText(testMessage)
        )

        verify { consumer.sendMessage(any()) }

        assertThat(slot.captured.message).anySatisfy { k, v ->
            assertThat(k).isEqualTo("text")
            assertThat(v).contains(testMessage)
        }
    }

    private fun messageWithText(testMessage: String) = FacebookEntry(
        time = 3L,
        id = "1",
        messaging = listOf(
            FacebookMessaging(
                sender = mapOf(Pair("id", "123")),
                timestamp = 2L,
                message = FacebookMessage(
                    sender = mapOf(Pair("id", "123")),
                    message = mapOf(Pair("text", testMessage))
                )
            )
        )
    )

}