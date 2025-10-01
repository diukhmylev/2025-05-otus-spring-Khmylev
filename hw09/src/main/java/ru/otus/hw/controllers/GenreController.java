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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public String editForm(@PathVariable String id, Model model) {
        GenreDto genreDto = genreService.findById(id).orElseThrow();
        model.addAttribute("genreDto", genreDto);
        return "genres/form";
    }

    @PostMapping("/genres")
    public String save(@ModelAttribute GenreDto genreDto) {
        if (genreDto.getId() == null || genreDto.getId().isBlank()) {
            genreDto.setId(UUID.randomUUID().toString());
        }
        genreService.save(genreDto);
        return "redirect:/genres";
    }

    @PostMapping("/genres/{id}/delete")
    public String delete(@PathVariable String id) {
        List<BookDto> books = bookService.findAll();
        books.forEach(book -> {
            if (book.getGenreIds() != null && !book.getGenreIds().isEmpty()) {
                List<String> mutableGenres = new ArrayList<>(book.getGenreIds());
                mutableGenres.removeIf(gId -> gId.equals(id));
                book.setGenreIds(mutableGenres);
                bookService.save(book);
            }
        });
        genreService.deleteById(id);
        return "redirect:/genres";
    }

}

