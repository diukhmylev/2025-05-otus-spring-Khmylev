package ru.otus.hw.models.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("genres")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenreDocument {
    @Id
    private String id;

    private String name;
}