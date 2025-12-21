package ru.otus.hw.services;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.actuator.AppMetrics;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;

    private final AppMetrics appMetrics;

    @Override
    public List<CommentDto> findAllByBookId(UUID bookId) {
        log.debug("Request to get comments for bookId={}", bookId);
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
        log.info("Create comment for bookId={}", dto.getBookId());
        var book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> {
                    log.warn("Book not found for comment create. bookId={}", dto.getBookId());
                    return new EntityNotFoundException("Book not found: " + dto.getBookId());
                });
        var entity = new Comment(UUID.randomUUID(), dto.getText(), book);
        appMetrics.incrementCommentsCreated();
        return CommentDto.toDto(commentRepository.save(entity));
    }

    @Override
    public void deleteById(UUID id) {
        commentRepository.deleteById(id);
    }
}

