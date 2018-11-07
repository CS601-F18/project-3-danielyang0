package cs601.project3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cs601.project3.amazonSearch.model.AmazonDocument;
import cs601.project3.amazonSearch.model.AmazonSearchUI;
import cs601.project3.amazonSearch.model.InvertedIndex;
import cs601.project3.amazonSearch.model.ListOfDocsWithFreqs;
import cs601.project3.amazonSearch.model.QADocument;
import cs601.project3.amazonSearch.model.ReviewDocument;
import cs601.project3.amazonSearch.tools.AmazonSearchFactory;
import cs601.project3.amazonSearch.tools.StringHelper;
import cs601.project3.tools.IteratorHelper;
import cs601.project3.tools.PropertyReader;

/**
 * the class for reading amazon data files, and executing command including find, search, partial search,etc
 * @author yangzun
 *
 */
public class AmazonSearch {
	private static Logger logger = Logger.getLogger(AmazonSearch.class);
	static {
		PropertyConfigurator.configure("./config/log4j.properties");
	}
	
	private InvertedIndex<QADocument> qaDatabase;//question/answer Inverted Index database
	private InvertedIndex<ReviewDocument> reviewDatabase;//review Inverted Index database
	private static AmazonSearch instance;
	private String qaFileName;
	private String reviewFileName;
	
	private AmazonSearch() {
		super();
		PropertyReader reader = new PropertyReader("./config","project3.properties");
		this.qaFileName = reader.readStringValue("qaFile", "qa1.json");
		this.reviewFileName = reader.readStringValue("reviewFile", "review1.json");
	}

	/**
	 * singleton with double checked locking approach which guarantees thread safe
	 * @return
	 */
	public static AmazonSearch getInstance() {
		if(instance == null) {
			synchronized (AmazonSearch.class) {
				if (instance == null) {
					instance = buildAmazonSearch();
				}
			}
		}
		return instance;
	}
	
	/**
	 * create AmazonSearch object, initiate its databases, if file name error, return null
	 * @return the initiated AmazonSearch object or null if file name is incorrect.
	 */
	private static AmazonSearch buildAmazonSearch() {
		AmazonSearch searchEngine = new AmazonSearch();
		if(AmazonSearchFactory.initDatabases(searchEngine, searchEngine.qaFileName, searchEngine.reviewFileName)){
			logger.info("amazon search database initiated!");
			return searchEngine;
		}
		logger.info("amazon search database initiation failed!");
		return null;
	}
	
	
	/**
	 * search review or find asin for multiple terms, and combined results to an iterator
	 * @param terms
	 * @param category specify to call search review or find method
	 * @return
	 */
	public Iterator<String> getCombinedAmazonResultsForMultipleTerms(String terms, String category) {
		String[] splitedTerms = terms.split("\\s+");
		List<Iterator<String>> iters = new ArrayList<>();
		for (String term : splitedTerms) {
			if("review".equals(category)) {
				iters.add(getSearchReviewResults(term));
			}else if("find".equals(category)) {
				iters.add(getFindResults(term));
			}
		}
		Iterator<String> iter = IteratorHelper.combineMultipleIterator(iters);
		return iter;
	}
	
	/**
	 * search review with an term and return a string iterator representing all the result entries
	 * @param term
	 * @return
	 */
	public Iterator<String> getSearchReviewResults(String term) {
		Iterator<String> displayIter = AmazonSearchUI.showSearchResults(term, search(term, reviewDatabase));
		return displayIter;
	}
	
	/**
	 * find with an asin and return a string iterator representing all the result entries
	 * @param asin
	 * @return
	 */
	public Iterator<String> getFindResults(String asin) {
		Iterator<String> displayIter = AmazonSearchUI.showFindResults(asin, find(asin,qaDatabase), find(asin,reviewDatabase));
		return displayIter;
	}

	/**
	 * in a review or QA inverted index database, find all the document associated with a certain asin
	 * @param asin the product asin
	 * @param database inverted index database
	 * @return
	 */
	public <T extends AmazonDocument> List<T> find(String asin, InvertedIndex<T> database) {
		return database.findDocumentsByAsin(asin);
	}

	/**
	 * search in a review or QA inverted index database the documents associated with the exact term, case insensitively.
	 * @param term the exact term for searching
	 * @param database inverted index database
	 * @return associated documents and its frequencies
	 */
	public <T extends AmazonDocument> ListOfDocsWithFreqs<T> search(String term, InvertedIndex<T> database) {
		return database.getSortedListOfDocsWithFreqsByTerm(term);
	}

	/**
	 * search the partial matches of term in the inverted index database
	 * @param term the term used for partial search
	 * @param database inverted index database
	 * @return token-associated documents map
	 */
	public <T extends AmazonDocument> Map<String, ListOfDocsWithFreqs<T>> partialsearch(String term, InvertedIndex<T> database) {
		Map<String, ListOfDocsWithFreqs<T>> searchResult = new LinkedHashMap<>(); 
		term = term.toLowerCase();
		List<String> tokens = StringHelper.partialMatchTokens(term, database.getAllTokens());
		for (String token : tokens) {
			searchResult.put(token, search(token,database));
		}
		return searchResult;
	}

	public InvertedIndex<QADocument> getQaDatabase() {
		return qaDatabase;
	}

	public InvertedIndex<ReviewDocument> getReviewDatabase() {
		return reviewDatabase;
	}

	public void setQaDatabase(InvertedIndex<QADocument> qaDatabase) {
		this.qaDatabase = qaDatabase;
	}

	public void setReviewDatabase(InvertedIndex<ReviewDocument> reviewDatabase) {
		this.reviewDatabase = reviewDatabase;
	}
}
