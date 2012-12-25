package crawler;

import java.net.URL;
import java.util.Iterator;
import java.util.Queue;

public class CrawlerWorker extends Thread {
	private URL seed;
	private CrawlerController controller;
	
	public CrawlerWorker(URL seedUrl, CrawlerController controller) {
		this.seed = seedUrl;
		this.controller = controller;
	}
	
	public void start() {
		controller.decreaseAvailableThreads();
		System.out.println("Thread starting to analize " + seed.toString());
		URLHelper urlHelper = new URLHelper(seed);
		urlHelper.savePageContent();
		Queue<URL> links = urlHelper.extractLinks();
		Iterator<URL> it = links.iterator();
		while(it.hasNext()) {
			System.out.println("URL: " + it.next().toString());
		}
		synchronized(this) {
			controller.increaseAvailableThreads();			
			notifyAll();
		}
		controller.addFoundLinks(links.size());
		controller.followLinks(links, seed.toString());
	}
}
