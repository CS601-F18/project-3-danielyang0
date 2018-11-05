package cs601.project3.amazonSearch.tools;

import java.io.IOException;

import cs601.project3.AmazonSearch;
import cs601.project3.amazonSearch.model.AmazonDocument;
import cs601.project3.amazonSearch.model.InvertedIndex;
import cs601.project3.amazonSearch.model.QADocument;
import cs601.project3.amazonSearch.model.ReviewDocument;

public class AmazonSearchFactory {
	
	/**
	 * initiate two inverted index databases
	 * @param qaFileName
	 * @param reviewFileName
	 * @return whether initiation is successful
	 */
	public static boolean initDatabases(AmazonSearch engine, String qaFileName, String reviewFileName) {
		InvertedIndex<QADocument> qaDatabase = initAmazonSearchDatabase(qaFileName, QADocument.class);
		if(qaDatabase == null) {
			return false;
		}
		engine.setQaDatabase(qaDatabase);
		InvertedIndex<ReviewDocument> reviewDatabase = initAmazonSearchDatabase(reviewFileName, ReviewDocument.class);
		if(reviewDatabase == null) {
			return false;
		}
		engine.setReviewDatabase(reviewDatabase);
		return true;
	}
	
	/**
	 * initiate review or question/answer inverted index database
	 */
	private static <T extends AmazonDocument> InvertedIndex<T> initAmazonSearchDatabase(String fileName, Class<T> clazz) {
		String title;
		if(QADocument.class == clazz) {
			title = "initiating question/answer inverted index database...";
		}else if(ReviewDocument.class == clazz) {
			title = "initiating review inverted index database...";
		}else{
			title = "";
		}
		try {
			System.out.println(title);
			return InvertedIndexFactory.buildInvertedIndex(fileName, clazz);
		} catch (IOException e) {
			System.out.println(fileName + " does not exist! Please check the file name. ");
			return null;
		}
	}
}
