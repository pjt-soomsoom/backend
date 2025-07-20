package com.soomsoom.backend.adapter.`in`.security.config

import com.soomsoom.backend.adapter.`in`.security.filter.JwtAuthenticationFilter
import com.soomsoom.backend.adapter.`in`.security.provider.JwtTokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider,
) {

    /**
     * 애플리케이션의 HTTP 보안 필터 체인을 구성합니다.
     *
     * CORS 설정, CSRF 비활성화, 세션을 상태 비저장으로 지정하며, 특정 엔드포인트("/swagger-ui/**", "/auth/admin/login", "/auth/admin/sign-up")에 대한 접근을 허용하고 그 외 모든 요청은 인증을 요구합니다.
     * JWT 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 추가합니다.
     *
     * @param http HTTP 보안 구성을 위한 HttpSecurity 객체
     * @return 구성된 SecurityFilterChain 인스턴스
     * @throws Exception 보안 필터 체인 설정 중 오류가 발생할 경우
     */
    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers(
                        "/swagger-ui/**",
                        "/auth/admin/login",
                        "/auth/admin/sign-up"
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter::class.java
            )
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = listOf("*")
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH")
            allowedHeaders = listOf("*")
            allowCredentials = true
        }
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
    }
}
