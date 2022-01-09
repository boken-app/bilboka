package bilboka.messenger

import bilboka.messenger.consumer.MessengerWebhookConsumer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer

@Configuration
@ComponentScan
class MessengerIntegrationConfig {

    @Value("\${messenger.send-url}")
    lateinit var messengerSendUrl: String

    @Value("\${messenger.page-access-token}")
    lateinit var pageAccessToken: String

    @Bean
    fun messengerWebhookConsumer(): MessengerWebhookConsumer {
        return MessengerWebhookConsumer(messengerSendUrl, pageAccessToken)
    }

    @Bean // TODO: trengs denne?
    fun propertiesResolver(): PropertySourcesPlaceholderConfigurer? {
        return PropertySourcesPlaceholderConfigurer()
    }

}