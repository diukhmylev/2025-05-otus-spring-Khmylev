package ru.otus.hw.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.otus.hw.handler.GenreHandler;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class GenreRouter {
    @Bean
    public RouterFunction<ServerResponse> genreRoutes(GenreHandler handler) {
        return route()
                .GET("/api/genres", handler::getAll)
                .GET("/api/genres/{id}", handler::getById)
                .POST("/api/genres", handler::create)
                .PUT("/api/genres/{id}", handler::update)
                .DELETE("/api/genres/{id}", handler::delete)
                .build();
    }
}

