package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    @Override
    public Flux<AuthorDto> findAll() {
        return authorRepository.findAll().map(AuthorDto::fromEntity);
    }

    @Override
    public Mono<AuthorDto> findById(String id) {
        return authorRepository.findById(id).map(AuthorDto::fromEntity);
    }

    @Override
    public Mono<AuthorDto> save(AuthorDto dto) {
        return authorRepository.save(new Author(dto.getId(), dto.getFullName()))
                .map(AuthorDto::fromEntity);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return authorRepository.deleteById(id);
    }
}



