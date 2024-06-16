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

	@Override
	public void run(String... args) throws Exception {

		String url="https://connect.thairath.co.th/ws/kaikai/content/mirror";
		try {
			Document doc = Jsoup.connect(url).get();
			Elements items = doc.select("item");
			//int i=0;
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
						//we remove link with HTML tags to eliminate image configuration and blank spaces
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
	public static String removeLinks(String text){
		text = removeTextBetween(text, "&lt;a", "&lt;/a&gt;");
		text = removeTextBetween(text, "&lt;img src", "&gt;");
		text = removeTextBetween(text, "&lt;p&gt;https", "&gt;");
		String regex = "\\b(https?|ftp):\\/\\/[-A-Z0-9+&@#\\/%?=~_|!:,.;]*[-A-Z0-9+&@#\\/%=~_|]";
		text = text.replaceAll(regex, "");
		String regex2 = "\\b((https?|ftp):\\/\\/|www\\.)[-A-Z0-9+&@#\\/%?=~_|!:,.;]*[-A-Z0-9+&@#\\/%=~_|]";
		text = text.replaceAll(regex2, "");
		return text;
	}
	public static String changeToHtml(String originalContent){
		String htmlContent=new String(originalContent);
		htmlContent= htmlContent.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&").replaceAll("&quot;","\"").replaceAll("&nbsp;", " ");
		return htmlContent;

	}
}

