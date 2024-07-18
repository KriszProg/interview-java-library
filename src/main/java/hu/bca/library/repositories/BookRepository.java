package hu.bca.library.repositories;

import hu.bca.library.models.Book;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends CrudRepository<Book, Long> {

    @Query("""
            SELECT b
            FROM Book b
            JOIN b.authors a
            WHERE LOWER(a.country) = LOWER(:country)
            ORDER BY b.year ASC NULLS LAST
            """)
    List<Book> findByAuthorsCountry(@Param("country") String country);

    @Query(value = """
        SELECT b
        FROM Book b
        JOIN b.authors a
        WHERE LOWER(a.country) = LOWER(:country)
        AND b.year IS NOT NULL
        AND (:from IS NULL OR b.year >= :from)
        AND (:to IS NULL OR b.year <= :to)
        ORDER BY b.year ASC
        """)
    List<Book> findByAuthorsCountryAndYearBetween(@Param("country") String country,
                                                  @Param("from") Integer from,
                                                  @Param("to") Integer to);

}
