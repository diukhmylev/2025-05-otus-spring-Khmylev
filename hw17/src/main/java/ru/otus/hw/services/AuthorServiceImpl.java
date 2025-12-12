package ru.otus.hw.services;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.actuator.AppMetrics;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepo;
    private final AppMetrics appMetrics;

    @Override
    @Transactional
    public List<AuthorDto> findAll() {
        log.debug("Request to get all authors");
        return authorRepo.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public AuthorDto getById(UUID id) {
        log.debug("Request to get author by id={}", id);
        Author a = authorRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Author not found by id={}", id);
                    return new EntityNotFoundException("Author not found: " + id);
                });
        return toDto(a);
    }

    @Override
    public AuthorDto create(AuthorDto dto) {
        log.info("Create new author with fullName='{}'", dto.getFullName());
        Author a = new Author();
        a.setId(UUID.randomUUID());
        a.setFullName(dto.getFullName());
        a = authorRepo.save(a);
        log.debug("Author created with id={}", a.getId());
        appMetrics.incrementAuthorsCreated();
        return toDto(a);
    }

    @Override
    public AuthorDto update(UUID id, AuthorDto dto) {
        log.info("Update author id={} with fullName='{}'", id, dto.getFullName());
        Author a = authorRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Attempt to update non-existing author id={}", id);
                    return new EntityNotFoundException("Author not found: " + id);
                });
        a.setFullName(dto.getFullName());
        a = authorRepo.save(a);
        log.debug("Author updated id={}", a.getId());
        return toDto(a);
    }

    @Override
    public void delete(UUID id) {
        log.info("Delete author id={}", id);
        if (!authorRepo.existsById(id)) {
            log.warn("Attempt to delete non-existing author id={}", id);
            throw new EntityNotFoundException("Author not found: " + id);
        }
        authorRepo.deleteById(id);
        log.debug("Author deleted id={}", id);
    }

    private AuthorDto toDto(Author a) {
        AuthorDto dto = new AuthorDto();
        dto.setId(a.getId());
        dto.setFullName(a.getFullName());
        return dto;
    }
}