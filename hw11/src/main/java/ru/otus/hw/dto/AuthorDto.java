package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.models.Author;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorDto {
    private String id;
    private String fullName;

    public static AuthorDto fromEntity(Author author) {
        return new AuthorDto(author.getId(), author.getFullName());
    }
}
