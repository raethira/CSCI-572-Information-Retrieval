import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

import java.awt.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;


public class MyCrawler extends WebCrawler {

		 private final static Pattern MATCH = Pattern.compile(".*(\\.(html|pdf|gif|jpeg|png))$");
    
		 private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|feed|rss|svg|json|vcf|xml|js|gif|jpg"
		 + "|mp3|zip|gz|json|x-crossword|pgp-signature|icon|x-icon|charset=utf-8|charset=UTF-8))$");
		 
		 
		 String crawlStorageFolder = "data/crawl/";
		 String fetchFile = "fetch_nytimes.csv";
		 String visitFile = "visit_nytimes.csv";
		 String urlsFile = "urls_nytimes.csv";
		 public static int count_fetch_attempted = 0,count_fetch_succeeded=0,count_fetch_failed=0,count_total_urls=0;
		 
		 //public static CopyOnWriteArrayList<String> al_total_urls_OK,al_total_urls_N_OK = new CopyOnWriteArrayList<String>();

		 //public   ArrayList<String> al_total_urls_OK,al_total_urls_N_OK = new ArrayList<String>();
		 
		 public static java.util.List<String> al_total_urls_OK =  Collections.synchronizedList(new ArrayList<String>());
		 
		 public static java.util.List<String> al_total_urls_N_OK = Collections.synchronizedList(new ArrayList<String>());
		 
		 public static java.util.List<String> al_content_type = Collections.synchronizedList(new ArrayList<String>());
		 
		 public static java.util.List<String> al_content_type1 = Collections.synchronizedList(new ArrayList<String>());
		 
		 
		 public static java.util.List<Integer> list_status = Collections.synchronizedList(new ArrayList<Integer>());
		 
		 public static java.util.List<Integer> list_status1 = Collections.synchronizedList(new ArrayList<Integer>());
		 
		 public static Set<String> hs = new HashSet<>();
		 
		 public static Set<Integer> hs1 = new HashSet<>();
		 
		 public static int[] sizeCount = new int[5];
		    
		    
		 /**
		     * This function is called once the header of a page is fetched. It can be
		     * overridden by sub-classes to perform custom logic for different status
		     * codes. For example, 404 pages can be logged, etc.
		     *
		     * @param webUrl WebUrl containing the statusCode
		     * @param statusCode Html Status Code number
		     * @param statusDescription Html Status COde description
		 */
		 @Override
		 protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
		        // Do nothing by default
		        // Sub-classed can override this to add their custom functionality
		        //String url = webUrl.getURL().toLowerCase();
			 count_fetch_attempted+=1;
		        try{
		            synchronized(this){
		                BufferedWriter bw = new BufferedWriter(new FileWriter(crawlStorageFolder+fetchFile,true));
		                bw.write(webUrl.getURL().replace(",", "_")+","+statusCode+"\n");
		                list_status.add(statusCode);
		                
		                if(Pattern.compile("2..").matcher(Integer.toString(statusCode)).matches())
		                	count_fetch_succeeded+=1;
		                else
		                	count_fetch_failed+=1;
		                
		                bw.close();
		                }
		            } catch(IOException e){
		                e.printStackTrace();
		            }
		    }
		    
		    
		    
		 /**
		 * This method receives two parameters. The first parameter is the page
		 * in which we have discovered this new url and the second parameter is
		 * the new url. You should implement this function to specify whether
		 * the given url should be crawled or not (based on your crawling logic).
		 * In this example, we are instructing the crawler to ignore urls that
		 * have css, js, git, ... extensions and to only accept urls that start
		 * with "http://www.viterbi.usc.edu/". In this case, we didn't need the
		 * referringPage parameter to make the decision.
		 */
		 @Override
		 
		 public boolean shouldVisit(Page referringPage, WebURL url) {
			 //String href = url.getURL().toLowerCase();

			 String href = url.getURL();

		     try
		     {
		            synchronized(this)
		            {
		                BufferedWriter bw = new BufferedWriter(new FileWriter(crawlStorageFolder+urlsFile, true));
		                if(href.startsWith("http://"+Controller.targetSite) || href.startsWith("https://" +Controller.targetSite))
		                {    bw.write(url.getURL().replace(",", "_") + ", OK\n");
		                	String temp=url.getURL().replace(",", "_");
		                	al_total_urls_OK.add(temp);
		                } 
		                else
		                {    bw.write(url.getURL().replace(",", "_")+ ", N_OK\n");
		                	String temp=url.getURL().replace(",", "_"); 
		                	al_total_urls_N_OK.add(temp);
		                
		                }
		                count_total_urls+=1;
		                
		                bw.close();
		            }
		     }
		     catch(IOException e)
		     {
		            e.printStackTrace();
		     }
			 
		     if(!(href.startsWith("http://"+Controller.targetSite) || (href.startsWith("https://"+Controller.targetSite))))
		            return false;
		     //		        if(NO_EXTENSION.matcher(href).matches())
		     //		            return true;
		     
			 return !FILTERS.matcher(href).matches();
			 //&& href.startsWith("https://www.nytimes.com/");
		 }
		 
		 /**
		  * This function is called when a page is fetched and ready
		  * to be processed by your program.
		  */
		  @Override
		  public void visit(Page page) {
		  String url = page.getWebURL().getURL();
		  System.out.println("URL: " + url);
		  
		  int size = page.getContentData().length;
	      int sizeKB = size/1024;
	      int numOfOutlink = page.getParseData().getOutgoingUrls().size();
	      String contentType = page.getContentType();
	      contentType = contentType.toLowerCase().indexOf(";") > -1
	                      ? contentType.replace(contentType.substring(contentType.indexOf(";"), contentType.length()), ""):contentType;
	      
	      
	      
	      try
	      {
	            synchronized (this){
	            	//System.out.println(contentType);
	            		
	            	contentType=contentType.trim();
	            	if( (Pattern.compile(".*(\\.(html|pdf|gif|jpeg|png))$").matcher(url).matches()  && (contentType.matches("(.*)application/json(.*)") || contentType.matches("(.*) application/rss+xml(.*)") ) )  || contentType.matches("(.*)text/html(.*)") ||contentType.matches("(.*)image/png(.*)") || contentType.matches("(.*)image/jpeg(.*)") || contentType.matches("(.*)image/gif(.*)") || contentType.matches("(.*)application/pdf(.*)"))
	                {
	            	
			            	if(sizeKB<1)
			                    sizeCount[0]++;
			                else if(1 <= sizeKB && sizeKB < 10)
			                    sizeCount[1]++;
			                else if(10 <= sizeKB && sizeKB < 100)
			                    sizeCount[2]++;
			                else if(100 <= sizeKB && sizeKB <1024)
			                    sizeCount[3]++;
			                else
			                    sizeCount[4]++;
			                BufferedWriter bw = new BufferedWriter(new FileWriter(crawlStorageFolder + visitFile, true));
			               
			                bw.write(url.replace(",", "_") + "," + sizeKB + "," + numOfOutlink + "," + contentType +"\n");
			                bw.close();
			                System.out.println(crawlStorageFolder + visitFile);
			                al_content_type.add(contentType);
			                al_content_type1.add(contentType);
	                }
	            }
	        }
	      catch(IOException e)
	      {
	            e.printStackTrace();
	      }
		  /*
		  if (page.getParseData() instanceof HtmlParseData) {
		  HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
		  String text = htmlParseData.getText();
		  String html = htmlParseData.getHtml();
		  Set<WebURL> links = htmlParseData.getOutgoingUrls();
		  System.out.println("Text length: " + text.length());
		  System.out.println("Html length: " + html.length());
		  System.out.println("Number of outgoing links: " + links.size());
		  }
		  */
		  
		  }
	
}
