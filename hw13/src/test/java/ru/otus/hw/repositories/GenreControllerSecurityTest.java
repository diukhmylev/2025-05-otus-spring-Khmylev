package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controllers.GenreController;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Проверки доступа к GenreController")
class GenreControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenreService genreService;

    @MockBean
    private BookService bookService;

    private final UUID genreId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");

    @BeforeEach
    void setUp() {
        when(genreService.getById(genreId)).thenReturn(new GenreDto(genreId, "Test Genre"));
        when(genreService.findAll()).thenReturn(List.of(new GenreDto(genreId, "Test Genre")));
        when(bookService.findAll()).thenReturn(List.of());
    }

    @Test
    @DisplayName("Неавторизованный пользователь перенаправляется на login при обращении к /genres")
    void shouldRedirectToLoginGenres() throws Exception {
        mockMvc.perform(get("/genres"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Авторизованный пользователь успешно получает список жанров")
    void shouldAllowGenresForAuthenticated() throws Exception {
        mockMvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(view().name("genres/list"))
                .andExpect(model().attributeExists("genres"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("ADMIN может создать новый жанр")
    void adminCanCreateGenre() throws Exception {
        GenreDto genreDto = new GenreDto(UUID.randomUUID(), "New Genre");

        mockMvc.perform(post("/genres")
                        .param("name", genreDto.getName())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/genres"));

        verify(genreService, times(1)).create(any(GenreDto.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("ADMIN может отредактировать жанр")
    void adminCanEditGenre() throws Exception {
        mockMvc.perform(get("/genres/{id}/edit", genreId))
                .andExpect(status().isOk())
                .andExpect(view().name("genres/form"))
                .andExpect(model().attributeExists("genreDto"))
                .andExpect(model().attribute("genreDto", hasProperty("id", is(genreId))));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("ADMIN может удалить жанр")
    void adminCanDeleteGenre() throws Exception {
        mockMvc.perform(post("/genres/{id}/delete", genreId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/genres"));

        verify(genreService, times(1)).delete(genreId);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("USER не может удалить жанр")
    void userCannotDeleteGenre() throws Exception {
        mockMvc.perform(post("/genres/{id}/delete", genreId)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
