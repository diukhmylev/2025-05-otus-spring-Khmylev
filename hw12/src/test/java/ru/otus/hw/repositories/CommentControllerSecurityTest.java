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

import static org.mockito.Mockito.when;
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
}

