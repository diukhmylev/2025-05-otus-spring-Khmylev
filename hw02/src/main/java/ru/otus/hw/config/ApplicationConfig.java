package ru.otus.hw.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan("ru.otus.hw")
public class ApplicationConfig {

    @Bean
    public AppProperties appProperties(
            @Value("${test.rightAnswersCountToPass}") int rightAnswersCountToPass,
            @Value("${test.fileName}") String testFileName
    ) {
        return new AppProperties(rightAnswersCountToPass, testFileName);
    }
}