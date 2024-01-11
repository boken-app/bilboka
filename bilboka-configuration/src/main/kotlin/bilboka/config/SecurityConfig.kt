package bilboka.config

import bilboka.security.AuthenticationFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.http.HttpServletResponse


@Configuration
@EnableWebSecurity
class SecurityConfig {
    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(SecurityConfig::class.java)
    }

    @Autowired
    lateinit var authenticationFilter: AuthenticationFilter

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf()
            //.disable()
            .ignoringAntMatchers("/webhook")
            .and()
            .addFilterBefore(
                authenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )
            .authorizeRequests {
                it.antMatchers("/", "/webhook", "/privacy", "/**/sample").permitAll()
                it.antMatchers("/vehicles/**").authenticated()
                it.anyRequest().authenticated()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .exceptionHandling {
                it.accessDeniedHandler { request, response, ex ->
                    logger.error("Access Denied: ${request.requestURI}", ex)
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden")
                }
                it.authenticationEntryPoint { request, response, ex ->
                    logger.error("Authentication Failed: ${request.requestURI}", ex)
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                }
            }

        return http.build()
    }

}
