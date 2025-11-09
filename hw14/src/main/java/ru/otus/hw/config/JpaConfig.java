package ru.otus.hw.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "ru.otus.hw.repositories.jpa")
@EntityScan(basePackages = "ru.otus.hw.models.jpa")
public class JpaConfig {
}