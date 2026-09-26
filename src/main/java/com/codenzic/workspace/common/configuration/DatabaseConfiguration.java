package com.codenzic.workspace.common.configuration;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class DatabaseConfiguration {
    @Bean
    static BeanFactoryPostProcessor requireDatasourceCredentials(Environment environment) {
        return beanFactory -> {
            String username = environment.getProperty("spring.datasource.username");
            String password = environment.getProperty("spring.datasource.password");
            if (isUnconfigured(username) || isUnconfigured(password)) {
                throw new IllegalStateException(
                        "Set SPRING_DATASOURCE_USERNAME and SPRING_DATASOURCE_PASSWORD before starting the application");
            }
        };
    }

    private static boolean isUnconfigured(String value) {
        return value == null || value.isBlank() || value.startsWith("${");
    }
}
