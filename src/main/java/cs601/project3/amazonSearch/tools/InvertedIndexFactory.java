package cs601.project3.amazonSearch.tools;

import java.io.IOException;
import java.util.List;

import cs601.project3.amazonSearch.model.AmazonDocument;
import cs601.project3.amazonSearch.model.InvertedIndex;

public class InvertedIndexFactory {
	/**
	 * create a inverted index database 
	 * and digest a bunch of certain type of documents read from a json file
	 * @param fileName the json file name where documents are from
	 * @param clazz the class object of the document type
	 * @return
	 * @throws IOException 
	 */
	public static <T extends AmazonDocument> InvertedIndex<T> buildInvertedIndex(String fileName, Class<T> clazz) throws IOException {
		List<T> docs = AmazonFileParser.parseJsonFileToObjects(fileName, clazz);
		InvertedIndex<T> invertedIndexDatabase = new InvertedIndex<T>();
		invertedIndexDatabase.batchDigestDocuments(docs);
		return invertedIndexDatabase;
	}
}
