package com.soomsoom.backend.adapter.`in`.web.api.banner.config

import com.soomsoom.backend.adapter.`in`.security.config.DomainSecurityConfigurer
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.stereotype.Component

@Component
class BannerConfigurer : DomainSecurityConfigurer {
    override fun configure(
        authorize: AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry,
    ) {
        authorize.requestMatchers(HttpMethod.GET, "/banners").permitAll()

        authorize.requestMatchers("/banners/admin").hasRole("ADMIN")
        authorize.requestMatchers(HttpMethod.POST, "/banners/**").hasRole("ADMIN")
        authorize.requestMatchers(HttpMethod.PUT, "/banners/**").hasRole("ADMIN")
        authorize.requestMatchers(HttpMethod.DELETE, "/banners/**").hasRole("ADMIN")
    }
}

