package bilboka.core.config

import org.springframework.context.annotation.Configuration

@Configuration
class BilbokaCoreConfig {
    // TODO kan kanskje brukes for postgres?
//    private val logger = LoggerFactory.getLogger(javaClass)
//
//    @Value("\${spring.datasource.url:null}")
//    private val dbUrl: String? = null
//
//    @Bean
//    @ConditionalOnProperty("spring.datasource.url")
//    fun dataSource(): DataSource? {
//        logger.debug("Creating datasource config for database $dbUrl")
//        val config = HikariConfig()
//        config.jdbcUrl = dbUrl
//        return HikariDataSource(config)
//    }
}
