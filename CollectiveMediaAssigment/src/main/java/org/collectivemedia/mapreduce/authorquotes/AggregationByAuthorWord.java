package org.collectivemedia.mapreduce.authorquotes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class AggregationByAuthorWord {

	
	public static class CountMapper
		extends Mapper<Object, Text, Text, Text> {
			
		private Text author = new Text();
		private Text word = new Text();
		
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
					word.set(sword);
					context.write(author, word);
				}
			}

		}
	}
		
	
	public static class CountReducer 
		extends Reducer<Text, Text, Text, Text> {

		
		public void reduce(Text key, Iterable<Text> values,
				Context context) throws IOException, InterruptedException {

			Text wordlist = new Text();
			String word;
			Map<String, Integer> wordmap= new HashMap<String, Integer>();

			Integer count;
			for (Text val: values) {
		        word = val.toString();
		        if (word.contains(":") == false) {
		        	//this is for combiner
		        	//we get a list of words so we split and fill the hashtable 
					if ((count = wordmap.get(word)) == null) {
						wordmap.put(word, new Integer(1));
					} else {
						wordmap.put(word, new Integer(count.intValue() + 1));
					}
		        } else {
		        	//this is for reducer
		        	//we have wordlist in form of word: count so split this and 
		        	//fill the hashtable 
					StringTokenizer st = new StringTokenizer(word, ",");
					String token = null;
					String wordtoken;
					String counttoken;
					
					while(st.hasMoreTokens()) {
						token = st.nextToken();
						token.trim();
						
						//split the text into author and the quote using '/t'delimiter
						int k = token.indexOf(':');
						if (k < 0) {
							continue;
						}
						wordtoken = token.substring(0, k);
						counttoken = token.substring(k+1);

						wordtoken.trim();
						counttoken.trim();
						
						if ((count = wordmap.get(wordtoken)) == null) {
							wordmap.put(wordtoken, new Integer(Integer.parseInt(counttoken)));
						} else {
							wordmap.put(wordtoken, new Integer(count.intValue() + 1));
						}
					}
					
		        }
		        
			}
			
			StringBuilder sresult = new StringBuilder();
			
			for (Map.Entry<String, Integer> entry : wordmap.entrySet()) {
		        sresult.append(entry.getKey() + ":"+ entry.getValue().toString());
		        sresult.append(",");
			}

			wordlist.set(sresult.toString());
			context.write(key, wordlist);
		}
	}
	
	/**
	 * @param args
	 */
	public boolean SetJobParameters(Job job) {
		try {
			job.setJarByClass(AggregationByAuthorWord.class);
			job.setMapperClass(CountMapper.class);
			job.setCombinerClass(CountReducer.class);
			job.setReducerClass(CountReducer.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			
		} catch(IllegalStateException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
