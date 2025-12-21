package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.UUID;

@RepositoryRestResource(
        path = "comments",
        collectionResourceRel = "comments"
)
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    @RestResource(path = "by-book", rel = "byBook")
    List<Comment> findByBookId(UUID bookId);
}
