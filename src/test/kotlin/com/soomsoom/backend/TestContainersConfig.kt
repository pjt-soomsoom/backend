package com.soomsoom.backend

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.MySQLContainer

object TestContainersConfig : ApplicationContextInitializer<ConfigurableApplicationContext> {
    private val mysqlContainer = MySQLContainer<Nothing>("mysql:8.0").apply {
        withDatabaseName("testdb")
        withUsername("test")
        withPassword("test")
        start()
    }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        TestPropertyValues.of(
            "spring.datasource.url=${mysqlContainer.jdbcUrl}",
            "spring.datasource.username=${mysqlContainer.username}",
            "spring.datasource.password=${mysqlContainer.password}"
//            "spring.jpa.hibernate.ddl-auto=create"
        ).applyTo(applicationContext.environment)
    }
}
