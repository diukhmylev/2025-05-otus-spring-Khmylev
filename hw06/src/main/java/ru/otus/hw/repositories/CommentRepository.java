package ru.otus.hw.repositories;

import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    Comment save(Comment comment);
    void deleteById(Long id);
    Optional<Comment> findById(Long id);
    List<Comment> findAllByBookId(Long bookId);
}