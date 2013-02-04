package org.collectivemedia.mapreduce.authorquotes;
import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class AggregationByAuthor {

	public static class CountMapper
		extends Mapper<Object, Text, Text, IntWritable> {
		
		private final static IntWritable one = new IntWritable(1);
		private Text author = new Text();
		
		public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
			
			String strval = value.toString();
			
			//split the text into author and the quote using '/t'delimiter
			int k = strval.indexOf('\t');
			if (k < 0) {
				return;
			}

			
			String sauthor = strval.substring(0, k);
			author.set(sauthor);
			context.write(author, one);
		}
	}
	
	
	public static class CountReducer 
		extends Reducer<Text, IntWritable, Text, IntWritable> {

		private IntWritable result = new IntWritable();
		
		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val: values) {
				sum += val.get();
			}
			
			result.set(sum);
			context.write(key,  result);
		}
	}
	
	/**
	 * @param args
	 */
	public boolean SetJobParameters(Job job) {
		try {
			job.setJarByClass(AggregationByAuthor.class);
			job.setMapperClass(CountMapper.class);
			job.setCombinerClass(CountReducer.class);
			job.setReducerClass(CountReducer.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(IntWritable.class);
		} catch(IllegalStateException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
