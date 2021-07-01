package bilboka.messenger.resource

import bilboka.messenger.dto.MessengerWebhookRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
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
@SpringBootApplication
class MessengerWebhookResource {

    @Value("\${messenger.verify-token}")
    lateinit var verifyToken: String

    @GetMapping
    fun get(
        @RequestParam(name = "hub.verify_token") token: String,
        @RequestParam(name = "hub.challenge") challenge: String,
        @RequestParam(name = "hub.mode") mode: String
    ): ResponseEntity<String> {
        if (verifyToken == token && MessengerWebhookConfig.SUBSCRIBE_MODE == mode) {
            return ResponseEntity.ok(challenge)
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
    }

    @PostMapping
    fun post(@RequestBody request: MessengerWebhookRequest): ResponseEntity<String> {
        if (MessengerWebhookConfig.PAGE_SUBSCRIPTION == request.requestObject) {
            print(request)
            return ResponseEntity.ok(MessengerWebhookConfig.EVENT_RECEIVED_RESPONSE)
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

}