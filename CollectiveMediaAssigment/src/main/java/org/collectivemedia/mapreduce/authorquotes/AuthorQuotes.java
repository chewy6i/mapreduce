package org.collectivemedia.mapreduce.authorquotes;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;


/* Logic:
 * There are 3 job types By Author, By Word , By Author By Word
 * By Author : each author is the key and value is count
 * 			mapper emits authorname, 1
 * 			combiner emits authorname, count
 * 			reducer emits authorname, finalcount
 * 
 * By Word : each word (length more than 4 with sanitization) is the key and value is count
 * 			mapper emits word, 1
 * 			combiner emits word, count
 * 			reducer emits word, finalcount
 * 
 * By Author By Word: each author is the key and value is word list
 * 			mapper emits authorname, word
 * 			combiner emits authorname, wordlist {word:count.....}
 * 			reducer emits authorname, wordlist {word:count.....}
 * 
 */
public class AuthorQuotes {
		/**
		 * @param args
		 */
		public static void main(String[] args) {
			Configuration conf = new Configuration();

			String[] otherArgs = null;
			try {
				otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(2);
			}
			if (otherArgs.length != 4) {
				System.err.println("Usage: AuthorQuotes <jobtype:1,2,3> <jobname> <inputfilename> <outputdir>");
				System.exit(2);
			}
					
			String jobtype = otherArgs[0];
			String jobname = otherArgs[1];
			String input = otherArgs[2];
			String output = otherArgs[3];
			
			String szjobtype = null;
			
			if (Integer.parseInt(jobtype) == 1) {
				szjobtype = "AggregationByAuthor";
			} else if (Integer.parseInt(jobtype) == 2) {
				szjobtype = "AggregationByWord";
			} else if (Integer.parseInt(jobtype) == 3) {
				szjobtype = "AggregationByAuthorWord";
				
			} else {
				System.out.println("jobtype MUST be 1,2 or 3");
				throw new IllegalArgumentException("jobtype MUST be 1,2 or 3");
			}
			
			System.out.println("Starting "+szjobtype+" job "+ jobname+ " with input="+ input+
					" and output="+output);
			
			Job job = null;
			try {
				job = new Job(conf, jobname);
				if (Integer.parseInt(jobtype) == 1) {
					AggregationByAuthor aggrjob = new AggregationByAuthor();
					if (!aggrjob.SetJobParameters(job)) {
						throw new IllegalStateException("Failed to set job params");
					}
				} else if (Integer.parseInt(jobtype) == 2) {
					AggregationByWord aggrjob = new AggregationByWord();
					if (!aggrjob.SetJobParameters(job)) {
						throw new IllegalStateException("Failed to set job params");
					}
				} else if (Integer.parseInt(jobtype) == 3) {
					AggregationByAuthorWord aggrjob = new AggregationByAuthorWord();
					if (!aggrjob.SetJobParameters(job)) {
						throw new IllegalStateException("Failed to set job params");
					}
				}
				/*experimental custom key
				else if (Integer.parseInt(jobtype) == 3) {
				AggregationByAuthorWordEx aggrjob = new AggregationByAuthorWordEx();
				if (!aggrjob.SetJobParameters(job)) {
					throw new IllegalStateException("Failed to set job params");
				}
				}
				*/

				FileInputFormat.addInputPath(job, new Path(input));
				FileOutputFormat.setOutputPath(job, new Path(output));
				
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(2);
			} catch (IllegalStateException e) {
				e.printStackTrace();
				System.exit(2);
			}
		
			try {
				System.exit(job.waitForCompletion(true) ? 0: 1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}
