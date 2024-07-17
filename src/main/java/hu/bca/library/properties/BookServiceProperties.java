package hu.bca.library.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "bookservice")
@Getter
@Setter
public class BookServiceProperties {

    private int recursiveDepthLimit;
    private String jsonKeyFirstPublishDate;
    private String jsonKeyLocation;
    private int lengthOfYearString;
    private char slashCharacter;

    @Override
    public String toString() {
        return "BookServiceProperties{" +
                "recursiveDepthLimit=" + recursiveDepthLimit +
                ", jsonKeyFirstPublishDate='" + jsonKeyFirstPublishDate + '\'' +
                ", jsonKeyLocation='" + jsonKeyLocation + '\'' +
                ", lengthOfYearString=" + lengthOfYearString +
                ", slashCharacter=" + slashCharacter +
                '}';
    }

}
