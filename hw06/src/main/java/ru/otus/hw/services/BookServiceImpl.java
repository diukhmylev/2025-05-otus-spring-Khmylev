package ru.otus.hw.services;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.BookRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final EntityManager em;

    @Override
    @Transactional
    public Book save(Book book) {
        Book saved;
        if (book.getId() == null || book.getId() == 0) {
            em.persist(book);
            saved = book;
        } else {
            saved = em.merge(book);
        }

        em.flush();
        em.refresh(saved);

        saved.getAuthor().getFullName();
        saved.getGenres().size();

        return saved;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findById(Long id) {
        EntityGraph<Book> graph = em.createEntityGraph(Book.class);
        graph.addAttributeNodes("author", "genres", "comments");

        Map<String, Object> hints = Map.of("jakarta.persistence.fetchgraph", graph);

        return Optional.ofNullable(em.find(Book.class, id, hints));
    }

    @Override
    @Transactional
    public List<Book> findAll() {
        return bookRepository.findAll();
    }
}