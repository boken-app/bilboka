package bilboka.messenger.resource

import bilboka.messenger.MessengerProperties
import bilboka.messenger.dto.MessengerWebhookRequest
import bilboka.messenger.service.FacebookMessageHandler
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

object MessengerWebhookConfig {
    const val SUBSCRIBE_MODE = "subscribe"
    const val PAGE_SUBSCRIPTION = "page"
    const val EVENT_RECEIVED_RESPONSE = "EVENT_RECEIVED"
}

@RestController
@RequestMapping("webhook")
class MessengerWebhookResource(
    private val messengerProperties: MessengerProperties
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var facebookMessageHandler: FacebookMessageHandler

    @GetMapping
    fun get(
        @RequestParam(name = "hub.verify_token") token: String,
        @RequestParam(name = "hub.challenge") challenge: String,
        @RequestParam(name = "hub.mode") mode: String
    ): ResponseEntity<String> {
        if (messengerProperties.verifyToken == token && MessengerWebhookConfig.SUBSCRIBE_MODE == mode) {
            logger.info("Webhook verified!")
            return ResponseEntity.ok(challenge)
        }
        logger.info("Webhook rejected!")
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
    }

    @PostMapping
    fun post(@RequestBody request: MessengerWebhookRequest): ResponseEntity<String> {
        return if (MessengerWebhookConfig.PAGE_SUBSCRIPTION == request.requestObject) {
            logger.info("Handling incoming page request!")
            // TODO valider request med SHA256 signatur / app secret 
            request.entry.stream()
                .forEach { facebookEntry -> facebookMessageHandler.handleMessage(facebookEntry) }
            ResponseEntity.ok(MessengerWebhookConfig.EVENT_RECEIVED_RESPONSE)
        } else {
            logger.info("Unknown request object {}. Replying not found!", request.requestObject)
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

}
