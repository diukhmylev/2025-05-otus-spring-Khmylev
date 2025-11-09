package ru.otus.hw.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;

    @Override
    public List<CommentDto> findAllByBookId(UUID bookId) {
        return commentRepository.findByBookId(bookId)
                .stream()
                .map(CommentDto::toDto)
                .toList();
    }

    @Override
    public Optional<CommentDto> findById(UUID id) {
        return commentRepository.findById(id).map(CommentDto::toDto);
    }

    @Override
    public CommentDto save(CommentDto dto) {
        var book = bookRepository.findById(dto.getBookId()).orElseThrow();
        var entity = new Comment(UUID.randomUUID(), dto.getText(), book);
        return CommentDto.toDto(commentRepository.save(entity));
    }

    @Override
    public void deleteById(UUID id) {
        commentRepository.deleteById(id);
    }
}

