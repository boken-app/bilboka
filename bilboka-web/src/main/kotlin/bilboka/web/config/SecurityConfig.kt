package bilboka.web.config

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
            .authorizeRequests { authorize ->
                authorize
                    .antMatchers("/").permitAll() // Permit all requests to the root URL
                    .antMatchers("/**/sample").permitAll() // Permit all requests to samples
                    .anyRequest().authenticated() // All other requests require authentication
            }
            .formLogin().and()
            .httpBasic()

        return http.build()
    }
}
