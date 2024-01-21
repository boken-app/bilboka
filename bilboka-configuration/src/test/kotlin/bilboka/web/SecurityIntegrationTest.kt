package bilboka.web

import bilboka.web.resource.VehicleResource
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher

@ExtendWith(SpringExtension::class)
@WebMvcTest(VehicleResource::class)
@ContextConfiguration(classes = [VehicleResourceTest.TestConfig::class])
@TestPropertySource(
    properties = [
        "bilboka.web.apiToken=bilbokaWebTestApiToken"
    ]
)
class SecurityIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `test get vehicle unauthorized`() {
        mockMvc.perform(
            get("/vehicles/4")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `test get vehicle authorized`() {
        mockMvc.perform(
            get("/vehicles/4")
                .header("X-API-KEY", encodeEmail("some.test@mail.com"))
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `test get vehicles authorized`() {
        mockMvc.perform(
            get("/vehicles")
                .header("X-API-KEY", encodeEmail("some.test@mail.com"))
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `test get vehicles authorized then unauthorized`() {
        mockMvc.perform(
            get("/vehicles")
                .header("X-API-KEY", encodeEmail("some.test@mail.com"))
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
        mockMvc.perform(
            get("/vehicles")
                .header("X-API-KEY", encodeEmail("some.new.test@mail.com"))
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `test get vehicle user not existing`() {
        mockMvc.perform(
            get("/vehicles")
                .header("X-API-KEY", encodeEmail("other.test@mail.com"))
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    private fun encodeEmail(email: String): String {
        val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
            .also { it.init(Cipher.ENCRYPT_MODE, publicTestKey.toPublicKey()) }

        val encryptedBytes = cipher.doFinal(email.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    private fun String.toPublicKey(): PublicKey {
        val keyBytes = Base64.getDecoder().decode(this)
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec)
    }

    private val publicTestKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyL/HemQivAm/LRAu8V0y" +
            "xXqGS8NugpFkqsK+GNJv/iksspDpQV7SQuuZDn/YJ+OIUS80q15shScNFQoz207G" +
            "wasD73BRPo8th7DBLjh/7B+tVx03JGkRwEuICjclmAIkTQTBIPyDDqJ8o5cvrpqL" +
            "zSdoNvX/ylRlH7EGpIMaSMlynfrFyGkRJSJzY+iZIyB8qhxrPa6WiTYNFqgd49LH" +
            "uKtT1IXsUMPx+VTKXCRaJ6LNHNoPaZOk/JPpg5Ie2QbSe7E5y9KdwQ1ScNSZG584" +
            "NMvYElePUWQ7xLTu40P13jbVuJQiDm8Vg74wNUvnPezby2X4CMTFnswMLSrWLv7E" +
            "1QIDAQAB"

    @Configuration
    @ComponentScan(basePackages = ["bilboka"])
    class TestConfig
}
