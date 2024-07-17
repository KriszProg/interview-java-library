package hu.bca.library.controllers;

import hu.bca.library.models.Book;
import hu.bca.library.services.BookService;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@RepositoryRestController("books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @ResponseStatus(HttpStatus.CREATED)

    @RequestMapping("/{bookId}/add_author/{authorId}")
    @ResponseBody Book addAuthor(@PathVariable Long bookId, @PathVariable Long authorId) {
        return this.bookService.addAuthor(bookId, authorId);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/update-all-with-year", method = RequestMethod.PATCH)
    public Mono<Void> updateAllWithYear() {
        return this.bookService.updateAllWithYear();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/query/functional/{country}", method = RequestMethod.GET)
    @ResponseBody
    public List<Book> queryFunctionalDriven(@PathVariable String country,
                                            @RequestParam(name = "from", required = false) Integer from) {
        List<Book> bookListByCountry = this.bookService.findBooksByCountry(country);

        if (from != null) {
            bookListByCountry = bookListByCountry.stream()
                    .filter(book -> book.getYear() != null && book.getYear() >= from)
                    .collect(Collectors.toList());
        }

        bookListByCountry.sort(Comparator.comparing(Book::getYear, Comparator.nullsLast(Comparator.naturalOrder())));

        return bookListByCountry;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/reset-all-year", method = RequestMethod.PATCH)
    public void resetAllYearToNull() {
        this.bookService.resetAllYearToNull();
    }

}
