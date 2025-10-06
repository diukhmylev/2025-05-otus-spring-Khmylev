package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/genres")
public class GenreRestController {

    private final GenreService genreService;

    @GetMapping
    public List<GenreDto> getAll() {
        return genreService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreDto> getById(@PathVariable String id) {
        return genreService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<GenreDto> create(@RequestBody GenreDto dto) {
        return ResponseEntity.ok(genreService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenreDto> update(@PathVariable String id, @RequestBody GenreDto dto) {
        dto.setId(id);
        return ResponseEntity.ok(genreService.save(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        genreService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
