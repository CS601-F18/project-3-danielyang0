package cs601.project3;

import java.io.OutputStream;

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
