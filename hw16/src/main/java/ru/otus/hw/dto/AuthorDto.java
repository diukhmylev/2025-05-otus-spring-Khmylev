package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.models.Author;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorDto {
    private UUID id;
    private String fullName;

    public static AuthorDto toDto(Author author) {
        return new AuthorDto(author.getId(), author.getFullName());
    }
}
