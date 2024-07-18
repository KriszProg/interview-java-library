package hu.bca.library.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.bca.library.properties.BookServiceProperties;
import hu.bca.library.models.Author;
import hu.bca.library.models.Book;
import hu.bca.library.repositories.AuthorRepository;
import hu.bca.library.repositories.BookRepository;
import hu.bca.library.services.BookService;
import hu.bca.library.services.OpenLibraryService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final OpenLibraryService openLibraryService;
    private final BookServiceProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository,
                           OpenLibraryService openLibraryService, BookServiceProperties properties) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.openLibraryService = openLibraryService;
        this.properties = properties;
    }

    @Override
    public Book addAuthor(Long bookId, Long authorId) {
        Optional<Book> book = this.bookRepository.findById(bookId);
        if (book.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Book with id %s not found", bookId));
        }
        Optional<Author> author = this.authorRepository.findById(authorId);
        if (author.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Author with id %s not found", authorId));
        }

        List<Author> authors = book.get().getAuthors();
        authors.add(author.get());

        book.get().setAuthors(authors);
        return this.bookRepository.save(book.get());
    }

    @Override
    @Transactional
    public Mono<Void> updateAllWithYear() {
        return Flux.fromIterable(this.bookRepository.findAll())
                .concatMap(this::updateBookYear)
                .then();
    }

    @Override
    public List<Book> findBooksByCountry(String country) {
        return this.bookRepository.findByAuthorsCountry(country);
    }

    /**
     * INFO a code-review-hoz:
     * [1] A 'query' feature 2 különböző módon lett implementálva:
     * BookController.queryFunctionalDriven()
     * BookController.queryDbDriven()
     *
     * [2] Az interjún feladat volt, hogy egészítsem ki a 'query' feature-t egy opcionális 'to' paramétterel.
     * A kiegészítés a queryDbDriven() változatban készült.
     * Az interjút úgy hagytuk abba, hogy a lekérdezés megfelelően működött ha:
     * (A) csak a 'from' paraméter volt megadva
     * (B) mindkét paraméter ('from', 'to') meg volt ada
     * De nem működött megfelelően ha:
     * (C) csak a 'to' paraméter volt megadva.
     *
     * [3] Az interjún azt feltételeztem, hogy a BookRepository-ban nem megfelelő a módosított query,
     * mert talán a Hibernate nem tudja megfeleően kiértékelni a :from IS NULL -t illetve a :to IS NULL -t.
     *
     * [4] Az interjút követően nyugodt körülmények között tesztelve hamar kiderült, hogy a query rendben volt,
     * csak a 93-as sorból kimaradt az új 'to' paraméter jelenlétének vizsgálata. A 93-as sort erre kellett
     * volna módosítani: if (from != null || to != null) {
     *
     */
    @Override
    public List<Book> findBooksByCountryAndYear(String country, Integer from, Integer to) {
        if (from != null) {
            return this.bookRepository.findByAuthorsCountryAndYearBetween(country, from, to);
        } else {
            return findBooksByCountry(country);
        }
    }

    @Override
    @Transactional
    public void resetAllYearToNull() {
        for (Book book : this.bookRepository.findAll()) {
            book.setYear(null);
            bookRepository.save(book);
        }
    }

    private Mono<Void> updateBookYear(Book book) {
        return updateBookYearRecursive(book, book.getWorkId(), 0);
    }

    private Mono<Void> updateBookYearRecursive(Book book, String workId, int depth) {
        if (depth >= properties.getRecursiveDepthLimit()) {
            return Mono.empty();
        }

        return this.openLibraryService.fetchBookDetailsByWorkId(workId)
                .flatMap(jsonResponse -> {
                    try {
                        JsonNode root = objectMapper.readTree(jsonResponse);
                        String jsonKeyFirstPublishDate = properties.getJsonKeyFirstPublishDate();
                        String jsonKeyLocation = properties.getJsonKeyLocation();

                        if (root.has(jsonKeyFirstPublishDate)) {
                            String firstPublishDate = root.get(jsonKeyFirstPublishDate).asText();
                            Integer year = extractYearFromFirstPublishDate(firstPublishDate);
                            book.setYear(year);
                            this.bookRepository.save(book);
                            return Mono.empty();
                        } else if (root.has(jsonKeyLocation)) {
                                String location = root.get(jsonKeyLocation).asText();
                                String newWorkId = extractNewWorkIdFromLocation(location);
                                return updateBookYearRecursive(book, newWorkId, depth + 1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return Mono.empty();
                });
    }

    private Integer extractYearFromFirstPublishDate(String firstPublishDate) {
        return Integer.parseInt(firstPublishDate.substring(firstPublishDate.length() - properties.getLengthOfYearString()));
    }

    private String extractNewWorkIdFromLocation(String location) {
        int indexOfLastSlash = location.lastIndexOf(properties.getSlashCharacter());
        return location.substring(indexOfLastSlash + 1);
    }

}
