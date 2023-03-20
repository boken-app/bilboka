package bilboka.core.report

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Description
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect
import org.thymeleaf.spring5.SpringTemplateEngine


@Configuration
class ThymeleafConfig {

//    @Bean
//    @Description("Thymeleaf Template Resolver")
//    fun templateResolver(): ITemplateResolver {
//        val templateResolver = ITemplateResolver()
//        templateResolver.setPrefix("/WEB-INF/views/")
//        templateResolver.setSuffix(".html")
//        templateResolver.setTemplateMode("HTML5")
//        return templateResolver
//    }

    @Bean
    @Description("Thymeleaf Template Engine")
    fun templateEngine(): SpringTemplateEngine {
        val templateEngine = SpringTemplateEngine()
        templateEngine.addDialect(Java8TimeDialect())
//        templateEngine.setTemplateResolver(templateResolver)
        return templateEngine
    }
}