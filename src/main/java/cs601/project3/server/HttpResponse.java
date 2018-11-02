package cs601.project3.server;

import java.io.OutputStream;

import cs601.project3.StaticFileHandler;

public class HttpResponse {

	private StaticFileHandler staticFileHandler;
	private String responseHeader;
	
	private OutputStream outputStream;

	public HttpResponse(OutputStream outputStream) {
		super();
		this.outputStream = outputStream;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public StaticFileHandler getStaticFileHandler() {
		return staticFileHandler;
	}

	public void setStaticFileHandler(StaticFileHandler staticFileHandler) {
		this.staticFileHandler = staticFileHandler;
	}

	public String getResponseHeader() {
		return responseHeader;
	}

	public void setResponseHeader(String responseHeader) {
		this.responseHeader = responseHeader;
	}
	
	
}
