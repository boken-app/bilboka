package bilboka.messenger.consumer

import bilboka.messenger.MessengerProperties
import bilboka.messenger.dto.MessengerProfileRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import khttp.responses.Response
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.lang.String.format

object MessengerProfileApiConfig {
    const val ACCESS_TOKEN = "access_token"
}

@Component
class MessengerProfileAPIConsumer(
    private val messengerProperties: MessengerProperties
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val mapper = jacksonObjectMapper()

    fun doProfileUpdate(profileRequest: MessengerProfileRequest) {
        profileRequest.persistentMenu.firstOrNull()?.let {
            logger.info("Setter persistent menu")
            logger.info("Items: {}", it.callToActions.joinToString { c -> c.title })
        }

        val url =
            "${messengerProperties.profileUrl}?${MessengerProfileApiConfig.ACCESS_TOKEN}=${messengerProperties.pageAccessToken}"

        val response: Response = khttp.post(
            url = url,
            json = JSONObject(mapper.writeValueAsString(profileRequest)),
        )
        if (response.statusCode == HttpStatus.OK.value()) {
            logger.info("Profil-oppdatering fullført!")
        } else {
            logger.error(format("Profil-oppdatering feilet. Status: %s - %s", response.statusCode, response.text))
        }
    }
}
