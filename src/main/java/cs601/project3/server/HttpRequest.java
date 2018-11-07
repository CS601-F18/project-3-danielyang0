package cs601.project3.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * http request class for storing request information, check validity
 * @author yangzun
 *
 */
public class HttpRequest {
	
	private String requestLines = null;
	private String headerLines = null;
	private String path;
	private Map<String,String> paramsMap = null;
	private String method;
	private String protocol;
	private Map<String,String> headerLinesMap = null;
	private boolean contentLengthConsistent;
	private Boolean requestAndHeaderLinesValid;
	private Integer contentLength;
	private Map<String, String> postData;
	private static List<String> supportedHttpMethod;
	
	private static Logger logger = Logger.getLogger(HttpRequest.class);
	static {
		PropertyConfigurator.configure("./config/log4j.properties");
		supportedHttpMethod = new ArrayList<>();
		supportedHttpMethod.add("GET");
		supportedHttpMethod.add("POST");
	}
	
	/**
	 * check params like "a=1&b=2&c=3" are well formated
	 * @param params
	 * @return
	 */
	private boolean paramsCheckAndSet(String params) {
		paramsMap = splitKeyValues(params,"&","=",false,true, true);
		boolean res = paramsMap != null && !paramsMap.isEmpty();
		if(!res) {
			logger.info("paramsCheck not pass");
		}
		return res;
	}
	
	/**
	 * check if path formate is invalid
	 * @param path
	 * @return
	 */
	private boolean pathCheckAndSet(String path) {
		if(!path.startsWith("/")) {
			logger.info("path should start with /");
			return false;
		}
		setPath(path);
		return true;
	}
	
	/**
	 * check if protocol correct
	 * @param protocol
	 * @return
	 */
	private boolean protocolCheckAndSet(String protocol) {
		String[] splited = protocol.split("/");
		if(splited.length != 2) {
			logger.info("protocol format wrong:" + protocol);
			return false;
		}
		try{
			Double version = Double.valueOf(splited[1]);
		}catch(NumberFormatException nfe) {
			logger.info("<<<protocol version should be an integer");
			return false;
		}
		if(!"HTTP".equals(splited[0])) {
			logger.info("<<<protocol only support HTTP");
			return false;
		}
		setProtocol(protocol);
		return true;
	}
	
	/**
	 * split headerLines and set into map
	 * @return
	 */
	private boolean headerLinesCheckAndSet() {
		headerLinesMap = splitKeyValues(headerLines, "\n", ":", true, true, false);
		return headerLinesMap != null && !headerLinesMap.isEmpty();
	}
	
	/**
	 * an overall method for checking if the requestLine is valid
	 * @return
	 */
	private boolean isRequestLineValid() {
		if(requestLines == null) {
			logger.info("<<<requestLine is null");
			return false;
		}
		String[] requestLineParts = requestLines.split("\\s+");
		if(requestLineParts.length != 3) {
			logger.info("<<<requestLine is not 3 parts");
			return false;
		}
		setMethod(requestLineParts[0]);
		String pathAndParams = requestLineParts[1];
		if(pathAndParams.contains("?")) {
			String[] splited = pathAndParams.split("\\?");
			if(splited.length != 2 || !pathCheckAndSet(splited[0]) || !paramsCheckAndSet(splited[1])) {
				logger.info("url parmas format not correct: "+ pathAndParams);
				return false;
			}
		}else{
			if(!pathCheckAndSet(pathAndParams)) {
				return false;
			}
		}
		if(!protocolCheckAndSet(requestLineParts[2])) {
			return false;
		}
		return true;
	}

	/**
	 * an overrall method for checking if this request is valid or not
	 * @return
	 */
	public boolean isValid() {
		if(!isRequestAndHeaderLinesValid()) {
			return false;
		}
		//check the consistency of post body if method is "POST"
		if("POST".equals(getMethod())) {
			return isContentLengthConsistent() && postData != null;
		}
		return true;
		
	}
	
	/**
	 * check if request and headerlines are valid
	 * @return
	 */
	public boolean isRequestAndHeaderLinesValid() {
		if(requestAndHeaderLinesValid != null) return requestAndHeaderLinesValid;
		if(!isRequestLineValid() || !headerLinesCheckAndSet()) {
			return false;
		}
		if("POST".equals(getMethod())) {
			if(!headerLinesMap.containsKey("CONTENT-LENGTH")) {
				logger.info("lack content-length");
				return false;
			}
			try{
				contentLength = Integer.parseInt(headerLinesMap.get("CONTENT-LENGTH"));
			}catch(NumberFormatException e) {
				logger.info("content-length value is not an integer");
				return false;
			}
		}
		return true;
	}
	
	
	public HttpRequest() {
		super();
		postData = new HashMap<>();
	}

	/**
	 * split body and set postData map
	 * @param postDataStr
	 */
	public void setPostData(String postDataStr) {
		postData = splitKeyValues(postDataStr, "&", "=", false,false,true);
		logger.debug("=====set postData Map: \n" + postData);
	}
	
	/**
	 * a helper method for split key values groups
	 * can be used for url params, or requestLines, or post data strings
	 * @param kVGroupsString
	 * @param splitToken
	 * @param equalSign the equal sign. for example, "=" for string like "a=1&b=2"
	 * @param toUpperCase will strings be turned into uppercase before storing
	 * @param isTrim wiil trim?
	 * @param strict strict mode means a string like "a=1=1&b=2" will be considered invalid
	 * @return
	 */
	private HashMap<String, String> splitKeyValues(String kVGroupsString, String splitToken, String equalSign, boolean toUpperCase,boolean isTrim, boolean strict) {
		if(kVGroupsString == null) {
			return null;
		}
		if(kVGroupsString.equals("")) {
			return new HashMap<String, String>();
		}
		HashMap<String,String> res = new HashMap<>();
		String[] kvs = kVGroupsString.split(splitToken);
		
		for (String kv : kvs) {
			String[] kvSplited = kv.split(equalSign);
			if(strict) {
				if(kvSplited.length != 2) {
					return null;
				}
			}else {
				if(kvSplited.length < 2) {
					return null;
				}
			}
			String v = kv.substring(kvSplited[0].length()+equalSign.length());
			String keyToWrite = kvSplited[0];
			String valueToWrite = v;
			if(toUpperCase) {
				if(isTrim) {
					keyToWrite = kvSplited[0].trim().toUpperCase();
					valueToWrite = v.trim().toUpperCase();
				}else{
					keyToWrite = kvSplited[0].toUpperCase();
					valueToWrite = v.toUpperCase();
				}
			}else{
				if(isTrim) {
					keyToWrite = kvSplited[0].trim();
					valueToWrite = v.trim();
				}
			}
			if(res.containsKey(keyToWrite)) {
				if(!valueToWrite.equals(res.get(keyToWrite))) {
					logger.info("duplicated key value pairs: "+ keyToWrite +": "+ valueToWrite+" ? "+ res.get(keyToWrite));
					return null;
				}
			}
			res.put(keyToWrite, valueToWrite);
		}
		logger.info("splited key values into map:\n" + res);
		return res;
	}
	
	public boolean isMethodSupported() {
		boolean supported = supportedHttpMethod.contains(method);
		if(!supported) logger.info(getMethod() + " method not supported");
		return supported;
	}
	
	public Integer getContentLength() {
		return contentLength;
	}
	public void setContentLength(Integer contentLength) {
		this.contentLength = contentLength;
	}
	
	/**
	 * Returns the name of the HTTP method 
	 * with which this request was made, for example, GET, POST, or PUT.
	 */
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	/**
	 * Returns any extra path information associated with the URL the client sent 
	 * when it made this request. 
	 * The extra path information follows the servlet path but precedes the query 
	 * string and will start with a "/" character.
	 * @return
	 */
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public Map<String, String> getPostData() {
		return postData;
	}

	public String getRequestLines() {
		return requestLines;
	}


	public void setRequestLines(String requestLines) {
		this.requestLines = requestLines;
	}


	public String getHeaderLines() {
		return headerLines;
	}

	public void setHeaderLines(String headerLines) {
		this.headerLines = headerLines;
	}

	public boolean isContentLengthConsistent() {
		return contentLengthConsistent;
	}

	public void setContentLengthConsistent(boolean contentLengthConsistent) {
		this.contentLengthConsistent = contentLengthConsistent;
	}

	
}
