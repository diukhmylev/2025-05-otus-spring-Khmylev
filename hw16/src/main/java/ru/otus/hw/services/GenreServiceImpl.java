package ru.otus.hw.services;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.actuator.AppMetrics;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepo;
    private final AppMetrics appMetrics;

    @Override
    @Transactional
    public List<GenreDto> findAll() {
        return genreRepo.findAll().stream()
                .map(GenreDto::toDto)
                .toList();
    }

    @Override
    @Transactional
    public GenreDto getById(UUID id) {
        log.debug("Request to get genre by id={}", id);
        Genre g = genreRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Genre not found: " + id));
        return GenreDto.toDto(g);
    }

    @Override
    public GenreDto create(GenreDto dto) {
        log.info("Create new genre name='{}'", dto.getName());
        Genre g = new Genre();
        g.setId(UUID.randomUUID());
        g.setName(dto.getName());
        g = genreRepo.save(g);
        appMetrics.incrementGenresCreated();
        return GenreDto.toDto(g);
    }

    @Override
    public GenreDto update(UUID id, GenreDto dto) {
        log.info("Update genre id={} with name='{}'", id, dto.getName());
        Genre g = genreRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Genre not found: " + id));
        g.setName(dto.getName());
        return GenreDto.toDto(genreRepo.save(g));
    }

    @Override
    public void delete(UUID id) {
        log.info("Delete genre id={}", id);
        if (!genreRepo.existsById(id)) {
            log.warn("Attempt to delete non-existing genre id={}", id);
            throw new EntityNotFoundException("Genre not found: " + id);
        }
        genreRepo.deleteById(id);
        log.debug("Genre deleted id={}", id);
    }
}