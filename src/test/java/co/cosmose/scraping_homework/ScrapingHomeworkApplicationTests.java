package co.cosmose.scraping_homework;
import co.cosmose.scraping_homework.content.PublisherContent;
import co.cosmose.scraping_homework.content.PublisherContentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration and unit tests for the ScrapingHomeworkApplication.
 */
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ScrapingHomeworkApplicationTests {

	@MockBean
	private PublisherContentRepository repository;

	@Autowired
	private DataSource dataSource;
	private UUID testUUID;

	/**
	 * Tests if context of the application loads successfully.
	 */
	@Test
	void contextLoads() {
	}


	/**
	 * Tests removing links and unescaping text back to HTML
	 */
	@Test
	public void testRemovingLinksConvertingHtml()  {
		// Only article links are removed. Image data (with urls) remains
		String textWithLinks = "<![CDATA[ &lt;p&gt;texttexttext&lt;a href=\"https://mirrorthailand.com/tags/%E0%B9%84%E0%B8%AD%E0%B8%94%E0%B8%AD%E0%B8%A5\" rel=\"noopener noreferrer\" target=\"_blank\"&gt;&lt;/a&gt;texttexttexttexttexttexttexttexttexttexttexttexttexttexttexttext&lt;a href=\"https://mirrorthailand.com/tags/%E0%B8%AA%E0%B8%81%E0%B8%B4%E0%B8%99%E0%B9%80%E0%B8%AE%E0%B8%94\" rel=\"noopener noreferrer\" target=\"_blank\"&lt;/a&gt; texttexttexttexttexttext!&lt;/p&gt;&lt;p&gt;&lt;img src=\"https://media.thairath.co.th/image/yob27b1s1a2hys1a2hmXBzb3uT9rg8c3Su83pLLxDPPuBLnF5gMe9vA1.jpg\" data-id=\"101607\" data-url=\"https://media.thairath.co.th/image/yob27b1s1a2hys1a2hmXBzb3uT9rg8c3Su83pLLxDPPuBLnF5gMe9vA1.jpg\" style=\"width: auto;\" class=\"fr-fic fr-dib\"&gt;&lt;/p&gt;";
		String textToCompare1=  "<![CDATA[ &lt;p&gt;texttexttext texttexttexttexttexttexttexttexttexttexttexttexttexttexttexttext  texttexttexttexttexttext!&lt;/p&gt;&lt;p&gt;&lt;img src=\"https://media.thairath.co.th/image/yob27b1s1a2hys1a2hmXBzb3uT9rg8c3Su83pLLxDPPuBLnF5gMe9vA1.jpg\" data-id=\"101607\" data-url=\"https://media.thairath.co.th/image/yob27b1s1a2hys1a2hmXBzb3uT9rg8c3Su83pLLxDPPuBLnF5gMe9vA1.jpg\" style=\"width: auto;\" class=\"fr-fic fr-dib\"&gt;&lt;/p&gt;";;
		String textWithoutLinks = ScrapingHomeworkApplication.removeLinks(textWithLinks);
		assertEquals(textWithoutLinks, textToCompare1);
		String textToCompare2="<![CDATA[ <p>texttexttext texttexttexttexttexttexttexttexttexttexttexttexttexttexttexttext  texttexttexttexttexttext!</p><p><img src=\"https://media.thairath.co.th/image/yob27b1s1a2hys1a2hmXBzb3uT9rg8c3Su83pLLxDPPuBLnF5gMe9vA1.jpg\" data-id=\"101607\" data-url=\"https://media.thairath.co.th/image/yob27b1s1a2hys1a2hmXBzb3uT9rg8c3Su83pLLxDPPuBLnF5gMe9vA1.jpg\" style=\"width: auto;\" class=\"fr-fic fr-dib\"></p>";
		String textHtml = ScrapingHomeworkApplication.changeToHtml(textWithoutLinks);
		assertEquals(textHtml, textToCompare2);

	}


	/**
	 * This method tests if there is a correct number of records in the database after running the application
	 * @throws Exception if there is an error accessing the database
	 */
	@Test
	public void testCountRecordsInDatabase() throws Exception {
		ScrapingHomeworkApplication application = new ScrapingHomeworkApplication();
		application.setRepository(repository);
		application.run();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM publisher_content", Integer.class);
		assertEquals(30, count);
	}

	/**
	 * Tests if the data is saved correctly into the database. Shown by fetching 3 first records
	 * @throws SQLException if there is an error accessing the database
	 */
	@Test
	public void testFetchFirstThreeRecords() throws SQLException {
		ScrapingHomeworkApplication application = new ScrapingHomeworkApplication();
		application.setRepository(repository);
		try {
			application.run();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		String sql = "SELECT * FROM publisher_content LIMIT 3";


		List<PublisherContent> firstThreeRecords = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(PublisherContent.class));


		assertEquals(3, firstThreeRecords.size());

		// Sprawdź, czy pola każdego rekordu mają oczekiwane wartości
		PublisherContent record1 = firstThreeRecords.get(0);
		assertEquals("‘Girl Dad’ พ่อที่ใกล้ชิด และสนิทสนมกับลูกสาว โอบอุ้ม เปิดกว้าง และเข้าใจสิทธิเสรีภาพในตัวลูก ที่แสดงให้เห็นว่าผู้ชายก็เข้าใจความเป็นหญิงในตัวลูกสาวได้ไม่ต่างจากคนเป็นแม่", record1.getTitle());
		assertEquals("Mirror",  record1.getAuthor());
		assertEquals("https://media.thairath.co.th/image/PGWos1a2hwyUD5Nr8LE9Bob7VdbaJrYw8Ob6snWFs1zcs1a2h1Ee9vA1.jpg",  record1.getMainImageUrl());
		assertEquals("https://mirrorthailand.com/self/relationship/101641", record1.getArticleUrl());

		PublisherContent record2 = firstThreeRecords.get(1);
		assertEquals("องค์กรสิทธิมนุษยชนเรียกร้องให้ฝรั่งเศส ยกเลิกกฎ ‘ห้ามสวมฮิญาบ’ เข้าแข่งขันใน โอลิมปิก 2024 เพราะนับเป็นการเลือกปฏิบัติที่กีดกันทั้งเรื่องเพศและศาสนา",  record2.getTitle());
		assertEquals("Mirror",  record2.getAuthor());
		assertEquals("https://media.thairath.co.th/image/vPquws1a2hmcGDP2oyABIZcEJ5Y7SsqilRCxQjoOdgvMvuYe9vA1.jpg", record2.getMainImageUrl());
		assertEquals("https://mirrorthailand.com/movinon/socialissues/101640", record2.getArticleUrl());

		PublisherContent record3 = firstThreeRecords.get(2);
		assertEquals("ความสำคัญของการเปลี่ยน ‘คำนำหน้า’ ให้ตรงเพศสภาพ จากประสบการณ์ตรง ที่ผู้หญิงข้ามเพศในไทย ยังเลือกสู้ต่อไป จนกว่าจะถึงวันที่ “เราเท่ากันจริงๆ”", record3.getTitle());
		assertEquals("Mirror", record3.getAuthor());
		assertEquals("https://media.thairath.co.th/image/Pt9tpE1VpoVAnyFp1V2XS1s5Aotmqp1V2LyCmybExcf1dVEv0e9vA1.jpg", record3.getMainImageUrl());
		assertEquals("https://mirrorthailand.com/movinon/socialissues/101638", record3.getArticleUrl());
	}


}
