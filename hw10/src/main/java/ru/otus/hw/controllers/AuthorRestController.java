package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/authors")
public class AuthorRestController {

    private final AuthorService authorService;

    @GetMapping
    public List<AuthorDto> getAll() {
        return authorService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDto> getById(@PathVariable String id) {
        return authorService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AuthorDto> create(@RequestBody AuthorDto dto) {
        return ResponseEntity.ok(authorService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorDto> update(@PathVariable String id, @RequestBody AuthorDto dto) {
        dto.setId(id);
        return ResponseEntity.ok(authorService.save(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        authorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
