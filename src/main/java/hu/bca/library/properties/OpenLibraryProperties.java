package hu.bca.library.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openlibrary")
@Getter
@Setter
public class OpenLibraryProperties {
    private String baseUrl;
    private String bookDetailsUri;

    @Override
    public String toString() {
        return "OpenLibraryProperties{" +
                "baseUrl='" + baseUrl + '\'' +
                ", bookDetailsUri='" + bookDetailsUri + '\'' +
                '}';
    }

}
