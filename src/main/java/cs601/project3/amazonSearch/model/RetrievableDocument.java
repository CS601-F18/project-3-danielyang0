package cs601.project3.amazonSearch.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * documents able to be retrieved using tokens
 * @author yangzun
 *
 */
public abstract class RetrievableDocument {
	/**
	 * the token-frequency pairs which can be used for retrieving
	 * @return
	 */
	public Map<String, Integer> getTokenFrequencyMap() {
		return calcTokenFrequency(getListOfTokens());
	}

	/**
	 * get all the tokens associated with this document
	 * @return
	 */
	public abstract List<String> getListOfTokens();

	/**
	 * from a list of tokens, calculate the token-frequency pairs
	 * @param tokens the tokens from original text 
	 * @return token-frequency pair
	 */
	private static Map<String, Integer> calcTokenFrequency(List<String> tokens) {
		Map<String, Integer> tokenFrequencyMap = new HashMap<>();
		for (String token : tokens) {
			Integer tokenCount = tokenFrequencyMap.get(token);
			if (tokenCount == null) {
				tokenFrequencyMap.put(token, 1);
			}else{
				tokenFrequencyMap.put(token, tokenCount + 1);
			}
		}
		return tokenFrequencyMap;
	}
}
