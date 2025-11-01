package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepo;

    @Override
    @Transactional
    public List<AuthorDto> findAll() {
        return authorRepo.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public AuthorDto getById(UUID id) {
        Author a = authorRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author not found: " + id));
        return toDto(a);
    }

    @Override
    public AuthorDto create(AuthorDto dto) {
        Author a = new Author();
        a.setId(UUID.randomUUID());
        a.setFullName(dto.getFullName());
        a = authorRepo.save(a);
        return toDto(a);
    }

    @Override
    public AuthorDto update(UUID id, AuthorDto dto) {
        Author a = authorRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author not found: " + id));
        a.setFullName(dto.getFullName());
        return toDto(authorRepo.save(a));
    }

    @Override
    public void delete(UUID id) {
        if (!authorRepo.existsById(id)) {
            throw new EntityNotFoundException("Author not found: " + id);
        }
        authorRepo.deleteById(id);
    }

    private AuthorDto toDto(Author a) {
        AuthorDto dto = new AuthorDto();
        dto.setId(a.getId());
        dto.setFullName(a.getFullName());
        return dto;
    }
}