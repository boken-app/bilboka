package bilboka.messenger.consumer

import bilboka.messenger.MessengerProperties
import bilboka.messenger.dto.FacebookMessage
import bilboka.messenger.dto.FacebookSendRequest
import io.mockk.InternalPlatformDsl.toStr
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@TestInstance(PER_CLASS)
// TODO prop inject?
internal class MessengerSendAPIConsumerIT {

    private lateinit var sendConsumer: MessengerSendAPIConsumer
    private lateinit var testUrl: String
    private lateinit var mockBackEnd: MockWebServer

    private val pageAccessToken: String = "testPageAccess"

    @BeforeAll
    fun setUp() {
        mockBackEnd = MockWebServer()
        mockBackEnd.start()
    }

    @AfterAll
    fun tearDown() {
        mockBackEnd.shutdown()
    }

    @BeforeEach
    fun initialize() {
        testUrl = String.format(
            "http://localhost:%s",
            mockBackEnd.port
        )
        val messengerProperties = MessengerProperties()
        messengerProperties.sendUrl = testUrl
        messengerProperties.pageAccessToken = pageAccessToken
        sendConsumer = MessengerSendAPIConsumer(messengerProperties)
    }

    @Test
    fun sendMessage_correctPostCall() {
        // Arrange
        mockBackEnd.enqueue(
            MockResponse()
                .setBody("\"test\" : \"test\"")
                .addHeader("Content-Type", "application/json")
        )

        val recipient = mapOf(Pair("id", "123"))
        val testMessage = "detteerentest"

        val testFBMessage = FacebookSendRequest(
            recipient = recipient,
            message = FacebookMessage(
                text = testMessage,
            )
        )

        // Act
        sendConsumer.sendMessage(testFBMessage)

        val takeRequest = mockBackEnd.takeRequest()

        assertThat(takeRequest.method).isEqualTo("POST")
        assertThat(takeRequest.requestUrl.toStr()).contains(mockBackEnd.port.toStr())
        assertThat(takeRequest.requestUrl.toStr()).contains(pageAccessToken)
        assertThat(takeRequest.headers["Content-Type"]).contains("application/json")
        assertThat(takeRequest.body.readUtf8())
            .contains("\"text\":\"$testMessage\"")
            .contains("\"recipient\":{\"id\":\"123\"")
            .doesNotContain("mid").doesNotContain("seq")
    }

    @Test
    fun sendFileWorks() {
        mockBackEnd.enqueue(
            MockResponse()
                .setBody("\"test\" : \"test\"")
                .addHeader("Content-Type", "application/json")
        )

        sendConsumer.sendAttachment(
            "124", "report".toByteArray(), "testfilnavn", "application/pdf"
        )

        val takeRequest = mockBackEnd.takeRequest()

        assertThat(takeRequest.method).isEqualTo("POST")
        assertThat(takeRequest.headers["Content-Type"]).contains("multipart/form-data")
        assertThat(takeRequest.body.readUtf8())
            .contains("Content-Disposition: form-data; name=\"filedata\"")
            .contains("\"type\":\"file\"")
    }

}
