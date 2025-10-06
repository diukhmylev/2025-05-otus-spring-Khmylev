package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {

    private final CommentService commentService;
    private final CommentConverter commentConverter;

    @ShellMethod(value = "Find comment by id", key = {"ccid", "comment-by-id"})
    public String findCommentById(String id) {
        return commentService.findById(id)
                .map(commentConverter::commentToString)
                .orElse("Comment with id %s not found".formatted(id));
    }

    @ShellMethod(value = "Insert new comment", key = {"cins", "insert-comment"})
    public String insertComment(String text, String bookId) {
        var comment = new Comment();
        comment.setText(text);
        comment.setCreatedAt(Instant.now());
        comment.setBook(new Book(bookId, null, null, null, null));

        var saved = commentService.save(comment);
        return commentConverter.commentToString(saved);
    }

    @ShellMethod(value = "Update comment", key = {"cupd", "update-comment"})
    public String updateComment(String id, String text, String bookId) {
        var comment = new Comment();
        comment.setId(id);
        comment.setText(text);
        comment.setCreatedAt(Instant.now());
        comment.setBook(new Book(bookId, null, null, null, null));

        var saved = commentService.save(comment);
        return commentConverter.commentToString(saved);
    }

    @ShellMethod(value = "Delete comment by id", key = {"cdel", "delete-comment"})
    public void deleteComment(String id) {
        commentService.deleteById(id);
    }

    @ShellMethod(value = "Get comments by book id", key = "bcom")
    public String getCommentsByBookId(String bookId) {
        List<Comment> comments = commentService.findAllByBookId(bookId);

        if (comments.isEmpty()) {
            return "No comments found for book id: " + bookId;
        }

        return comments.stream()
                .map(c -> "Id: %s, text: %s".formatted(c.getId(), c.getText()))
                .collect(Collectors.joining(System.lineSeparator()));
    }
}