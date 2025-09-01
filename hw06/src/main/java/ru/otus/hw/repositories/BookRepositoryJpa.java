package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class BookRepositoryJpa implements BookRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Book save(Book book) {
        if (book.getId() == null || book.getId() == 0) {
            em.persist(book);
            return book;
        } else {
            return em.merge(book);
        }
    }

    @Override
    public void deleteById(Long id) {
        Book book = em.find(Book.class, id);
        if (book != null) {
            em.remove(book);
        }
    }

    @Override
    public Optional<Book> findById(Long id) {
        EntityGraph<Book> graph = em.createEntityGraph(Book.class);
        graph.addAttributeNodes("author", "genres", "comments");
        Map<String, Object> hints = Map.of("jakarta.persistence.fetchgraph", graph);
        return Optional.ofNullable(em.find(Book.class, id, hints));
    }

    @Override
    public List<Book> findAll() {
        EntityGraph<Book> graph = em.createEntityGraph(Book.class);
        graph.addAttributeNodes("author", "genres");
        TypedQuery<Book> query = em.createQuery("select b from Book b", Book.class);
        query.setHint("jakarta.persistence.fetchgraph", graph);
        return query.getResultList();
    }
}
