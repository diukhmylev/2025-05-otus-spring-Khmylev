package ru.otus.hw.services;

import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreService {
    List<Genre> findAll();
    Optional<Genre> findById(String id);
    Genre save(Genre genre);
    void deleteById(String id);
}



