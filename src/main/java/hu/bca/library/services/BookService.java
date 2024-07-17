package hu.bca.library.services;

import hu.bca.library.models.Book;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BookService {
    Book addAuthor(Long bookId, Long authorId);

    Mono<Void> updateAllWithYear();

}
