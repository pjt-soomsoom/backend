package com.soomsoom.backend.adapter.`in`.web.api.activity.config

import com.soomsoom.backend.adapter.`in`.security.config.DomainSecurityConfigurer
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.stereotype.Component

@Component
class UserConfigurer : DomainSecurityConfigurer {
    override fun configure(
        authorize: AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry,
    ) {
        authorize.requestMatchers(HttpMethod.GET, "/users/me/**").authenticated()
        authorize.requestMatchers("/users/**").authenticated()
    }
}
