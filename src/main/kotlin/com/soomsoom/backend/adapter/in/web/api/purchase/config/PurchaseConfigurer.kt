package com.soomsoom.backend.adapter.`in`.web.api.purchase.config

import com.soomsoom.backend.adapter.`in`.security.config.DomainSecurityConfigurer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.stereotype.Component

@Component
class PurchaseConfigurer : DomainSecurityConfigurer {
    override fun configure(authorize: AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry) {
        authorize.requestMatchers("/purchase/**").authenticated()
    }
}
