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
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Проверки доступа к AuthorController")
class AuthorControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private BookService bookService;

    private final UUID authorId = UUID.fromString("6d92be5e-c2a0-40df-9e7c-e46a9dc66a92");

    @BeforeEach
    void setUp() {
        AuthorDto authorDto = new AuthorDto(authorId, "Test Author");
        when(authorService.getById(authorId)).thenReturn(authorDto);
        doNothing().when(authorService).delete(authorId);
        when(bookService.findAll()).thenReturn(Collections.emptyList());
    }

    @Test
    @DisplayName("Запрет анонимного доступа к /authors")
    void shouldDenyAnonymousAccess() throws Exception {
        mockMvc.perform(get("/authors"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Разрешение доступа аутентифицированному пользователю")
    void shouldAllowAuthenticatedAccess() throws Exception {
        mockMvc.perform(get("/authors"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("USER не может удалять авторов — должен получить 403")
    void userCannotDeleteAuthor() throws Exception {
        mockMvc.perform(post("/authors/{id}/delete", UUID.randomUUID()).with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Разрешение удаления авторов пользователю с ролью ADMIN")
    void adminCanDeleteAuthor() throws Exception {
        mockMvc.perform(post("/authors/{id}/delete", authorId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/authors"));
    }
}
