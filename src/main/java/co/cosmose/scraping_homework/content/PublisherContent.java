package co.cosmose.scraping_homework.content;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PublisherContent {

    @Id
    @Builder.Default
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id = UUID.randomUUID();
    private String articleUrl;
    private String title;
    private String author;
    @Column(columnDefinition = "TEXT")
    private String htmlContent;
    @Column(columnDefinition = "TEXT")
    private String originalContent;
    private String mainImageUrl;
}
