package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controllers.CommentController;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;
    @MockBean
    private BookService bookService;

    @Test
    @DisplayName("GET /comments/book/{bookId} — получение списка комментариев книги")
    void listByBook_shouldReturnComments() throws Exception {
        when(commentService.findAllByBookId(anyString())).thenReturn(List.of(new CommentDto()));
        when(bookService.findById(anyString())).thenReturn(Optional.of(new BookDto()));
        mockMvc.perform(get("/comments/book/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("comments/list"))
                .andExpect(model().attributeExists("comments", "book", "newComment"));
    }

    @Test
    @DisplayName("POST /comments/book/{bookId}/add — добавление комментария к книге")
    void addComment_shouldRedirectToCommentsByBook() throws Exception {
        mockMvc.perform(post("/comments/book/1/add")
                        .flashAttr("commentDto", new CommentDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/comments/book/1"));
    }

    @Test
    @DisplayName("GET /comments/{id}/edit — отображение формы редактирования комментария по ID")
    void editForm_shouldReturnCommentEditForm() throws Exception {
        CommentDto commentDto = new CommentDto(); commentDto.setBookId("1");
        when(commentService.findById(anyString())).thenReturn(Optional.of(commentDto));
        when(bookService.findById(anyString())).thenReturn(Optional.of(new BookDto()));
        mockMvc.perform(get("/comments/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("comments/form"))
                .andExpect(model().attributeExists("comment", "book"));
    }

    @Test
    @DisplayName("POST /comments/{id}/edit — сохранение изменений комментария и редирект")
    void saveEdit_shouldRedirectToCommentsByBook() throws Exception {
        CommentDto commentDto = new CommentDto(); commentDto.setBookId("1");
        when(commentService.findById(anyString())).thenReturn(Optional.of(commentDto));
        mockMvc.perform(post("/comments/1/edit")
                        .flashAttr("commentDto", new CommentDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/comments/book/1"));
    }

    @Test
    @DisplayName("POST /comments/{id}/delete — удаление комментария и редирект к списку комментариев")
    void delete_shouldRedirectToCommentsByBook() throws Exception {
        CommentDto commentDto = new CommentDto(); commentDto.setBookId("1");
        when(commentService.findById(anyString())).thenReturn(Optional.of(commentDto));
        mockMvc.perform(post("/comments/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/comments/book/1"));
    }
}
