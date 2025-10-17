package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public Flux<GenreDto> findAll() {
        return genreRepository.findAll().map(GenreDto::fromEntity);
    }

    @Override
    public Mono<GenreDto> findById(String id) {
        return genreRepository.findById(id).map(GenreDto::fromEntity);
    }

    @Override
    public Mono<GenreDto> save(GenreDto dto) {
        Genre entity = new Genre(dto.getId(), dto.getName());
        return genreRepository.save(entity).map(GenreDto::fromEntity);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return genreRepository.deleteById(id);
    }
}
