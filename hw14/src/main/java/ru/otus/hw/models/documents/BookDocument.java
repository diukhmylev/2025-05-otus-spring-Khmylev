package ru.otus.hw.models.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDocument {
    @Id
    private String id;

    private String title;
    private String authorId;
    private List<String> genreIds;
}