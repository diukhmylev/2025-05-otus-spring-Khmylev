package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentRestController {

    private final CommentService commentService;

    // получить все комментарии к книге
    @GetMapping("/book/{bookId}")
    public List<CommentDto> getByBook(@PathVariable String bookId) {
        return commentService.findAllByBookId(bookId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getById(@PathVariable String id) {
        return commentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CommentDto> create(@RequestBody CommentDto dto) {
        return ResponseEntity.ok(commentService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> update(@PathVariable String id, @RequestBody CommentDto dto) {
        dto.setId(id);
        return ResponseEntity.ok(commentService.save(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        commentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
