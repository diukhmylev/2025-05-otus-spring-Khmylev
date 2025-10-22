package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final CommentRepository commentRepository;

    @Override
    public Flux<BookDto> findAll() {
        return bookRepository.findAll()
                .flatMap(book -> {
                    List<String> commentIds =
                            Optional.ofNullable(book.getCommentIds()).orElse(Collections.emptyList());
                    List<String> genreIds =
                            Optional.ofNullable(book.getGenreIds()).orElse(Collections.emptyList());

                    return Flux.fromIterable(commentIds)
                            .flatMap(cid -> commentRepository.findById(cid).map(Comment::getText).defaultIfEmpty(""))
                            .collectList()
                            .map(commentTexts -> new BookDto(
                                            book.getId(),
                                            book.getTitle(),
                                            book.getAuthorId(),
                                            genreIds,
                                            commentIds,
                                            commentTexts
                                    )
                            );
                });
    }


    @Override
    public Mono<BookDto> findById(String id) {
        return bookRepository.findById(id)
                .flatMap(book ->
                        Flux.fromIterable(book.getCommentIds())
                                .flatMap(cid -> commentRepository.findById(cid).map(Comment::getText).defaultIfEmpty(""))
                                .collectList()
                                .map(commentTexts -> new BookDto(
                                        book.getId(),
                                        book.getTitle(),
                                        book.getAuthorId(),
                                        book.getGenreIds(),
                                        book.getCommentIds(),
                                        commentTexts
                                ))
                );
    }

    @Override
    public Mono<BookDto> save(BookDto dto) {
        List<String> safeGenreIds = Optional.ofNullable(dto.getGenreIds()).orElse(Collections.emptyList());
        List<String> safeCommentIds = Optional.ofNullable(dto.getCommentIds()).orElse(Collections.emptyList());

        Book book = new Book(
                dto.getId(),
                dto.getTitle(),
                dto.getAuthorId(),
                safeGenreIds,
                safeCommentIds
        );
        return bookRepository.save(book)
                .flatMap(savedBook -> {
                    List<String> commentIds = Optional.ofNullable(savedBook.getCommentIds()).orElse(Collections.emptyList());
                    return Flux.fromIterable(commentIds)
                            .flatMap(cid -> commentRepository.findById(cid).map(Comment::getText).defaultIfEmpty(""))
                            .collectList()
                            .map(commentTexts -> new BookDto(
                                    savedBook.getId(),
                                    savedBook.getTitle(),
                                    savedBook.getAuthorId(),
                                    Optional.ofNullable(savedBook.getGenreIds()).orElse(Collections.emptyList()),
                                    commentIds,
                                    commentTexts
                            ));
                });
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return bookRepository.deleteById(id);
    }
}
