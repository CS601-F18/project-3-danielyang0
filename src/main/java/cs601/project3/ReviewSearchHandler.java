package cs601.project3;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cs601.project3.server.HttpRequest;
import cs601.project3.server.HttpResponse;

public class ReviewSearchHandler implements Handler{

	private static Logger logger = Logger.getLogger(ReviewSearchHandler.class);
	static {
		PropertyConfigurator.configure("./config/log4j.properties");
	}
	
	@Override
	public void handle(HttpRequest req, HttpResponse resp) {
		logger.debug("handled by ReviewSearchHandler");
		if("GET".equals(req.getMethod())) {
			doGet(req,resp);
		}else if("POST".equals(req.getMethod())) {
			doPost(req,resp);
		}
	}
	
	public void doGet(HttpRequest req, HttpResponse resp) {
		StaticFileHandler staticFileHandler = resp.getStaticFileHandler();
		req.setMethod("GET");
		req.setPath("/reviewSearchPage.html");
		resp.setResponseHeader("HTTP/1.0 200 OK\nConnection: close\n\r\n");
		staticFileHandler.handle(req, resp);
	}
	
	public void doPost(HttpRequest req, HttpResponse resp) {
		StaticFileHandler staticFileHandler = resp.getStaticFileHandler();
		String term = req.getPostData().get("query");
		AmazonSearch as = AmazonSearch.getInstance();
		if(as == null) {
			return;
		}
		staticFileHandler.setParam(as.getSearchResults(term).replace("\n", "<br>"));
		req.setMethod("GET");
		req.setPath("/reviewSearchResults.html");
		resp.setResponseHeader("HTTP/1.0 200 OK\nConnection: close\n\r\n");
		staticFileHandler.handle(req, resp);
	}

}
