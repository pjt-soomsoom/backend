package com.soomsoom.backend

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@SpringBootTest
@ContextConfiguration(initializers = [TestContainersConfig::class])
annotation class IntegrationTest()
