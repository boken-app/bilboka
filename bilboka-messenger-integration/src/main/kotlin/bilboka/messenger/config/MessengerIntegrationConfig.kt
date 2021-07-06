package bilboka.messenger.config

import bilboka.messenger.consumer.MessengerWebhookConsumer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MessengerIntegrationConfig {

    @Value("\${messenger.send-url}")
    lateinit var messengerSendUrl: String

    @Value("\${messenger.page-access-token}")
    lateinit var pageAccessToken: String

    @Bean
    fun messengerWebhookConsumer(): MessengerWebhookConsumer {
        return MessengerWebhookConsumer(messengerSendUrl, pageAccessToken)
    }

}