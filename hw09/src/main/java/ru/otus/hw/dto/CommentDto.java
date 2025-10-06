package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.models.Comment;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private String id;
    private String text;
    private String bookId;

    public static CommentDto fromEntity(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getBook() != null ? comment.getBook().getId() : null
        );
    }
}

