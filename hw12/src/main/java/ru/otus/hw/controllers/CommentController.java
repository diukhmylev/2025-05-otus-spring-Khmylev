package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final BookService bookService;

    @GetMapping("/comments/book/{bookId}")
    public String listByBook(@PathVariable UUID bookId, Model model) {
        List<CommentDto> comments = commentService.findAllByBookId(bookId);
        BookDto book = bookService.findById(bookId).orElseThrow();
        model.addAttribute("comments", comments);
        model.addAttribute("book", book);
        model.addAttribute("newComment", new CommentDto());
        return "comments/list";
    }

    @PostMapping("/comments/book/{bookId}/add")
    public String addComment(@PathVariable UUID bookId, @ModelAttribute CommentDto commentDto) {
        commentDto.setBookId(bookId);
        commentService.save(commentDto);
        return "redirect:/comments/book/" + bookId;
    }

    @GetMapping("/comments/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        CommentDto commentDto = commentService.findById(id).orElseThrow();
        BookDto book = bookService.findById(commentDto.getBookId()).orElseThrow();
        model.addAttribute("comment", commentDto);
        model.addAttribute("book", book);
        return "comments/form";
    }

    @PostMapping("/comments/{id}/edit")
    public String saveEdit(@PathVariable UUID id, @ModelAttribute CommentDto commentDto) {
        CommentDto existing = commentService.findById(id).orElseThrow();
        existing.setText(commentDto.getText());
        commentService.save(existing);
        return "redirect:/comments/book/" + existing.getBookId();
    }

    @PostMapping("/comments/{id}/delete")
    public String delete(@PathVariable UUID id) {
        CommentDto comment = commentService.findById(id).orElseThrow();
        UUID bookId = comment.getBookId();
        commentService.deleteById(id);
        return "redirect:/comments/book/" + bookId;
    }
}