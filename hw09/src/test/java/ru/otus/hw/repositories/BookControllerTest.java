package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controllers.BookController;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;
    @MockBean
    private AuthorService authorService;
    @MockBean
    private GenreService genreService;
    @MockBean
    private CommentService commentService;

    @Test
    @DisplayName("GET / — проверка отображения списка книг")
    void index_shouldReturnListOfBooks() throws Exception {
        when(bookService.findAll()).thenReturn(List.of(new BookDto()));
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("books"));
    }

    @Test
    @DisplayName("GET /new — отображение формы создания новой книги")
    void createForm_shouldReturnBookForm() throws Exception {
        when(authorService.findAll()).thenReturn(List.of(new AuthorDto()));
        when(genreService.findAll()).thenReturn(List.of(new GenreDto()));
        mockMvc.perform(get("/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/book-form"))
                .andExpect(model().attributeExists("bookDto", "authors", "genres"));
    }

    @Test
    @DisplayName("GET /{id}/edit — отображение формы редактирования книги по ID")
    void editForm_shouldReturnBookEditForm() throws Exception {
        BookDto bookDto = new BookDto();
        bookDto.setGenreIds(new ArrayList<>());
        when(bookService.findById(anyString())).thenReturn(Optional.of(bookDto));

        when(authorService.findAll()).thenReturn(List.of(new AuthorDto()));
        when(genreService.findAll()).thenReturn(List.of(new GenreDto()));

        mockMvc.perform(get("/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/book-form"))
                .andExpect(model().attributeExists("bookDto", "authors", "genres"));
    }

    @Test
    @DisplayName("POST / — сохранение книги и редирект на главную страницу")
    void save_shouldRedirectToIndex() throws Exception {
        AuthorDto authorDto = new AuthorDto("author1", "Author Name");
        GenreDto genreDto = new GenreDto("genre1", "Genre Name");

        when(authorService.findById(anyString())).thenReturn(Optional.of(authorDto));
        when(genreService.findById(anyString())).thenReturn(Optional.of(genreDto));

        BookDto bookDto = new BookDto();
        bookDto.setAuthorId("author1");
        bookDto.setGenreIds(List.of("genre1"));
        bookDto.setTitle("Test Book");

        mockMvc.perform(post("/")
                        .flashAttr("bookDto", bookDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }



    @Test
    @DisplayName("POST /{id}/delete — удаление книги и редирект на главную страницу")
    void delete_shouldRedirectToIndex() throws Exception {
        mockMvc.perform(post("/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
