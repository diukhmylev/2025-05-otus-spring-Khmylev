package ru.otus.hw.services;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.actuator.AppMetrics;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepo;
    private final AuthorRepository authorRepo;
    private final GenreRepository genreRepo;
    private final CommentService commentService;
    private final AppMetrics appMetrics;

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> findAll() {
        log.debug("Request to get all books");
        return bookRepo.findAllWithoutComments().stream()
                .map(book -> {
                    BookDto dto = BookDto.toDto(book);
                    dto.setComments(new HashSet<>(commentService.findAllByBookId(book.getId())));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookDto> findById(UUID id) {
        log.debug("Request to get book by id={}", id);
        return bookRepo.findById(id)
                .map(BookDto::toDto);
    }

    @Override
    public BookDto save(BookDto dto) {
        log.info("Save book title='{}', authorId={}, genres={}",
                dto.getTitle(),
                dto.getAuthor() != null ? dto.getAuthor().getId() : null,
                dto.getGenres());
        Book entity = (dto.getId() != null)
                ? bookRepo.findById(dto.getId()).orElse(new Book())
                : new Book();

        applyDtoToEntity(entity, dto);
        Book saved = bookRepo.save(entity);
        appMetrics.incrementBooksCreated();
        return BookDto.toDto(saved);
    }

    @Override
    public void deleteById(UUID id) {
        if (!bookRepo.existsById(id)) {
            throw new EntityNotFoundException("Book not found: " + id);
        }
        bookRepo.deleteById(id);
    }

    private void applyDtoToEntity(Book entity, BookDto dto) {
        entity.setTitle(dto.getTitle());
        entity.setId(dto.getId());

        if (dto.getAuthor() == null || dto.getAuthor().getId() == null) {
            throw new IllegalArgumentException("Author is required");
        }

        Author author = authorRepo.findById(dto.getAuthor().getId())
                .orElseThrow(() -> new EntityNotFoundException("Author not found: " + dto.getAuthor().getId()));
        entity.setAuthor(author);

        Set<GenreDto> genreDtos = dto.getGenres() != null ? dto.getGenres() : Set.of();
        Set<UUID> genreIds = genreDtos.stream().map(GenreDto::getId).collect(Collectors.toSet());

        List<Genre> genres = genreRepo.findAllById(genreIds);
        if (genres.size() != genreIds.size()) {
            throw new EntityNotFoundException("Some genres not found: " + genreIds);
        }

        entity.getGenres().clear();
        entity.getGenres().addAll(genres);
    }
}
