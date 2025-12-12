package ru.otus.hw.actuator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    @Override
    public Health health() {
        log.debug("Running custom database health check");

        try (Connection connection = dataSource.getConnection()) {
            boolean valid = connection.isValid(1000);

            if (valid) {
                log.info("Database health check: UP");
                return Health.up()
                        .withDetail("database", "reachable")
                        .withDetail("url", connection.getMetaData().getURL())
                        .withDetail("user", connection.getMetaData().getUserName())
                        .build();
            } else {
                log.warn("Database health check: connection is not valid");
                return Health.down()
                        .withDetail("database", "connection is not valid")
                        .build();
            }

        } catch (SQLException ex) {
            log.error("Database health check: DOWN, exception occurred", ex);
            return Health.down(ex)
                    .withDetail("database", "exception while trying to get connection")
                    .build();
        }
    }
}