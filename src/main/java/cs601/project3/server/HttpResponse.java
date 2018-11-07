package cs601.project3.server;

import java.io.OutputStream;

import cs601.project3.HTTPServer;
import cs601.project3.StaticFileHandler;

public class HttpResponse {

	private String responseHeader;
	
	private OutputStream outputStream;

	public HttpResponse(OutputStream outputStream) {
		super();
		this.outputStream = outputStream;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public String getResponseHeader() {
		return responseHeader;
	}

	public void setResponseHeader(String responseHeader) {
		this.responseHeader = responseHeader;
	}
	
	
}
