// Referred official documentation of Apache Tika:
	// https://tika.apache.org/1.19.1/examples.html
	// https://www.tutorialspoint.com/tika/tika_extracting_html_document.htm


import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;


public class Parser
{
	public static void main(String args[]) throws Exception
	{

		PrintWriter writer = new PrintWriter ("big.txt");
		String dirPath = "/home/rahul/Desktop/HW4-Rahul/Data/latimes-20181112T035524Z-001/latimes/latimes/latimes";
		File dir = new File(dirPath);
		int count = 0;
		try 
		{
	
			for(File file: dir.listFiles())
			{
		
				count++;
		
				BodyContentHandler handler = new BodyContentHandler(-1);
			    	Metadata metadata = new Metadata();
			    	ParseContext pcontext = new ParseContext();
			    	HtmlParser htmlparser = new HtmlParser();
				FileInputStream inputstream = new FileInputStream(file);
				htmlparser.parse(inputstream, handler, metadata,pcontext);
				String sentences = handler.toString();   // Extract sentences of each file
				String words[] = sentences.split(" ");
			
				for(String t: words)				// Extract words of each file
				{
					if(t.matches("[a-zA-Z]+\\.?"))
					{
						writer.print(t + " ");

						System.out.println(count);
						System.out.println(t);
						
					}
				}
				//if (count ==1)
				//{
				//	break;
			//	}
			
			}
	
		} catch (Exception e) 
		{
			e.printStackTrace();
		}

		writer.close();
		System.out.println("Done");

	}

}


