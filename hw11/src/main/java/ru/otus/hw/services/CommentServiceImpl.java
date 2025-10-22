package ru.otus.hw.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;

    @Override
    public Flux<CommentDto> findAllByBookId(String bookId) {
        return commentRepository.findAllByBookId(bookId).map(CommentDto::fromEntity);
    }

    @Override
    public Mono<CommentDto> findById(String id) {
        return commentRepository.findById(id).map(CommentDto::fromEntity);
    }

    @Override
    public Mono<CommentDto> save(CommentDto dto) {
        if (dto.getId() == null) {
            return commentRepository.save(
                            new Comment(null, dto.getText(), dto.getBookId()))
                    .flatMap(savedComment ->
                            bookRepository.findById(savedComment.getBookId())
                                    .flatMap(book -> {
                                        List<String> updatedIds = new ArrayList<>(
                                                Optional.ofNullable(book.getCommentIds()).orElse(Collections.emptyList())
                                        );
                                        updatedIds.add(savedComment.getId());
                                        Book updatedBook = new Book(
                                                book.getId(),
                                                book.getTitle(),
                                                book.getAuthorId(),
                                                book.getGenreIds(),
                                                updatedIds
                                        );
                                        return bookRepository.save(updatedBook)
                                                .thenReturn(savedComment);
                                    })
                    )
                    .map(CommentDto::fromEntity);
        } else {
            Comment entity = new Comment(dto.getId(), dto.getText(), dto.getBookId());
            return commentRepository.save(entity)
                    .map(CommentDto::fromEntity);
        }
    }



    @Override
    public Mono<Void> deleteById(String id) {
        return commentRepository.deleteById(id);
    }
}



