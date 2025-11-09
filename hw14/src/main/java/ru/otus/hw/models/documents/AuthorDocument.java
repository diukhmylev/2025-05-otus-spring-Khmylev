package ru.otus.hw.models.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("authors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorDocument {
    @Id
    private String id;

    private String fullName;
}