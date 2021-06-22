package bilboka.messenger.integration

import bilboka.messenger.dto.MessengerWebhookRequest
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

object MessengerWebhookConfig {
    const val VERIFY_TOKEN = "vdfgsnmrfeiudi59fblablajvbrmeivncmq231v"
    const val SUBSCRIBE_MODE = "subscribe"
}

@RestController
@RequestMapping("webhook")
@SpringBootApplication
class MessengerWebhook {

    @GetMapping
    fun get(
        @RequestParam(name = "hub.verify_token") token: String,
        @RequestParam(name = "hub.challenge") challenge: String,
        @RequestParam(name = "hub.mode") mode: String
    ): ResponseEntity<String> {
        if (MessengerWebhookConfig.VERIFY_TOKEN.equals(token) && MessengerWebhookConfig.SUBSCRIBE_MODE.equals(mode)) {
            return ResponseEntity.ok(challenge)
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
    }

    @PostMapping
    fun post(@RequestBody request: MessengerWebhookRequest) {
        print(request)
    }

}