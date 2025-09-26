package ru.otus.hw.services;

import ru.otus.hw.models.Comment;
import java.util.List;
import java.util.Optional;

public interface CommentService {
    Optional<Comment> findById(String id);
    Comment save(Comment comment);
    void deleteById(String id);
    List<Comment> findAllByBookId(String bookId);
}

