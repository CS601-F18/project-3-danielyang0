package cs601.project3.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cs601.project3.HTTPServer;

public class HttpRequestBuilder {
	
	private static Logger logger = Logger.getLogger(HTTPServer.class);
	static {
		PropertyConfigurator.configure("./config/log4j.properties");
	}
	
	private HttpRequest req;
	
	public HttpRequestBuilder() {
		req = new HttpRequest();
	}
	
	/**
	 * parse http request from bufferedReader of the socket, set information into HttpRequst object
	 * @param br
	 * @return HttpRequst object
	 */
	public HttpRequest parseRequest(BufferedReader br){
		String requestLines;
		try {
			requestLines = br.readLine();
		} catch (IOException e1) {
			logger.info(">>>>read requestLines exception");
			return req;
		}
		logger.info("======requestLines: =======\n"+ requestLines);
		if(requestLines == null){
			return req;
		}
		req.setRequestLines(requestLines);
		
		String line;
		StringBuffer headerLines = new StringBuffer();
		try {
			line = br.readLine();
			while(line != null && !line.trim().isEmpty()) {
				headerLines.append(line).append("\n");
				line = br.readLine();
			}
		} catch (IOException e1) {
			logger.info(">>>>read headerLines exception");
			logger.info(">>>>Have read headerLines: \n" + headerLines.toString());
			return req;
		}
		logger.debug("======HeaderLines:======== \n" + headerLines);
		if(headerLines.toString().length() == 0) {
			return req;
		}
		req.setHeaderLines(headerLines.toString());
		if(!req.isRequestAndHeaderLinesValid()) {
			return req;
		}
		if("POST".equals(req.getMethod())) {
			byte b = 0;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			try {
				for (int i = 0; i < req.getContentLength(); i++) {
					b = (byte) br.read();
					bout.write(b);
				}
			} catch (IOException e) {
				req.setContentLengthConsistent(false);
				logger.info("read request body exception");
				return req;
			}
			req.setContentLengthConsistent(true);
			String postData = new String(bout.toByteArray());
			logger.debug("=======raw post data: =======\n"+ postData);
			req.setPostData(postData);
		}
		return req;
	}
}
