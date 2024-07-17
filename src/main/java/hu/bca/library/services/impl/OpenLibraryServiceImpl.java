package hu.bca.library.services.impl;

import hu.bca.library.properties.OpenLibraryProperties;
import hu.bca.library.services.OpenLibraryService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
public class OpenLibraryServiceImpl implements OpenLibraryService {

    private final WebClient webClient;
    private final OpenLibraryProperties properties;

    public OpenLibraryServiceImpl(WebClient.Builder webClientBuilder, OpenLibraryProperties properties) {
        this.webClient = webClientBuilder.baseUrl(properties.getBaseUrl()).build();
        this.properties = properties;
    }

    @Override
    public Mono<String> fetchBookDetailsByWorkId(String workId) {
        return this.webClient.get()
                .uri(properties.getBookDetailsUri() , workId)
                .retrieve()
                .bodyToMono(String.class);
    }

}
