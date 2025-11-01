package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepo;

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
        Genre g = genreRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Genre not found: " + id));
        return GenreDto.toDto(g);
    }

    @Override
    public GenreDto create(GenreDto dto) {
        Genre g = new Genre();
        g.setId(UUID.randomUUID());
        g.setName(dto.getName());
        g = genreRepo.save(g);
        return GenreDto.toDto(g);
    }

    @Override
    public GenreDto update(UUID id, GenreDto dto) {
        Genre g = genreRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Genre not found: " + id));
        g.setName(dto.getName());
        return GenreDto.toDto(genreRepo.save(g));
    }

    @Override
    public void delete(UUID id) {
        if (!genreRepo.existsById(id)) {
            throw new EntityNotFoundException("Genre not found: " + id);
        }
        genreRepo.deleteById(id);
    }
}