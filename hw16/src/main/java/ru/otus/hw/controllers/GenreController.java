package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;
    private final BookService bookService;

    @GetMapping("/genres")
    public String list(Model model) {
        List<GenreDto> genres = genreService.findAll();
        model.addAttribute("genres", genres);
        return "genres/list";
    }

    @GetMapping("/genres/new")
    public String createForm(Model model) {
        model.addAttribute("genreDto", new GenreDto());
        return "genres/form";
    }

    @GetMapping("/genres/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        GenreDto genreDto = genreService.getById(id);
        model.addAttribute("genreDto", genreDto);
        return "genres/form";
    }

    @PostMapping("/genres")
    public String save(@ModelAttribute GenreDto genreDto) {
        if (genreDto.getId() == null) {
            genreService.create(genreDto);
        } else {
            genreService.update(genreDto.getId(), genreDto);
        }
        return "redirect:/genres";
    }

    @PostMapping("/genres/{id}/delete")
    public String delete(@PathVariable UUID id) {
        List<BookDto> books = bookService.findAll();
        for (BookDto book : books) {
            if (book.getGenres() != null && !book.getGenres().isEmpty()) {
                Set<GenreDto> updatedGenres = new HashSet<>(book.getGenres());
                updatedGenres.removeIf(genre -> genre.getId().equals(id));
                book.setGenres(updatedGenres);
                bookService.save(book);
            }
        }
        genreService.delete(id);
        return "redirect:/genres";
    }
}
