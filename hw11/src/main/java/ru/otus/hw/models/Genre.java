package ru.otus.hw.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "genres")
public class Genre {
    @Id
    private String id;
    private String name;
}
