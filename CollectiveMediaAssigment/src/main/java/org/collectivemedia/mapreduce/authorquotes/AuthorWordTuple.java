package org.collectivemedia.mapreduce.authorquotes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class AuthorWordTuple extends ObjectWritable implements WritableComparable<AuthorWordTuple>{

	public enum KeyType {
		OnlyAuthor,
		OnlyWord,
		AuthorWord
	}
	
	private KeyType keytype;
	private String author;
	private String word;
	
	public KeyType getKeyType() {
		return keytype;
	}

	public void setKeyType(KeyType keytype) {
		this.keytype = keytype;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String sword) {
		this.word = sword;
	}


	public void write(DataOutput out) throws IOException {
		out.writeUTF(keytype.toString());
		out.writeUTF(author);
		out.writeUTF(word);
	}

	public void readFields(DataInput in) throws IOException {
		keytype = KeyType.valueOf(in.readUTF());
		author = in.readUTF();
		word = in.readUTF();
	}

	public int compareTo(AuthorWordTuple other) {
		if (getKeyType().compareTo(other.getKeyType()) != 0) {
            return getKeyType().compareTo(other.getKeyType());
        } else if (getAuthor().compareTo(other.getAuthor()) != 0) {
            return getAuthor().compareTo(other.getAuthor());
        } else if (getWord().compareTo(other.getWord()) != 0) {
            return getWord().compareTo(other.getWord());
        } else {
            return 0;
        }
	}
	
	public String toString() {
		return keytype.toString()+author+word;
	}
	
	public boolean equals(AuthorWordTuple other) {
		if (getKeyType().compareTo(other.getKeyType()) == 0 &&
			getAuthor().compareTo(other.getAuthor()) == 0 &&
			getWord().compareTo(other.getWord()) == 0) {
         
			return true;
		}
       return false;
	}
	
	public int hashcode() {
		return keytype.hashCode()+author.hashCode()+word.hashCode();
	}

	public static class KeyComparator extends WritableComparator {
        public KeyComparator() {
            super(AuthorWordTuple.class);
        }

        public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
            return compareBytes(b1, s1, l1, b2, s2, l2);
        }
    }

    static { // register this comparator
        WritableComparator.define(AuthorWordTuple.class, new KeyComparator());
    }
    
}
