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
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        newBook.setGenreIds(new ArrayList<>());
        newBook.setGenreNames(new ArrayList<>());
        newBook.setCommentIds(new ArrayList<>());
        newBook.setCommentTexts(new ArrayList<>());
        model.addAttribute("bookDto", newBook);
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "books/book-form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable String id, Model model) {
        BookDto bookDto = bookService.findById(id).orElseThrow();
        model.addAttribute("bookDto", bookDto);
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        return "books/book-form";
    }

    @PostMapping
    public String save(@ModelAttribute("bookDto") BookDto bookDto) {
        AuthorDto authorDto = authorService.findById(bookDto.getAuthorId()).orElseThrow();

        List<GenreDto> genresDto = bookDto.getGenreIds() == null ? List.of()
                : bookDto.getGenreIds().stream()
                .map(id -> genreService.findById(id).orElseThrow())
                .toList();

        String bookId = bookDto.getId() == null || bookDto.getId().isBlank() ? UUID.randomUUID().toString() : bookDto.getId();

        bookDto.setId(bookId);
        bookDto.setAuthorId(authorDto.getId());
        bookDto.setAuthorFullName(authorDto.getFullName());
        bookDto.setGenreIds(genresDto.stream().map(GenreDto::getId).toList());
        bookDto.setGenreNames(genresDto.stream().map(GenreDto::getName).toList());
        if (bookDto.getCommentIds() == null) bookDto.setCommentIds(List.of());
        if (bookDto.getCommentTexts() == null) bookDto.setCommentTexts(List.of());

        bookService.save(bookDto);
        return "redirect:/";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id) {
        bookService.deleteById(id);
        return "redirect:/";
    }
}
