package com.soomsoom.backend.common.config

import com.soomsoom.backend.common.argumentresolvers.pageble.CustomPageableArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val customPageableArgumentResolver: CustomPageableArgumentResolver
) : WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(customPageableArgumentResolver)
    }
}
