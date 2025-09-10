package com.soomsoom.backend.adapter.`in`.web.api.item.config

import com.soomsoom.backend.adapter.`in`.security.config.DomainSecurityConfigurer
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.stereotype.Component

@Component
class ItemConfigurer : DomainSecurityConfigurer {
    override fun configure(authorize: AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry) {
        authorize
            .requestMatchers(HttpMethod.GET, "/items", "/items/{itemId}").authenticated()
            .requestMatchers(HttpMethod.POST, "/items", "/items/{itemId}/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/items/{itemId}/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/items/{itemId}/**").hasRole("ADMIN")
    }
}
