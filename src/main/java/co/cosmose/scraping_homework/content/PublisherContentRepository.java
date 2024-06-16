package co.cosmose.scraping_homework.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
/**
 * Repository interface for accessing PublisherContent entities.
 * It simplifies data access and management of the database.
 */
@Repository
public interface PublisherContentRepository extends JpaRepository<PublisherContent, UUID> {
    /**
     * Checks if an article with the provided URL already exists in the database.
     * @param articleUrl The URL of the article
     * @return true if an article was found inside the database, false otherwise
     */
    boolean existsByArticleUrl(String articleUrl);
}
