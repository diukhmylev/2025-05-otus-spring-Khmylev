package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Override
    public List<AuthorDto> findAll() {
        return authorRepository.findAll()
                .stream()
                .map(AuthorDto::fromEntity)
                .toList();
    }

    @Override
    public Optional<AuthorDto> findById(String id) {
        return authorRepository.findById(id).map(AuthorDto::fromEntity);
    }

    @Override
    public AuthorDto save(AuthorDto dto) {
        var entity = new Author(dto.getId(), dto.getFullName());
        return AuthorDto.fromEntity(authorRepository.save(entity));
    }

    @Override
    public void deleteById(String id) {
        authorRepository.deleteById(id);
    }
}


