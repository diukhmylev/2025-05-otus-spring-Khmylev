package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;

    @GetMapping("/")
    public String index(Model model) {
        List<BookDto> books = bookService.findAll();
        model.addAttribute("books", books);
        return "index";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        BookDto newBook = new BookDto();
        newBook.setId(UUID.randomUUID());
        newBook.setGenres(new HashSet<>());
        newBook.setComments(new HashSet<>());
        model.addAttribute("bookDto", newBook);
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "books/book-form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        BookDto bookDto = bookService.findById(id).orElseThrow();
        model.addAttribute("bookDto", bookDto);
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "books/book-form";
    }

    @PostMapping("/books")
    public String save(@ModelAttribute("bookDto") BookDto bookDto,
                       @RequestParam UUID authorId,
                       @RequestParam(required = false) List<UUID> genreIds) {

        AuthorDto authorDto = authorService.getById(authorId);
        Set<GenreDto> genresDto = genreIds == null
                ? Set.of()
                : genreIds.stream()
                .map(genreService::getById)
                .collect(Collectors.toSet());

        bookDto.setAuthor(authorDto);
        bookDto.setGenres(genresDto);

        if (bookDto.getComments() == null) {
            bookDto.setComments(Set.of());
        }

        bookService.save(bookDto);
        return "redirect:/";
    }


    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id) {
        bookService.deleteById(id);
        return "redirect:/";
    }
}