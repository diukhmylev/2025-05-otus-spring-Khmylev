package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;
    private final BookService bookService;

    @GetMapping("/authors")
    public String list(Model model) {
        List<AuthorDto> authors = authorService.findAll();
        model.addAttribute("authors", authors);
        return "authors/list";
    }

    @GetMapping("/authors/new")
    public String createForm(Model model) {
        model.addAttribute("authorForm", new AuthorDto());
        return "authors/form";
    }

    @GetMapping("/authors/{id}/edit")
    public String editForm(@PathVariable String id, Model model) {
        AuthorDto authorDto = authorService.findById(id).orElseThrow();
        model.addAttribute("authorForm", authorDto);
        return "authors/form";
    }

    @PostMapping("/authors")
    public String save(@ModelAttribute AuthorDto authorDto) {
        if (authorDto.getId() == null || authorDto.getId().isBlank()) {
            authorDto.setId(UUID.randomUUID().toString());
        }
        authorService.save(authorDto);
        return "redirect:/authors";
    }

    @PostMapping("/authors/{id}/delete")
    public String delete(@PathVariable String id) {
        List<BookDto> books = bookService.findAll();

        books.stream()
                .filter(book -> id.equals(book.getAuthorId()))
                .forEach(book -> bookService.deleteById(book.getId()));
        authorService.deleteById(id);

        return "redirect:/authors";
    }

}
