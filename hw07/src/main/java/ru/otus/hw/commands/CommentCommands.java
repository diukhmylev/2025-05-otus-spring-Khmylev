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
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {

    private final CommentService commentService;
    private final BookService bookService;
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
        var book = bookService.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book with id %d not found".formatted(bookId)));

        var newComment = new Comment(null, text, Instant.now(), book);
        var savedComment = commentService.save(newComment);
        return commentConverter.commentToString(savedComment);
    }

    // cupd 3 "Updated comment" 2
    @ShellMethod(value = "Update comment", key = {"cupd", "update-comment"})
    public String updateComment(long id, String text, long bookId) {
        var existingComment = commentService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment with id %d not found".formatted(id)));

        var book = bookService.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book with id %d not found".formatted(bookId)));

        existingComment.setText(text);
        existingComment.setBook(book);

        var savedComment = commentService.save(existingComment);
        return commentConverter.commentToString(savedComment);
    }


    // cdel 3
    @ShellMethod(value = "Delete comment by id", key = {"cdel", "delete-comment"})
    public void deleteComment(long id) {
        commentService.deleteById(id);
    }

    // bcom 1
    @ShellMethod(value = "Get comments by book id", key = "bcom")
    public String getCommentsByBookId(long bookId) {
        var comments = commentService.findAllByBookId(bookId);

        if (comments.isEmpty()) {
            return "No comments found for book id: " + bookId;
        }

        return comments.stream()
                .map(c -> String.format("Id: %d, text: %s", c.getId(), c.getText()))
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
