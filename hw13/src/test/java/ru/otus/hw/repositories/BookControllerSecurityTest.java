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
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Проверки доступа к BookController")
public class BookControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    private final UUID bookId = UUID.fromString("6d92be5e-c2a0-40df-9e7c-e46a9dc66a92");

    @BeforeEach
    void setUp() {
        BookDto bookDto = new BookDto(
                bookId,
                "Test Book",
                new AuthorDto(UUID.randomUUID(), "Test Author"),
                Set.of(new GenreDto(UUID.randomUUID(), "Genre")),
                Set.of()
        );

        when(bookService.findById(bookId)).thenReturn(Optional.of(bookDto));
        doNothing().when(bookService).deleteById(bookId);
    }

    @Test
    @DisplayName("Неавторизованный пользователь перенаправляется на login при обращении к /")
    void anonymousUserRedirectedToLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Пользователь с ролью USER имеет доступ к списку книг")
    void userCanAccessIndex() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Пользователь с ролью USER не может удалять книги")
    void userCannotDeleteBook() throws Exception {
        mockMvc.perform(post("/" + bookId + "/delete"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCanDeleteBook() throws Exception {
        mockMvc.perform(post("/" + bookId + "/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }


    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Пользователь с ролью ADMIN имеет доступ к списку книг")
    void adminCanAccessIndex() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }
}