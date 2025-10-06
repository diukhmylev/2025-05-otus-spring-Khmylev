package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    public List<GenreDto> findAll() {
        return genreRepository.findAll()
                .stream()
                .map(GenreDto::fromEntity)
                .toList();
    }

    @Override
    public Optional<GenreDto> findById(String id) {
        return genreRepository.findById(id).map(GenreDto::fromEntity);
    }

    @Override
    public GenreDto save(GenreDto dto) {
        var entity = new Genre(dto.getId(), dto.getName());
        return GenreDto.fromEntity(genreRepository.save(entity));
    }

    @Override
    public void deleteById(String id) {
        genreRepository.deleteById(id);
    }
}
