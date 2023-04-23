package bilboka.integration.autosys.consumer

import bilboka.integration.autosys.AutosysProperties
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
// TODO prop inject?
internal class AkfDatautleveringConsumerTest {

    lateinit var akfDatautleveringConsumer: AkfDatautleveringConsumer
    lateinit var testUrl: String
    lateinit var mockBackEnd: MockWebServer

    val apiKey: String = "testApiKey"

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
        val autosysProperties = AutosysProperties()
        autosysProperties.akfDatautleveringUrl = testUrl
        autosysProperties.apiKey = apiKey
        akfDatautleveringConsumer = AkfDatautleveringConsumer(autosysProperties)
    }

    @Test
    fun hentKjoretoydata_correctGetCall() {
        // Arrange
        mockBackEnd.enqueue(
            MockResponse()
                .setBody("\"test\" : \"testbody\"")
                .addHeader("Content-Type", "application/json")
        )

        // Act
        val kjoretoydata = akfDatautleveringConsumer.hentKjoretoydata("AB12345")

        assertThat(kjoretoydata).contains("testbody")

        val takeRequest = mockBackEnd.takeRequest()

        assertThat(takeRequest.method).isEqualTo("GET")
        assertThat(takeRequest.requestUrl.toString()).contains("kjennemerke")
        assertThat(takeRequest.requestUrl.toString()).contains("AB12345")
        assertThat(takeRequest.headers["SVV-Authorization"]).contains("Apikey $apiKey")

    }
}
