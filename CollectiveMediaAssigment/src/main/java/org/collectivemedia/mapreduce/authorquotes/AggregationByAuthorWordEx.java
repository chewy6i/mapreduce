package org.collectivemedia.mapreduce.authorquotes;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.collectivemedia.mapreduce.authorquotes.AuthorWordTuple.KeyType;

public class AggregationByAuthorWordEx {

	
	public static class CountMapper
		extends Mapper<Object, Text, AuthorWordTuple, IntWritable> {
			
		private final static IntWritable one = new IntWritable(1);
		private Text author = new Text();
		AuthorWordTuple awtuple = new AuthorWordTuple();
		
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
			
			//Add the author only as key
			awtuple.setKeyType(KeyType.OnlyAuthor);
			awtuple.setAuthor(sauthor);
			awtuple.setWord("");
			context.write(awtuple, one);
			
			String squote = strval.substring(k+1);
			
			// Tokenize the string by splitting it up on whitespace into
			// something we can iterate over,
			// then send the tokens away
			
			StringTokenizer st = new StringTokenizer(squote);
			String sword = null;
			while(st.hasMoreTokens()) {
				sword = st.nextToken();
				
				// Remove some annoying punctuation
				sword = sword.replaceAll("'", ""); // remove single quotes (e.g., can't)
				sword = sword.replaceAll("[^a-zA-Z]", " "); // replace the rest with a space
				sword = sword.trim();//trim whitespaces
				
				//only consider words length of atleast 4
				if (sword.length() >=4) {
					//Add the word only as key
					awtuple.setKeyType(KeyType.OnlyWord);
					awtuple.setAuthor("");
					awtuple.setWord(sword);
					context.write(awtuple, one);

					//add author and word now as key
					awtuple.setKeyType(KeyType.AuthorWord);
					awtuple.setAuthor(sauthor);
					awtuple.setWord(sword);
					context.write(awtuple, one);
				}
			}

		}
	}
		
	
	public static class CountReducer 
		extends Reducer<AuthorWordTuple, IntWritable, Text, Text> {

		AuthorWordTuple awtuple = new AuthorWordTuple();
		Text author = new Text();
		Text wordlist = new Text();
		
		public void reduce(AuthorWordTuple key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			
			author.set(key.getAuthor());
			
			String sresult = new String();
			wordlist.set(sresult);
			
			/*int sum = 0;
			for (IntWritable val: values) {
				sum += val.get();
			}*/
			//result.set(sresult);
			
			context.write(author, wordlist);
		}
	}
	
	/**
	 * @param args
	 */
	public boolean SetJobParameters(Job job) {
		try {
			job.setJarByClass(AggregationByAuthorWordEx.class);
			job.setMapperClass(CountMapper.class);
			job.setCombinerClass(CountReducer.class);
			job.setReducerClass(CountReducer.class);
			job.setOutputKeyClass(AuthorWordTuple.class);
			job.setOutputValueClass(IntWritable.class);
			
		} catch(IllegalStateException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
