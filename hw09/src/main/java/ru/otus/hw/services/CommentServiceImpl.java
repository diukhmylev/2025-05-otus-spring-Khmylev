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

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;

    @Override
    public List<CommentDto> findAllByBookId(String bookId) {
        return commentRepository.findAllByBookId(bookId)
                .stream()
                .map(CommentDto::fromEntity)
                .toList();
    }

    @Override
    public Optional<CommentDto> findById(String id) {
        return commentRepository.findById(id).map(CommentDto::fromEntity);
    }

    @Override
    public CommentDto save(CommentDto dto) {
        var book = bookRepository.findById(dto.getBookId()).orElseThrow();
        var entity = new Comment(dto.getId(), dto.getText(), Instant.now(), book);
        return CommentDto.fromEntity(commentRepository.save(entity));
    }

    @Override
    public void deleteById(String id) {
        commentRepository.deleteById(id);
    }
}

