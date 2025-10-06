package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final CommentRepository commentRepository;
    private final CommentService commentService;

    @Override
    public List<BookDto> findAll() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map(book -> {
            BookDto dto = BookDto.fromEntity(book);

            List<CommentDto> comments = commentService.findAllByBookId(book.getId());
            dto.setCommentIds(comments.stream().map(CommentDto::getId).toList());
            dto.setCommentTexts(comments.stream().map(CommentDto::getText).toList());

            return dto;
        }).toList();
    }


    @Override
    public Optional<BookDto> findById(String id) {
        Optional<Book> bookOpt = bookRepository.findById(id);
        return bookOpt.map(book -> {
            BookDto dto = BookDto.fromEntity(book);
            List<CommentDto> comments = commentService.findAllByBookId(book.getId());
            dto.setCommentIds(comments.stream().map(CommentDto::getId).toList());
            dto.setCommentTexts(comments.stream().map(CommentDto::getText).toList());
            return dto;
        });
    }


    @Override
    public BookDto save(BookDto dto) {
        var author = authorRepository.findById(dto.getAuthorId()).orElseThrow();

        var genres = dto.getGenreIds() == null ? List.<Genre>of()
                : dto.getGenreIds().stream()
                .map(gid -> genreRepository.findById(gid).orElseThrow())
                .toList();

        var entity = new Book(
                dto.getId(),
                dto.getTitle(),
                author,
                genres,
                new ArrayList<>()
        );

        return BookDto.fromEntity(bookRepository.save(entity));
    }

    @Override
    public void deleteById(String id) {
        List<Comment> comments = commentRepository.findAllByBookId(id);
        if (!comments.isEmpty()) {
            commentRepository.deleteAll(comments);
        }
        bookRepository.deleteById(id);
    }
}
