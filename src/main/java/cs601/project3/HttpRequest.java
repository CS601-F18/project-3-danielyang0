package cs601.project3;

public class HttpRequest {
	
	private String method;
	private String protocol;
	private String pathInfo;
	

	public HttpRequest(String method, String pathInfo, String protocol) {
		super();
		this.method = method;
		this.pathInfo = pathInfo;
		this.protocol = protocol;
	}

	/**
	 * Returns the name of the HTTP method 
	 * with which this request was made, for example, GET, POST, or PUT.
	 */
	public String getMethod() {
		return this.method;
	}
	
	/**
	 * Returns any extra path information associated with the URL the client sent 
	 * when it made this request. 
	 * The extra path information follows the servlet path but precedes the query 
	 * string and will start with a "/" character.
	 * @return
	 */
	public String getPathInfo() {
		return pathInfo;
	}
	


	public String getProtocol() {
		return protocol;
	}



	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}



	public void setMethod(String method) {
		this.method = method;
	}



	public void setPathInfo(String pathInfo) {
		this.pathInfo = pathInfo;
	}
}
