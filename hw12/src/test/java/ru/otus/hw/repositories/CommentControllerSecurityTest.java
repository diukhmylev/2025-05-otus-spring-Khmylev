package ru.otus.hw.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.config.SecurityConfig;
import ru.otus.hw.controllers.CommentController;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;

@WebMvcTest(CommentController.class)
@Import(SecurityConfig.class)
class CommentControllerSecurityTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CommentService commentService;
    @MockBean
    private BookService bookService;

    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldRedirectToLoginComments() throws Exception {
        UUID bookId = UUID.fromString("c52d129d-62ba-4e0f-82a9-5a30dc99dd30");

        mvc.perform(get("/comments/book/{bookId}", bookId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldAllowEditFormAccessForAuthenticated() throws Exception {

        UUID commentId = UUID.fromString("11111111-2222-3333-4444-555555555555");
        UUID bookId    = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

        var commentDto = mock(ru.otus.hw.dto.CommentDto.class);
        when(commentDto.getId()).thenReturn(commentId);
        when(commentDto.getBookId()).thenReturn(bookId);

        var bookDto = mock(ru.otus.hw.dto.BookDto.class);
        given(commentService.findById(eq(commentId))).willReturn(Optional.of(commentDto));

        given(bookService.findById(eq(bookId))).willReturn(Optional.of(bookDto));

        mvc.perform(get("/comments/{id}/edit", commentId))
                .andExpect(status().isOk());
    }
}