package hu.bca.library.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String workId;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "book_author"
            , joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id")
            , inverseJoinColumns = @JoinColumn(name = "author_id", referencedColumnName = "id"))
    @JsonIgnoreProperties({"books"})
    private List<Author> authors;

    @JsonIgnoreProperties({"authors"})
    @OneToMany(mappedBy = "book")
    private List<Rental> rentals;
}
