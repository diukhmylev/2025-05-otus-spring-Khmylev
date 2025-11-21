package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    private UUID id;
    private String title;
    private AuthorDto author;
    private Set<GenreDto> genres = new HashSet<>();
    private Set<CommentDto> comments = new HashSet<>();

    public static BookDto toDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                AuthorDto.toDto(book.getAuthor()),
                book.getGenres().stream().map(GenreDto::toDto).collect(Collectors.toSet()),
                book.getComments().stream().map(c -> new CommentDto(c.getId(), c.getText(), book.getId()))
                        .collect(Collectors.toSet())
        );
    }



}
