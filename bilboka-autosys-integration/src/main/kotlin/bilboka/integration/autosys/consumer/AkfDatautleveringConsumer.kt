package bilboka.integration.autosys.consumer

import bilboka.integration.autosys.AutosysProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AkfDatautleveringConsumer(private val autosysProperties: AutosysProperties) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val mapper = jacksonObjectMapper()
    private val client = OkHttpClient()

    fun hentKjoretoydata(kjennemerke: String): String {
        client.newCall(
            Request.Builder()
                .url("${autosysProperties.akfDatautleveringUrl}?kjennemerke=$kjennemerke")
                .header("SVV-Authorization", "Apikey ${autosysProperties.apiKey}")
                .build()
        ).execute().use {
            if (it.isSuccessful) {
                logger.info("Hentet kjøretøydata for $kjennemerke")
                return it.body()?.string() ?: throw KjoretoydataFeiletException("Mottok ingen body fra kjøretøydata")
            } else {
                logger.error(
                    String.format(
                        "Hent kjøretøydata for $kjennemerke gikk ikke ok. Status: %s - %s",
                        it.code(),
                        it.body()?.string()
                    )
                )
                throw KjoretoydataFeiletException("Feilrespons fra kjøretøydata (${it.code()}). ${it.body()?.string()}")
            }
        }
    }

}

class KjoretoydataFeiletException(message: String? = null) : RuntimeException(message)
