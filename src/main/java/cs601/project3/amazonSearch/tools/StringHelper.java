package cs601.project3.amazonSearch.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Helper class for operations about strings including partial match
 * @author yangzun
 *
 */
public class StringHelper {
	public static void main(String[] args) {
		System.out.println("fd".indexOf(null));
	}
	
	/**
	 * partial match tokens and sort them by degree of matching
	 * shorter tokens have greater degree of matching
	 * @param term
	 * @return
	 */
	public static List<String> partialMatchTokens(String term, Collection<String> tokens) {
		List<String> matched = new ArrayList<>();
		if(term == null) return matched;
		for (String token : tokens) {
			int index = token.indexOf(term);
			if(index >= 0) {
				matched.add(token);
			}
		}
		Collections.sort(matched,new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.length() - o2.length();
			}
		});
		return matched;
	}
	
	/**
	 * split by white spaces tokens in a text string, non-alphanumeric characters are discarded.
	 * @param text
	 * @return
	 */
	public static String[] splitTokens(String text) {
		text = text.toLowerCase();
		text = text.replaceAll("[^A-Za-z0-9\\s]", "");//https://stackoverflow.com/questions/1805518/replacing-all-non-alphanumeric-characters-with-empty-strings
		String[] splited = text.split("\\s+");//https://stackoverflow.com/questions/7899525/how-to-split-a-string-by-space
		if(splited.length >=1 && "".equals(splited[0])){
			splited = Arrays.copyOfRange(splited,1,splited.length);//https://stackoverflow.com/questions/4439595/how-to-create-a-sub-array-from-another-array-in-java
			//it's shallow copy,  no need to worry unnecessary new instances of String will be created
			//https://stackoverflow.com/questions/15135104/system-arraycopy-copies-object-or-reference-to-object
		}
		return splited;
	}
}
