package ru.otus.hw.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.otus.hw.handler.AuthorHandler;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class AuthorRouter {
    @Bean
    public RouterFunction<ServerResponse> authorRoutes(AuthorHandler handler) {
        return route()
                .GET("/api/authors", handler::getAll)
                .GET("/api/authors/{id}", handler::getById)
                .POST("/api/authors", handler::create)
                .PUT("/api/authors/{id}", handler::update)
                .DELETE("/api/authors/{id}", handler::delete)
                .build();
    }
}


