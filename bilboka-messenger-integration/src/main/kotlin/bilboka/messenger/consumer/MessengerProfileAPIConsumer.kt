package bilboka.messenger.consumer

import bilboka.messenger.MessengerProperties
import bilboka.messenger.dto.MessengerProfileRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.lang.String.format

object MessengerProfileApiConfig {
    const val ACCESS_TOKEN = "access_token"
    const val FIELDS = "fields"
}

@Component
class MessengerProfileAPIConsumer(
    private val messengerProperties: MessengerProperties,
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val mapper = jacksonObjectMapper()
    private val client = OkHttpClient()

    fun doProfileUpdate(profileRequest: MessengerProfileRequest) {
        profileRequest.persistentMenu.firstOrNull()?.let {
            logger.info("Setter persistent menu")
            logger.info("Items: {}", it.callToActions.joinToString { c -> c.title })
        }
        profileRequest.iceBreakers.firstOrNull()?.let {
            logger.info("Setter icebreaker")
            logger.info("Items: {}", it.callToActions.joinToString { c -> c.question })
        }

        val url =
            "${messengerProperties.profileUrl}?${MessengerProfileApiConfig.ACCESS_TOKEN}=${messengerProperties.pageAccessToken}"

        val request = Request.Builder()
            .url(url)
            .post(
                RequestBody.create(MediaType.parse("application/json"), mapper.writeValueAsString(profileRequest))
            )

        client.newCall(request.build()).execute().use {
            if (it.isSuccessful) {
                logger.info("Profil-oppdatering fullført!")
            } else {
                logger.error(format("Profil-oppdatering feilet. Status: %s - %s", it.code(), it.body()?.string()))
            }
        }
    }

    fun getCurrentProfileSettings(fields: List<String>): String? {
        logger.info("Henter profilinfo for felt {}", fields.joinToString())

        val url = "${messengerProperties.profileUrl}?" +
                "${MessengerProfileApiConfig.FIELDS}=${fields.joinToString(",")}&" +
                "${MessengerProfileApiConfig.ACCESS_TOKEN}=${messengerProperties.pageAccessToken}"

        Request.Builder().url(url).build()
            .run {
                client.newCall(this).execute().use {
                    return if (it.isSuccessful) {
                        logger.info("Hentet profilinfo")
                        it.body()?.string()
                    } else {
                        logger.error(format("Profil-info feilet. Status: %s - %s", it.code(), it.body()?.string()))
                        null
                    }
                }
            }
    }
}
