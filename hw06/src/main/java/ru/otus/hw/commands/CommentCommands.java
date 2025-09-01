package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.services.CommentService;

import java.time.Instant;

@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {

    private final CommentService commentService;
    private final CommentConverter commentConverter;

    @ShellMethod(value = "Find comment by id", key = {"ccid", "comment-by-id"})
    public String findCommentById(long id) {
        return commentService.findById(id)
                .map(commentConverter::commentToString)
                .orElse("Comment with id %d not found".formatted(id));
    }

    // cins "New comment" 1
    @ShellMethod(value = "Insert new comment", key = {"cins", "insert-comment"})
    public String insertComment(String text, long bookId) {
        var savedComment = commentService.save(
                new Comment(null, text, Instant.now(), new Book(bookId, null, null, null, null))
        );
        return commentConverter.commentToString(savedComment);
    }

    // cupd 3 "Updated comment" 2
    @ShellMethod(value = "Update comment", key = {"cupd", "update-comment"})
    public String updateComment(long id, String text, long bookId) {
        Comment commentToSave = new Comment(id, text, Instant.now(), new Book(bookId, null, null, null, null));
        var savedComment = commentService.save(commentToSave);
        return commentConverter.commentToString(savedComment);
    }

    // cdel 3
    @ShellMethod(value = "Delete comment by id", key = {"cdel", "delete-comment"})
    public void deleteComment(long id) {
        commentService.deleteById(id);
    }
}
