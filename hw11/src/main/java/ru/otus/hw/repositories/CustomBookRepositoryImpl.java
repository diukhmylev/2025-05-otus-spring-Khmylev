package ru.otus.hw.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;

public class CustomBookRepositoryImpl implements CustomBookRepository {

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Override
    public Flux<BookDto> findAllWithDetails() {
        LookupOperation lookupAuthor = LookupOperation.newLookup()
                .from("authors")
                .localField("authorId")
                .foreignField("_id")
                .as("author");

        LookupOperation lookupGenres = LookupOperation.newLookup()
                .from("genres")
                .localField("genreIds")
                .foreignField("_id")
                .as("genres");

        LookupOperation lookupComments = LookupOperation.newLookup()
                .from("comments")
                .localField("commentIds")
                .foreignField("_id")
                .as("comments");

        Aggregation aggregation = Aggregation.newAggregation(
                lookupAuthor,
                lookupGenres,
                lookupComments
        );

        return mongoTemplate.aggregate(aggregation, "books", BookDto.class);
    }

    @Override
    public Mono<BookDto> findByIdWithDetails(String id) {
        LookupOperation lookupAuthor = LookupOperation.newLookup()
                .from("authors")
                .localField("authorId")
                .foreignField("_id")
                .as("author");

        LookupOperation lookupGenres = LookupOperation.newLookup()
                .from("genres")
                .localField("genreIds")
                .foreignField("_id")
                .as("genres");

        LookupOperation lookupComments = LookupOperation.newLookup()
                .from("comments")
                .localField("commentIds")
                .foreignField("_id")
                .as("comments");

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").is(id)),
                lookupAuthor,
                lookupGenres,
                lookupComments
        );

        return mongoTemplate.aggregate(aggregation, "books", BookDto.class).next();
    }
}
