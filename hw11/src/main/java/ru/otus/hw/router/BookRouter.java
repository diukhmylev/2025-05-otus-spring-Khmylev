package ru.otus.hw.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.otus.hw.handler.BookHandler;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class BookRouter {
    @Bean
    public RouterFunction<ServerResponse> bookRoutes(BookHandler handler) {
        return route()
                .GET("/api/books", handler::getAll)
                .GET("/api/books/{id}", handler::getById)
                .POST("/api/books", handler::create)
                .PUT("/api/books/{id}", handler::update)
                .DELETE("/api/books/{id}", handler::delete)
                .build();
    }
}

