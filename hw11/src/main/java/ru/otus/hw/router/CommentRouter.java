package ru.otus.hw.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.otus.hw.handler.CommentHandler;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class CommentRouter {
    @Bean
    public RouterFunction<ServerResponse> commentRoutes(CommentHandler handler) {
        return route()
                .GET("/api/comments/by-book/{bookId}", handler::getByBookId)
                .GET("/api/comments/{id}", handler::getById)
                .POST("/api/comments", handler::create)
                .PUT("/api/comments/{id}", handler::update)
                .DELETE("/api/comments/{id}", handler::delete)
                .build();
    }
}


