package cs601.project3.amazonSearch.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


/**
 * a list of documents with the frequencies a certain token shows up in it
 * @author yangzun
 *
 * @param <T>
 */
public class ListOfDocsWithFreqs<T extends AmazonDocument>{
	private List<DocWithFreq> docs;
	private boolean isSorted;
	/**
	 * a document and the frequency some token shows up in it
	 * @author yangzun
	 *
	 */
	public class DocWithFreq implements Comparable<DocWithFreq>{
		private T doc;
		private Integer freq;
		DocWithFreq(T doc, Integer freq) {
			super();
			this.doc = doc;
			this.freq = freq;
		}
		@Override
		public int compareTo(ListOfDocsWithFreqs<T>.DocWithFreq o) {
			return -1 * this.freq.compareTo(o.freq);
		}
		public T getDoc() {
			return doc;
		}
		public void setDoc(T doc) {
			this.doc = doc;
		}
		public Integer getFreq() {
			return freq;
		}
		public void setFreq(Integer freq) {
			this.freq = freq;
		}
	}

	public int size() {
		return docs.size();
	}
	public ListOfDocsWithFreqs() {
		super();
		isSorted = false;
		docs = new ArrayList<>();
	}

	public Iterator<DocWithFreq> getIterator() {
		return docs.iterator();
	}

	/**
	 * add a new doc and its token frequency into the list
	 * @param doc
	 * @param freq
	 */
	public void add(T doc, Integer freq) {
		docs.add(new DocWithFreq(doc, freq));
	}

	/**
	 * sort only once if invoked
	 */
	public void sort() {
		if(!isSorted) {
			Collections.sort(docs);
			isSorted = true;
		}
	}
}