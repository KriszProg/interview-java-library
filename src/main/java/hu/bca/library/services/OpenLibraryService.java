package hu.bca.library.services;

import reactor.core.publisher.Mono;

public interface OpenLibraryService {

    Mono<String> fetchBookDetailsByWorkId(String workId);

}
