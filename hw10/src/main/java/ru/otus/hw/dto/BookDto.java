package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    private String id;
    private String title;
    private String authorId;
    private String authorFullName;
    private List<String> genreIds;
    private List<String> genreNames;
    private List<String> commentIds;
    private List<String> commentTexts;

    public static BookDto fromEntity(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor() != null ? book.getAuthor().getId() : null,
                book.getAuthor() != null ? book.getAuthor().getFullName() : null,
                book.getGenres() != null ? book.getGenres().stream().map(Genre::getId).toList() : List.of(),
                book.getGenres() != null ? book.getGenres().stream().map(Genre::getName).toList() : List.of(),
                book.getComments() != null ? book.getComments().stream().map(Comment::getId).toList() : List.of(),
                book.getComments() != null ? book.getComments().stream().map(Comment::getText).toList() : List.of()
        );
    }
}
