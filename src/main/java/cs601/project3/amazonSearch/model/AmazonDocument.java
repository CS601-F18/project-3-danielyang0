package cs601.project3.amazonSearch.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cs601.project3.amazonSearch.tools.StringHelper;


/**
 * An amazon document refers to one review record or question/answer record, 
 * which is retrievable and has a specific asin id(product id) related to it.
 * @author yangzun
 *
 */
public abstract class AmazonDocument extends RetrievableDocument {
	
	//the asin id(product id) related to this document
	//this may NOT be unique for each document
	protected String asin;
	
	/**
	 * get all tokens from the original texts in the document
	 */
	@Override
	public List<String> getListOfTokens() {
		List<String> textToBeRetrived = getOriginalTexts();
		List<String> listOfTokens = new ArrayList<>();
		for (String text : textToBeRetrived) {
			listOfTokens.addAll(Arrays.asList(StringHelper.splitTokens(text)));
		}
		return listOfTokens;
	}
	
	/**
	 * specify a list of texts that belongs to the document 
	 * and from where the tokens come from
	 * @return the list of texts
	 */
	protected abstract List<String> getOriginalTexts();
	
	/**
	 * the document summary which will be shown to user when requested
	 * @return the list of strings which will be presented to the user
	 */
	public abstract List<String> summary();
	
	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}
}
