package ru.otus.hw.services;

import ru.otus.hw.models.Comment;
import java.util.List;
import java.util.Optional;

public interface CommentService {
    Comment save(Comment comment);
    void deleteById(Long id);
    Optional<Comment> findById(Long id);
    List<Comment> findByBookId(Long bookId);
}