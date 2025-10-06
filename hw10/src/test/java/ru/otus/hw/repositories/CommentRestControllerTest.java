package ru.otus.hw.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controllers.CommentRestController;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("CommentRestController")
@WebMvcTest(controllers = CommentRestController.class)
class CommentRestControllerTest {

    @Resource
    private MockMvc mvc;

    @Resource
    private ObjectMapper mapper;

    @MockBean
    private CommentService commentService;

    @DisplayName("should return list of comments for book")
    @Test
    void getByBook_shouldReturnList() throws Exception {
        var c1 = new CommentDto("c1", "Comment 1", "b1");
        var c2 = new CommentDto("c2", "Comment 2", "b1");

        Mockito.when(commentService.findAllByBookId("b1")).thenReturn(List.of(c1, c2));

        mvc.perform(get("/api/comments/book/b1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].text", is("Comment 1")));
    }

    @DisplayName("should return comment by id if found")
    @Test
    void getById_found() throws Exception {
        var c = new CommentDto("c1", "Comment 1", "b1");
        Mockito.when(commentService.findById("c1")).thenReturn(Optional.of(c));

        mvc.perform(get("/api/comments/c1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("c1")))
                .andExpect(jsonPath("$.text", is("Comment 1")));
    }

    @DisplayName("should return 404 if comment not found")
    @Test
    void getById_notFound() throws Exception {
        Mockito.when(commentService.findById("404")).thenReturn(Optional.empty());

        mvc.perform(get("/api/comments/404"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("should save and return new comment")
    @Test
    void create_shouldReturnSaved() throws Exception {
        var req = new CommentDto(null, "New comment", "b1");
        var saved = new CommentDto("c10", "New comment", "b1");

        Mockito.when(commentService.save(any(CommentDto.class))).thenReturn(saved);

        mvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("c10")))
                .andExpect(jsonPath("$.text", is("New comment")))
                .andExpect(jsonPath("$.bookId", is("b1")));

        var captor = ArgumentCaptor.forClass(CommentDto.class);
        Mockito.verify(commentService).save(captor.capture());
        CommentDto passed = captor.getValue();
        assertThat(passed.getText()).isEqualTo("New comment");
        assertThat(passed.getBookId()).isEqualTo("b1");
    }

    @DisplayName("should update and return comment")
    @Test
    void update_shouldReturnUpdated() throws Exception {
        var req = new CommentDto(null, "Updated", "b1");
        var saved = new CommentDto("c1", "Updated", "b1");

        Mockito.when(commentService.save(any(CommentDto.class))).thenReturn(saved);

        mvc.perform(put("/api/comments/c1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("c1")))
                .andExpect(jsonPath("$.text", is("Updated")))
                .andExpect(jsonPath("$.bookId", is("b1")));
    }

    @DisplayName("should delete comment and return no content")
    @Test
    void delete_shouldReturnNoContent() throws Exception {
        mvc.perform(delete("/api/comments/c1"))
                .andExpect(status().isNoContent());
        Mockito.verify(commentService).deleteById("c1");
    }
}
