import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller 
{
		 public final static String targetSite = "www.nytimes.com/";
	 
		 public static void main(String[] args) throws Exception 
		 {
			 
			 
			 String crawlStorageFolder = "data/crawl/";
			 
			 
			 String fetchFile = "fetch_nytimes.csv";
		     String visitFile = "visit_nytimes.csv";
		     String urlsFile = "urls_nytimes.csv";
		     String countFile = "count.txt";
		        
		     /*Basic configuration of the crawler*/
		     int numberOfCrawlers = 20;
		     int maxPagesToFetch = 20000;
		     int maxDepthOfCrawling = 16;
		     int politeDelay = 1000;
		        
		        
			 CrawlConfig config = new CrawlConfig();
			 config.setCrawlStorageFolder(crawlStorageFolder);
			 
			 config.setMaxPagesToFetch(maxPagesToFetch);
		     config.setMaxDepthOfCrawling(maxDepthOfCrawling);
		     //config.setPolitenessDelay(politeDelay);
		     config.setIncludeHttpsPages(true);
		     config.setFollowRedirects(true);
		     config.setIncludeBinaryContentInCrawling(true);
		     config.setUserAgentString("a-name");
		     
			 /*
			 * Instantiate the controller for this crawl.
			 */
			 PageFetcher pageFetcher = new PageFetcher(config);
			 RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
			 RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
			 CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
			 
			 /*Create storage file*/
		     try
		     {
		            BufferedWriter bw = new BufferedWriter(new FileWriter(crawlStorageFolder+fetchFile));
		            bw.write("URL, HTTP status code\n");
		            bw.close();

		            bw = new BufferedWriter(new FileWriter(crawlStorageFolder+visitFile));
		            bw.write("URLs Downloaded, SizeKB, # of outlinks found, Content-type\n");
		            bw.close();

		            bw = new BufferedWriter(new FileWriter(crawlStorageFolder+urlsFile));
		            bw.write("Encountered URL, Indicator\n");
		            bw.close();
		     }
		     catch (IOException e)
		     {
		            e.printStackTrace();
		     }
		        
		        
			 
			 /*
			 * For each crawl, you need to add some seed urls. These are the first
			 * URLs that are fetched and then the crawler starts following links
			 * which are found in these pages
			 */
			 controller.addSeed("https://"+targetSite);
					 /*
					  * Start the crawl. This is a blocking operation, meaning that your code
					  * will reach the line after this only when crawling is finished.
					  */
			 controller.start(MyCrawler.class, numberOfCrawlers);
			 //public static Set<String> hs = new HashSet<>();
			 
			 MyCrawler.hs.clear();
			 MyCrawler.hs.addAll(MyCrawler.al_total_urls_OK);
			 MyCrawler.al_total_urls_OK.clear();
			 MyCrawler.al_total_urls_OK.addAll(MyCrawler.hs);
             
			 
			 MyCrawler.hs.clear();
			 
			 
			 MyCrawler.hs.addAll(MyCrawler.al_total_urls_N_OK);
			 MyCrawler.al_total_urls_N_OK.clear();
			 MyCrawler.al_total_urls_N_OK.addAll(MyCrawler.hs);
     
			 
			 int c=MyCrawler.count_fetch_failed;
			 int abort=Collections.frequency(MyCrawler.list_status, 200)-MyCrawler.count_fetch_succeeded;
			 int failed=c-abort;
			 
			 System.out.println("# of fetches attempted                                                  : "+ (MyCrawler.count_fetch_succeeded  + MyCrawler.count_fetch_failed ));
			 System.out.println("# of fetches succeeded                                                  : "+MyCrawler.count_fetch_succeeded);
			 System.out.println("# of fetches failed or aborted = (# fetches failed + # fetches aborted) : "+ failed + " + "+ abort+" = "+c  );
			 
			 System.out.println("");
			 
			 System.out.println("# of total outgoing URLs                  : "+MyCrawler.count_total_urls);
			 System.out.println("# of unique URLs extracted                : "+ (MyCrawler.al_total_urls_OK.size()  + MyCrawler.al_total_urls_N_OK.size() ) );
			 System.out.println("# of unique URLs within your news website : "+MyCrawler.al_total_urls_OK.size());
			 System.out.println("# of unique URLs outside the news website : "+MyCrawler.al_total_urls_N_OK.size());
			 
			 MyCrawler.hs1.clear();
			 
			 MyCrawler.hs1.addAll(MyCrawler.list_status);
			 MyCrawler.list_status1.clear();
			 MyCrawler.list_status1.addAll(MyCrawler.hs1);
			 

			 System.out.println("");
			 
			 System.out.println("Status Codes :");
			 System.out.println("============");
			 Collections.sort(MyCrawler.list_status1); 
			 
			 for (int i = 0; i < MyCrawler.list_status1.size(); i++) 
			 {
					System.out.println(MyCrawler.list_status1.get(i) +" : " + Collections.frequency(MyCrawler.list_status,  MyCrawler.list_status1.get(i)) );
					
			 }	
			 
			 try
			 {
		            BufferedWriter bw = new BufferedWriter(new FileWriter(crawlStorageFolder+countFile));
		            bw.write("< 1KB: "+MyCrawler.sizeCount[0]+"\n");
		            bw.write("\r\n"+"1KB ~ <10KB: "+MyCrawler.sizeCount[1]+"\n");
		            bw.write("\r\n"+"10KB ~ <100KB: "+MyCrawler.sizeCount[2]+"\n");
		            bw.write("\r\n"+"100KB ~ <1MB: "+MyCrawler.sizeCount[3]+"\n");
		            bw.write("\r\n"+">= 1MB: "+MyCrawler.sizeCount[4]+"\n");
		            bw.close();
		            
		            
		            System.out.println("");
					 
					System.out.println("File Sizes   :");
					System.out.println("==========");
					System.out.println("< 1KB        : "+MyCrawler.sizeCount[0]);
					System.out.println("1KB ~ <10KB  : "+MyCrawler.sizeCount[1]);
					System.out.println("10KB ~ <100KB: "+MyCrawler.sizeCount[2]);
					System.out.println("100KB ~ <1MB : "+MyCrawler.sizeCount[3]);
					System.out.println(">= 1MB       : "+MyCrawler.sizeCount[4]);
					 
					 
		     }
			 catch (IOException e)
			 {
		            e.printStackTrace();
		     }
			 
			 
			 MyCrawler.hs.clear();
			 
			 MyCrawler.hs.addAll(MyCrawler.al_content_type1);
			 MyCrawler.al_content_type1.clear();
			 MyCrawler.al_content_type1.addAll(MyCrawler.hs);
			 
			 
			 System.out.println("");
			 
			 System.out.println("Content Types :");
			 System.out.println("=============");
			 
			 for (int i = 0; i < MyCrawler.al_content_type1.size(); i++) 
			 {
					System.out.println(MyCrawler.al_content_type1.get(i) +" : " + Collections.frequency(MyCrawler.al_content_type,  MyCrawler.al_content_type1.get(i)) );
					
			 }	
			 
			 
		}
}