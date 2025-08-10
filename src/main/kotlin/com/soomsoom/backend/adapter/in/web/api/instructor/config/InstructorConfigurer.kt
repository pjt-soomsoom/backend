package com.soomsoom.backend.adapter.`in`.web.api.instructor.config

import com.soomsoom.backend.adapter.`in`.security.config.DomainSecurityConfigurer
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.stereotype.Component

@Component
class InstructorConfigurer : DomainSecurityConfigurer {
    override fun configure(
        authorize: AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry,
    ) {
        authorize.requestMatchers(HttpMethod.GET, "/instructors/**").permitAll()
        authorize.requestMatchers(HttpMethod.OPTIONS,"/instructors/**").permitAll()
        authorize.requestMatchers(HttpMethod.HEAD,"/instructors/**").permitAll()
        authorize.requestMatchers(HttpMethod.POST, "/instructors/**").hasRole("ADMIN")
        authorize.requestMatchers(HttpMethod.PUT, "/instructors/**").hasRole("ADMIN")
        authorize.requestMatchers(HttpMethod.DELETE, "/instructors/**").hasRole("ADMIN")
        authorize.requestMatchers(HttpMethod.PATCH, "/instructors/**").hasRole("ADMIN")
    }
}
