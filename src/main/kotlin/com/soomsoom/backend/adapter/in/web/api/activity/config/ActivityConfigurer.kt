package com.soomsoom.backend.adapter.`in`.web.api.activity.config

import com.soomsoom.backend.adapter.`in`.security.config.DomainSecurityConfigurer
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.stereotype.Component

@Component
class ActivityConfigurer : DomainSecurityConfigurer {
    override fun configure(
        authorize: AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry,
    ) {
        authorize.requestMatchers("/activities/*/history/**").authenticated()

        authorize.requestMatchers(HttpMethod.GET, "/activities/**").permitAll()
        authorize.requestMatchers(HttpMethod.OPTIONS, "/activities/**").permitAll()
        authorize.requestMatchers(HttpMethod.HEAD, "/activities/**").permitAll()
        authorize.requestMatchers(HttpMethod.POST, "/activities/**").authenticated()
        authorize.requestMatchers(HttpMethod.PUT, "/activities/**").hasRole("ADMIN")
        authorize.requestMatchers(HttpMethod.DELETE, "/activities/**").hasRole("ADMIN")
        authorize.requestMatchers(HttpMethod.PATCH, "/activities/**").hasRole("ADMIN")
    }
}
