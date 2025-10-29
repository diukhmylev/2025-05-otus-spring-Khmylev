package ru.otus.hw.repositories;

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
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Проверки доступа к BookController")
class BookControllerSecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Test
    @DisplayName("Неавторизованный пользователь перенаправляется на login при обращении к /")
    void shouldRedirectToLoginBooks() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Авторизованный пользователь успешно получает список книг")
    void shouldAllowBooksForAuthenticated() throws Exception {
        List<BookDto> books = List.of(
                new BookDto(
                        UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"),
                        "Test Book",
                        new AuthorDto(UUID.fromString("6d92be5e-c2a0-40df-9e7c-e46a9dc66a92"), "Test Author"),
                        Set.of(
                                new GenreDto(UUID.fromString("d3b07384-d9a5-4b3a-bad0-e3f3a3db58a1"), "Genre1"),
                                new GenreDto(UUID.fromString("9f6b4076-a9cb-45fc-90ce-7191cc44d6c2"), "Genre2")
                        ),
                        null
                )
        );
        when(bookService.findAll()).thenReturn(books);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("books"));
    }
}
