package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.models.Comment;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private UUID id;
    private String text;
    private UUID bookId;

    public static CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getBook() != null ? comment.getBook().getId() : null
        );
    }
}

