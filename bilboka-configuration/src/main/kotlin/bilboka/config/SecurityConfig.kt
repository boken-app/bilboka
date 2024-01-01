package bilboka.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf -> csrf.ignoringAntMatchers("/webhook") } // Disable CSRF protection for /webhook
            .authorizeRequests { authorize ->
                authorize
                    .antMatchers("/").permitAll() // Permit all requests to the root URL
                    .antMatchers("/webhook").permitAll() // Permit all requests to messenger webhook
                    .antMatchers("/privacy").permitAll() // Permit all requests to privacy policy
                    .antMatchers("/**/sample").permitAll() // Permit all requests to samples
                    .anyRequest().authenticated() // All other requests require authentication
            }
            .formLogin().and()
            .httpBasic()

        return http.build()
    }
}
