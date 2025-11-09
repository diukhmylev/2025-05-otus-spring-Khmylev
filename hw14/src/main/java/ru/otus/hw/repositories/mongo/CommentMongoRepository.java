package ru.otus.hw.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.documents.CommentDocument;

public interface CommentMongoRepository extends MongoRepository<CommentDocument, String> {}