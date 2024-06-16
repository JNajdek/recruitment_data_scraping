package co.cosmose.scraping_homework;
import co.cosmose.scraping_homework.content.PublisherContent;
import co.cosmose.scraping_homework.content.PublisherContentRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class ScrapingHomeworkApplication implements CommandLineRunner{
	@Autowired
	private PublisherContentRepository repository;
	public void setRepository(PublisherContentRepository repository) {
		this.repository = repository;
	}

	public static void main(String[] args) {
		SpringApplication.run(ScrapingHomeworkApplication.class, args);
	}

	/**
	 * Method responsible for scraping RSS content feed from an external publisher and saving it into the database.
	 * It also removes all links from the <description> elements
	 * @param args Command-line arguments
	 * @throws Exception If an error occurs during running of the application
	 */
	@Override
	public void run(String... args) throws Exception {

		String url="https://connect.thairath.co.th/ws/kaikai/content/mirror";
		try {
			Document doc = Jsoup.connect(url).get();
			Elements items = doc.select("item");
			for (Element item : items) {
					String articleUrl = item.select("link").first().text();

					String title = item.select("title").first().text();

					Element creatorElement = item.select("dc|creator").first();
					String author = (creatorElement != null) ? creatorElement.text() : "No author";


					Element imageElement = item.select("media|content[url]").first();
					String imageUrl = (imageElement != null) ? imageElement.attr("url") : "No image";

					Element descriptionElement = item.select("description").first();

					String originalContent;
					String htmlContent;
					if (descriptionElement != null) {
						originalContent = descriptionElement.text();
						originalContent = removeLinks(originalContent);
						htmlContent = changeToHtml(originalContent);
					} else {
						originalContent = "No description";
						htmlContent = "No description";
					}

				if (!repository.existsByArticleUrl(articleUrl)) {
					PublisherContent content = PublisherContent.builder()
							.mainImageUrl(imageUrl)
							.articleUrl(articleUrl)
							.title(title)
							.author(author)
							.htmlContent(htmlContent)
							.originalContent(originalContent)
							.build();
					repository.save(content);
				}
			}

			List<PublisherContent> contents = repository.findAll();
			contents.forEach(System.out::println);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is responsible for removal of the text between specified start and end strings from a given text
	 * @param text whole text that needs to be trimmed
	 * @param startStr beginning of the part of the text that needs to be removed
	 * @param endStr end of the part of the text that needs to be removed
	 * @return text without removed parts which were in between startStr and endStr
	 */
	public static String removeTextBetween(String text, String startStr, String endStr) {
		String regex = Pattern.quote(startStr) + ".*?" + Pattern.quote(endStr);
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);

		StringBuffer result = new StringBuffer();
		while (matcher.find()) {
			((Matcher) matcher).appendReplacement(result, " ");
		}
		matcher.appendTail(result);

		return result.toString();
	}

	/**
	 * This method removes links to other articles from the text
	 * @param text provided text containing unwanted links
	 * @return text without unwanted links
	 */
	public static String removeLinks(String text){
		text = removeTextBetween(text, "&lt;a", "&lt;/a&gt;");

//  The instruction specified the removal of only the links to other articles. If it is required to also remove the links to images, please uncomment the following line.
//		text = removeTextBetween(text, "&lt;img src", "&gt;");
		return text;
	}

	/**
	 * This method unescapes the originally scraped content back to HTML format
	 * @param originalContent The content originally scraped from RSS source feed containing escaped HTML characters
	 * @return content unescaped back to HTML format
	 */
	public static String changeToHtml(String originalContent){
		String htmlContent= originalContent;
		htmlContent= htmlContent.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&").replaceAll("&quot;","\"").replaceAll("&nbsp;", " ");
		return htmlContent;

	}
	
}

