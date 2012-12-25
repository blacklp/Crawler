package crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Queue;
import java.util.Scanner;
import java.util.TreeSet;

public class CrawlerController {
	private int availableThreads;
	private int numFoundPages;
	private TreeSet<String> analizedPages;
	private final int MAX_URLS = 1000;
	
	public CrawlerController(URL seedUrl, int numThreads) {
		long start = System.currentTimeMillis();
		this.availableThreads = numThreads;
		this.analizedPages = new TreeSet<String>();
		this.numFoundPages = 0;
		CrawlerWorker initialCrawler = new CrawlerWorker(seedUrl, this);
		initialCrawler.start();
		long end = System.currentTimeMillis();
		System.out.println("The crawler took " + (end - start) + " milliseconds to find the " + numFoundPages + " links.");
	}
	
	public void followLinks(Queue<URL> links, String name) {
		try {
			while(!links.isEmpty() && numFoundPages < MAX_URLS) {
				while(this.availableThreads == 0) wait();
				URL link = links.poll();
				if(!this.hasBeenAnalized(link.toString())) { //in order to prevent from infinite loops
					this.analizedPages.add(link.toString());
					System.out.println("New crawler worker for page " + link.toString());
					new CrawlerWorker(link, this).start();
				}
			}
			if(numFoundPages == MAX_URLS) {
				System.out.println("ENDED! All the required " + MAX_URLS + " URLs were analized.");
			}
		}catch(InterruptedException e) {
			System.out.println("Error: An Interrupted Exception occurred in the thread.");
		}
	}
	
	private synchronized boolean hasBeenAnalized(String url) {
		if(analizedPages == null) return false;
		return analizedPages.contains(url);
	}
	
	public synchronized void decreaseAvailableThreads() {
		availableThreads--;
	}
	
	public synchronized void increaseAvailableThreads() {
		availableThreads++;
	}
	
	public void addAnalizedPage(String link) {
		this.analizedPages.add(link.toString());
	}

	public void addFoundLinks(int num) {
		this.numFoundPages += num;
	}
	
	public static void main(String[] args) {
		System.out.println("Welcome to the Java Web Crawler.");
		System.out.println("Please enter the seed URL to start crawling:");
		Scanner s = new Scanner(System.in);
		String seed = s.nextLine();
		System.out.println("Please enter the number of threads that you want to use:");
		int numThreads = s.nextInt();
		try {
			URL seedUrl = new URL(seed);
			new CrawlerController(seedUrl, numThreads);
		}catch(MalformedURLException e) {
			System.out.println("Error: Malformed URL!");
		}
	}
}
