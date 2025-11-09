package ru.otus.hw.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.documents.AuthorDocument;

public interface AuthorMongoRepository extends MongoRepository<AuthorDocument, String> {}

