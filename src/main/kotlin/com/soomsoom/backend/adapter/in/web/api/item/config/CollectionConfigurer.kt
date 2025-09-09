package com.soomsoom.backend.adapter.`in`.web.api.item.config

import com.soomsoom.backend.adapter.`in`.security.config.DomainSecurityConfigurer
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.stereotype.Component

@Component
class CollectionConfigurer : DomainSecurityConfigurer {
    override fun configure(authorize: AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry) {
        authorize
            .requestMatchers(HttpMethod.GET, "/collections", "/collections/{collectionId}").authenticated()
            .requestMatchers(HttpMethod.POST, "/collections", "/collections/{collectionId}/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/collections/{collectionId}/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/collections/{collectionId}/**").hasRole("ADMIN")
    }
}
