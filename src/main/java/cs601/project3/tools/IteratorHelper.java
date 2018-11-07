package cs601.project3.tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * a iterator helper class responsible for combining iterator, and paging method 
 * @author yangzun
 *
 */
public class IteratorHelper {
	/**
	 * combine two iterator to one
	 * @param iters
	 * @return
	 */
	public static Iterator<String> combineMultipleIterator(List<Iterator<String>> iters) {
		if(iters == null) return null;
		int sz = iters.size();
		if(sz == 0 ) return null;
		Iterator<String> combined = new Iterator<String>() {
			int sz = iters.size();
			int curr = 0;
			Iterator<String> currIter = iters.get(curr);
			@Override
			public boolean hasNext() {
				if(currIter.hasNext()) {
					return true;
				}else{
					if(curr == sz -1) {
						return false;
					}else{
						currIter = iters.get(++curr);
						return currIter.hasNext();
					}
				}
			}
			@Override
			public String next() {
				return currIter.next();
			}
		};
		return combined;
		
	}
	
	/**
	 * method for returning only the contents for the page specified 
	 * @param iter the iterator representing all the data
	 * @param linesPerPage how many entries per page
	 * @param page the page number 
	 * @return
	 */
	public static List<String[]> toList(Iterator<String> iter, int linesPerPage, int page) {
		if(linesPerPage <= 0 ) {
			linesPerPage = 30;
		}
		if(page <= 0) {
			page = 1;
		}
		int passNumberOfLines = (page - 1) * linesPerPage;
		List<String[]> firstPage = new ArrayList<>();
		List<String[]> results = new ArrayList<>();
		String[] pageInfo = new String[3];
		firstPage.add(pageInfo);
		results.add(pageInfo);
		int count = 0;
		boolean hasAtLeastTwoPage = false;
		while(iter.hasNext()){
			count++;
			String line = iter.next();
			if(count <= linesPerPage) {
				String[] splited = line.split("\n");
				firstPage.add(splited);
			}else{
				hasAtLeastTwoPage = true;
			}
			if(count > passNumberOfLines) {
				String[] splited = line.split("\n");
				results.add(splited);
				if(count == passNumberOfLines + linesPerPage) {
					break;
				}
			}
		}
		boolean isLastPage = false;
		if(results.size() > 0) {
			if(!iter.hasNext()) {
				isLastPage = true;
			}
		}else{
			page = 1;
			isLastPage = !hasAtLeastTwoPage;
			results = firstPage;
		}
		boolean isFirstPage = (page == 1);
		results.get(0)[1] = page + "";
		if(isFirstPage) {
			results.get(0)[0] = null;
		}else{
			results.get(0)[0] = "prev";
		}
		if(isLastPage) {
			results.get(0)[2] = null;
		}else{
			results.get(0)[2] = "next";
		}
		return results;
	}
}
