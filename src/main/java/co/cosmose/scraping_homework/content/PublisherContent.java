package co.cosmose.scraping_homework.content;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Represents information of a singular article scraped from RSS content feed
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PublisherContent {

    /**
     * The unique identifier of represented article
     */
    @Id
    @Builder.Default
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id = UUID.randomUUID();
    /**
     * The URL of the article.
     */
    private String articleUrl;
    /**
     * The title of the article.
     */
    private String title;
    /**
     * The author of the article.
     */
    private String author;

    /**
     * The content of the article unescaped back to HTML format
     */
    @Column(columnDefinition = "TEXT")
    private String htmlContent;
    /**
     * Original content of the article
     */
    @Column(columnDefinition = "TEXT")
    private String originalContent;
    /**
     * The URL of the main image of the article.
     */
    private String mainImageUrl;
}
