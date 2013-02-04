MapReduce tasks to process quotes 
====================================
* Checking in the 3 job types plus the experimental 4th type.
* Testing folder contains quotes.txt, Authors.txt, words.txt and Authorwords.txt

#1 AuthorQuotes.java
this is the main class which invokes the various jobs

There are 3 job types By Author, By Word , By Author By Word

#2 AggregationByAuthor.java
By Author : each author is the key and value is count
  		mapper emits authorname, 1
			combiner emits authorname, count
			reducer emits authorname, finalcount
Output: 
    Agatha Christie  1
    Alan Bleasdale	1
    Albert Camus	1
    Albert Einstein	1


#3 AggregationByWord.java
By Word : each word (length more than 4 with
sanitization) is the keya and value is count
			mapper emits word, 1
			combiner emits word, count
			reducer emits word, finalcount
Output:
    Advice  1
    After	1
    Alas	1
    America	1
    Twenty Nine	2

#4 AggregationByAuthorWord.java
By Author By Word: each author is the key and value is word list
			mapper emits authorname, word
			combiner emits authorname, wordlist {word:count.....}
			reducer emits authorname, wordlist {word:count.....}

Output:
    Agatha Christie  more:1,have:1,older:1,woman:1,gets:1,archaeologist:1,best:1,husband:1,interested:1,
    Alan Bleasdale	body:1,parallel:1,heart:1,exercise:1,study:1,resist:1,must:2,love:1,vigor:1,frigidity:1,combine:1,mind:1,keep:1,these:1,
    Albert Camus	after:1,face:1,Alas:1,responsible:1,certain:1,every:1,


#5 AggregationAuthorWordEx.java
 Experimental class with custom key. goal is to use single parsing step
to get both authors and words. difficulty in getting the authorword (3rd
job type).Shows usage of custom keys.

#6 AuthorWordTuple.java
custom key class to use single parsing step to get authors and words. Used by the AggregationAuthorWordEx.java (see #5 above).


=======================

