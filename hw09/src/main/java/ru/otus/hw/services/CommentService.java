package ru.otus.hw.services;

import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Comment;
import java.util.List;
import java.util.Optional;

public interface CommentService {
    List<CommentDto> findAllByBookId(String bookId);
    Optional<CommentDto> findById(String id);
    CommentDto save(CommentDto dto);
    void deleteById(String id);
}



