package ru.otus.hw.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "authors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Author {
    @Id
    private UUID id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 255)
    private String fullName;
}
