package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.models.Genre;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenreDto {
    private String id;
    private String name;

    public static GenreDto fromEntity(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }
}
