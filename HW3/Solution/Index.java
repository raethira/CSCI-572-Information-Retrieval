import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Index 
{

	 public static class IndexMapper extends Mapper < Object, Text, Text, Text > 
	 {

		  Text word = new Text();
		  Text DocID = new Text();

		  public void map(Object key, Text value, Context context) throws IOException,InterruptedException 
		  {

			    String line = value.toString();
			    StringTokenizer tokens = new StringTokenizer(line);
			    String ID = tokens.nextToken();
			    DocID = new Text(ID);
				String cleaned_word;
				String s;

			   while (tokens.hasMoreTokens()) 
			   {
					cleaned_word=tokens.nextToken().toString().toLowerCase();
					s=cleaned_word;

					String[] arr = s.split("[^a-zA-Z]+");
					
					for( int i = 0; i < arr.length; i += 1)
					{
							if (arr[i].length() >0)
							{
								word.set(arr[i].trim());
								context.write(word, DocID);
							}
					}
			   }
		  }
	 }

	 public static class IndexReducer extends Reducer < Text, Text, Text, Text > 
	 {
			  public void reduce(Text key, Iterable < Text > values, Context context) throws IOException, InterruptedException 
			  {
				   HashMap < String, Integer > map = new HashMap < String, Integer > ();

					  for (Text value : values) 
					  {
								if(!map.containsKey(value.toString()))
								{
									map.put(value.toString(),1);
								}
								else
									map.put(value.toString(),( map.get(value.toString()) + 1));
					  }
				 
					String a="";
					
					for(String id: map.keySet())
					{
							 a+=id+":"+Integer.toString(map.get(id))+"\t";
					}

					context.write(key, new Text(a));
			}
	  }
	  
	  
	 public static void main(String[] args) throws Exception 
	 {
		  Configuration conf = new Configuration();
		  Job job = Job.getInstance(conf, "Inverted Index");
		  job.setJarByClass(Index.class);
		  job.setMapperClass(IndexMapper.class);
		  job.setReducerClass(IndexReducer.class);
		  job.setOutputKeyClass(Text.class);
		  job.setOutputValueClass(Text.class);
		  FileInputFormat.addInputPath(job, new Path(args[0]));
		  FileOutputFormat.setOutputPath(job, new Path(args[1]));
		  System.exit(job.waitForCompletion(true) ? 0 : 1);
	 }
}
    