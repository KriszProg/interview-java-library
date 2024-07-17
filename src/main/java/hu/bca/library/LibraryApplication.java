package hu.bca.library;

import hu.bca.library.properties.BookServiceProperties;
import hu.bca.library.properties.OpenLibraryProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({BookServiceProperties.class, OpenLibraryProperties.class})
public class LibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }
}
