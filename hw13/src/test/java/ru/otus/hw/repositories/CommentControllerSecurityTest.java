package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Проверки доступа к CommentController")
class CommentControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private CommentService commentService;

    private final UUID commentId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
    private final UUID bookId = UUID.fromString("6d92be5e-c2a0-40df-9e7c-e46a9dc66a92");

    @BeforeEach
    void setUp() {
        CommentDto commentDto = new CommentDto(
                commentId,
                "Test Comment",
                bookId
        );
        BookDto bookDto = new BookDto(
                bookId,
                "Test Book",
                new AuthorDto(UUID.randomUUID(), "Test Author"),
                Set.of(new GenreDto(UUID.randomUUID(), "Genre")),
                Set.of()
        );

        when(commentService.findById(commentId)).thenReturn(Optional.of(commentDto));
        when(bookService.findById(bookId)).thenReturn(Optional.of(bookDto));
    }

    @Test
    @DisplayName("Неавторизованный пользователь перенаправляется на login при обращении к /comments")
    void shouldRedirectToLoginComments() throws Exception {
        mockMvc.perform(get("/comments"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Авторизованный пользователь получает форму редактирования комментария")
    void shouldAllowEditFormAccessForAuthenticated() throws Exception {
        mockMvc.perform(get("/comments/{id}/edit", commentId))
                .andExpect(status().isOk())
                .andExpect(view().name("comments/form"))
                .andExpect(model().attributeExists("comment"))
                .andExpect(model().attributeExists("book"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("USER может сохранить комментарий")
    void userCanPostComment() throws Exception {
        CommentDto commentDto = new CommentDto(
                commentId,
                "Updated",
                bookId
        );
        when(commentService.save(any(CommentDto.class))).thenReturn(commentDto);
        mockMvc.perform(post("/comments/book/{bookId}/add", bookId)
                        .param("id", commentId.toString())
                        .param("text", "Updated")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/comments/book/" + bookId));
        verify(commentService, times(1)).save(any(CommentDto.class));
    }



    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("ADMIN может сохранить комментарий")
    void adminCanPostComment() throws Exception {
        CommentDto commentDto = new CommentDto(
                commentId,
                "Updated",
                bookId
        );
        when(commentService.save(any(CommentDto.class))).thenReturn(commentDto);
        mockMvc.perform(post("/comments/book/{bookId}/add", bookId)
                        .param("id", commentId.toString())
                        .param("text", "Updated")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/comments/book/" + bookId));
        verify(commentService, times(1)).save(any(CommentDto.class));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("USER может получить список комментариев к книге")
    void userCanViewBookComments() throws Exception {
        mockMvc.perform(get("/comments/book/{bookId}", bookId))
                .andExpect(status().isOk())
                .andExpect(view().name("comments/list"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("USER может удалить комментарий")
    void userCanDeleteComment() throws Exception {
        doNothing().when(commentService).deleteById(commentId);

        mockMvc.perform(post("/comments/{id}/delete", commentId).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/comments/book/" + bookId));

        verify(commentService, times(1)).deleteById(commentId);
    }


    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("ADMIN может удалить комментарий")
    void adminCanDeleteComment() throws Exception {
        mockMvc.perform(post("/comments/{id}/delete", commentId).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/comments/book/" + bookId));
    }
}

