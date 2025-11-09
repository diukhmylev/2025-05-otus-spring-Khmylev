package ru.otus.hw.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "genres")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genre {
    @Id
    private UUID id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String name;
}

