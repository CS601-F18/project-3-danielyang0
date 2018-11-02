package cs601.project3.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

	private boolean keepAlive;
	private Integer contentLength;
	private String method;
	private String path;
	private String protocol;
	private Map<String, String> postData;
	private static List<String> supportedHttpMethod = new ArrayList<>();
	
	
	public HttpRequest() {
		super();
		postData = new HashMap<>();
	}

	public void setPostData(String postDataStr) {
		String[] kvs = postDataStr.split("&");
		for (String kv : kvs) {
			String[] splited = kv.split("=");
			if(splited.length == 2) {
				postData.put(splited[0], splited[1]);
			}
		}
	}
	
	public boolean isMethodSupported() {
		return supportedHttpMethod.contains(method);
	}
	
	public boolean isEmpty() {
		return method == null;
	}
	
	public boolean getKeepAlive() {
		return keepAlive;
	}
	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
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

	public static List<String> getSupportedHttpMethod() {
		return supportedHttpMethod;
	}

	public static void setSupportedHttpMethod(List<String> supportedHttpMethod) {
		HttpRequest.supportedHttpMethod = supportedHttpMethod;
	}
	
	
}
