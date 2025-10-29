package ru.otus.hw.services;

import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Comment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentService {
    List<CommentDto> findAllByBookId(UUID bookId);
    Optional<CommentDto> findById(UUID id);
    CommentDto save(CommentDto dto);
    void deleteById(UUID id);
}



