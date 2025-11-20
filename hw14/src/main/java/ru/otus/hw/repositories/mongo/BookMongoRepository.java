package ru.otus.hw.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.documents.BookDocument;

public interface BookMongoRepository extends MongoRepository<BookDocument, String> {}

