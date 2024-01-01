package bilboka.web

import bilboka.messenger.consumer.MessengerSendAPIConsumer
import bilboka.messenger.dto.FacebookEntry
import bilboka.messenger.dto.MessageReceived
import bilboka.messenger.dto.MessagingReceived
import bilboka.messenger.dto.MessengerWebhookRequest
import bilboka.messenger.resource.MessengerWebhookConfig
import bilboka.messenger.resource.MessengerWebhookResource
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.clearAllMocks
import io.mockk.justRun
import io.mockk.verify
import org.apache.commons.codec.binary.Hex
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@ExtendWith(SpringExtension::class)
@WebMvcTest(
    MessengerWebhookResource::class, properties = [
        "messenger.verify-token = detteerettesttoken",
        "messenger.appSecret = totallyLegitIPromise",
    ]
)
@ContextConfiguration(classes = [MessengerWebhookIntegrationTest.TestConfig::class])
class MessengerWebhookIntegrationTest {
    @MockkBean
    lateinit var messengerSendAPIConsumer: MessengerSendAPIConsumer

    @Autowired
    lateinit var mvc: MockMvc

    @BeforeEach
    fun setUp() {
        justRun { messengerSendAPIConsumer.sendMessage(any()) }
    }

    @AfterEach
    fun clear() {
        clearAllMocks()
    }

    @Nested
    inner class GetWebhookTests {

        @Test
        fun getRequestWithoutParams_returnsBadRequest() {
            mvc.perform(
                MockMvcRequestBuilders.get("/${MessengerWebhookConfig.WEBHOOK_URL}")
                    .contentType(MediaType.TEXT_HTML)
            )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
        }

        @Test
        fun validGetRequest_returnsChallengeAccepted() {
            mvc.perform(
                MockMvcRequestBuilders.get("/${MessengerWebhookConfig.WEBHOOK_URL}?hub.verify_token=detteerettesttoken&hub.challenge=CHALLENGE_ACCEPTED&hub.mode=subscribe")
                    .contentType(MediaType.TEXT_HTML)
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().string("CHALLENGE_ACCEPTED"))
        }

        @Test
        fun invalidToken_returns403() {
            mvc.perform(
                MockMvcRequestBuilders.get("/${MessengerWebhookConfig.WEBHOOK_URL}?hub.verify_token=blah&hub.challenge=CHALLENGE_ACCEPTED&hub.mode=subscribe")
                    .contentType(MediaType.TEXT_HTML)
            )
                .andExpect(MockMvcResultMatchers.status().isForbidden)
        }
    }

    @Nested
    inner class PostWebhookTests {
        @Test
        fun postRequestSomeList_returnsOk() {
            postAsJson(
                MessengerWebhookRequest(
                    requestObject = "page", entry = listOf(
                        FacebookEntry(id = "123", time = 123L, messaging = Collections.emptyList())
                    )
                )
            )
                .andExpect(MockMvcResultMatchers.status().isOk).andExpect(okResponseContent())

            verify(exactly = 0) { messengerSendAPIConsumer.sendMessage(any()) }
        }

        @Test
        fun postRequestSomeListWithMessaging_returnsOk() {
            postAsJson(
                MessengerWebhookRequest(
                    requestObject = "page", entry = listOf(
                        FacebookEntry(
                            "123", 123L, listOf(
                                MessagingReceived(
                                    1234L,
                                    null,
                                    personWithId("456"),
                                    personWithId("354"),
                                    MessageReceived(
                                        text = "Test"
                                    ),
                                    null
                                )
                            )
                        )
                    )
                )
            )
                .andExpect(MockMvcResultMatchers.status().isOk).andExpect(okResponseContent())

            verify { messengerSendAPIConsumer.sendMessage(any()) }
        }

        private fun okResponseContent() = MockMvcResultMatchers.content().string("EVENT_RECEIVED")

        private fun postAsJson(request: MessengerWebhookRequest): ResultActions {
            val jsonString = asJsonString(request)
            return mvc.perform(
                MockMvcRequestBuilders.post("/${MessengerWebhookConfig.WEBHOOK_URL}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-hub-signature-256", "sha256=${jsonString.hash("totallyLegitIPromise")}")
                    .content(jsonString)
            )
        }

        private fun personWithId(id: String): Map<String, String> {
            return mapOf(Pair("id", id))
        }

        private fun asJsonString(obj: Any): String {
            return try {
                ObjectMapper().writeValueAsString(obj)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

        private fun String.hash(key: String): String {
            val secretKeySpec = SecretKeySpec(key.toByteArray(), "HmacSHA256")
            val mac = Mac.getInstance("HmacSHA256")
            mac.init(secretKeySpec)
            return Hex.encodeHexString(mac.doFinal(this.toByteArray()))
        }
    }

    @Configuration
    @ComponentScan(basePackages = ["bilboka"])
    class TestConfig
}
