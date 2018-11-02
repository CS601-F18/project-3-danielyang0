package cs601.project3.amazonSearch.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * the database for a bunch of certain kind of document, with its inverted index
 * contains an asin-document map and an inverted index map
 * provide ways to "digest" documents: incorporate documents to database 
 * and update the inverted index as well.
 * @author yangzun
 *
 * @param <T>
 */
public class InvertedIndex<T extends AmazonDocument> {
	//asin-document map in the InvertedIndex object
	private Map<String, ArrayList<T>> asinDocumentsMap;
	//key: token ; value: document-frequency map
//	private Map<String, Map<T, Integer>> invertedIndexMap;
	private Map<String, ListOfDocsWithFreqs<T>> invertedIndexMap;

	public InvertedIndex() {
		super();
		asinDocumentsMap = new HashMap<>();
		invertedIndexMap = new HashMap<>();
	}

	/**
	 * find a list of documents by asin
	 * @param asin the asin indicates a product
	 * @return
	 */
	public List<T> findDocumentsByAsin(String asin) {
		ArrayList<T> docs = this.asinDocumentsMap.get(asin.toLowerCase());
		if(docs == null){
			return new ArrayList<T>();
		}
		return docs;
	}

	/**
	 * get a list of comparableDocs associated with an exact term
	 * @param term exact term used to search in the invertedIndexMap
	 * @return the comparableDocs associated with the term in invertedIndexMap
	 */
	public ListOfDocsWithFreqs<T> getSortedListOfDocsWithFreqsByTerm(String term) {
		ListOfDocsWithFreqs<T> listOfDocsWithFreqs = this.invertedIndexMap.get(term.toLowerCase());
		if(listOfDocsWithFreqs == null) {
			return null;
		}
		listOfDocsWithFreqs.sort();
		return listOfDocsWithFreqs;
	}

	/**
	 * get all the tokens in the invertedIndexMap
	 * @return all the keys in invertedIndexMap
	 */
	public Set<String> getAllTokens() {
		return this.invertedIndexMap.keySet();
	}

	/**
	 * batch digests documents
	 * @param documents
	 */
	public void batchDigestDocuments(List<T> documents) {
		for (T doc : documents) {
			this.digestDocument(doc);
		}
	}

	/**
	 * analyze a new document, update the asinDocumentsMap and invertedIndexMap in this object
	 * @param doc
	 */
	public void digestDocument(T doc) {
		addNewToDocumentsMap(doc);//add a new document into the database
		Map<String, Integer> tokenFrequencyMap = doc.getTokenFrequencyMap();
		for(String token: tokenFrequencyMap.keySet()){
			Integer freq = tokenFrequencyMap.get(token);
			updateInvertedIndexMap(token, doc, freq);
		}
	}

	/**
	 * add a new document to asinDocumentsMap
	 * @param doc
	 */
	private void addNewToDocumentsMap(T doc) {
		String asin = doc.getAsin().toLowerCase();
		List<T> relatedDocs = asinDocumentsMap.get(asin);
		if(relatedDocs == null) {
			asinDocumentsMap.put(asin, new ArrayList<T>());
			relatedDocs = asinDocumentsMap.get(asin);
		}
		relatedDocs.add(doc);
	}

	/**
	 * update inverted index by associating a document to a token
	 * @param token
	 * @param doc
	 */
	private void updateInvertedIndexMap(String token, T doc, Integer freq) {
		ListOfDocsWithFreqs<T> listOfDocsWithFreqs = invertedIndexMap.get(token);
		if(listOfDocsWithFreqs == null) {
			invertedIndexMap.put(token, new ListOfDocsWithFreqs<T>());
			listOfDocsWithFreqs = invertedIndexMap.get(token);
		}
		listOfDocsWithFreqs.add(doc, freq);
	}
}

