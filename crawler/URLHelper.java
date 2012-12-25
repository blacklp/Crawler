package crawler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLHelper {
	private URL url;
	private String pageContent;
	
	public URLHelper(URL url) {
		this.url = url;
	}
	
	public void savePageContent() {
		try {
			URLConnection connection = url.openConnection();
			InputStream inputStream = connection.getInputStream();
			StringBuffer buffer = new StringBuffer();
			Reader r = new InputStreamReader(inputStream, "UTF-8");
			char[] b = new char[2048];
			int numRead = r.read(b);
			
			while(numRead > 0) {
				buffer.append(b, 0, numRead);
				numRead = r.read(b);
			}
			String pageSource = buffer.toString();
			this.pageContent = pageSource;
		}catch(IOException e) {
			System.out.println("Error: An IOException occurred when retrieving the content.");
		}
	}
	
	public Queue<URL> extractLinks() {
		Queue<URL> q = new ConcurrentLinkedQueue<URL>();
		final int flags = Pattern.CASE_INSENSITIVE | Pattern.MULTILINE;
		Pattern tagPattern = Pattern.compile("<a([^>]+)>(.+?)</a>", flags);
		String linkRegEx = "href\\s*=\\s*(\"[^\"]*\")|('[^']*')|[^'\"\\s>]+";
		Pattern linkPattern = Pattern.compile(linkRegEx, flags);
		Matcher tagMatcher = tagPattern.matcher(this.pageContent);
		
		while(tagMatcher.find()) {
			String href = tagMatcher.group(1);
			Matcher linkMatcher = linkPattern.matcher(href);
			while(linkMatcher.find()) {
				String link = linkMatcher.group(1);
				if(link != null) {
					if(link.charAt(0) == '"' || link.charAt(0) == '\'') {
						link = link.substring(1, link.length() - 1);
					}
					if(link.charAt(0) == '/' || link.charAt(0) == '\\') {
						link = url.getProtocol() + "://" + url.getHost() + link;
					}
					try {
						URL url = new URL(link);
						q.add(url);
					}catch(MalformedURLException e) {
						System.out.println("Error: An invalid link was found.");
					}
					System.out.println("link: " + link);
				}
			}
		}
		return q;
	}
}
