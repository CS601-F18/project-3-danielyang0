package cs601.project3.handler;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cs601.project3.AmazonSearch;
import cs601.project3.HTTPServer;
import cs601.project3.HttpConnection;
import cs601.project3.chat.ChatClient;
import cs601.project3.server.HttpRequest;
import cs601.project3.server.HttpResponse;
import cs601.project3.tools.IteratorHelper;
import cs601.project3.tools.PropertyReader;
import cs601.project3.tools.TemplateHelper;

/**
 * a handler for handle finding documents with search terms in amazon inverted index database
 * @author yangzun
 *
 */
public class ReviewSearchHandler implements Handler{

	private static Logger logger = Logger.getLogger(ReviewSearchHandler.class);
	private static final int ROWS_PER_PAGE;
	static {
		PropertyConfigurator.configure("./config/log4j.properties");
		PropertyReader reader = new PropertyReader("./config","project3.properties");
		ROWS_PER_PAGE = reader.readIntValue("rowsPerPage", 30);
	}
	
	private String webRoot;
	private StaticFileHandler staticFileHandler;
	
	public ReviewSearchHandler(String webRoot) {
		super();
		this.webRoot = webRoot;
		staticFileHandler = new StaticFileHandler(webRoot);
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
		StaticFileHandler.turnToStaticFile200OK(req, resp, "/reviewSearchPage.html", staticFileHandler);
		
	}
	
	public void doPost(HttpRequest req, HttpResponse resp) {
		String terms = req.getPostData().get("query");
		if(terms == null) {
			StaticFileHandler.turnTo400Page(resp, staticFileHandler);
			return;
		}
		int page = 1;
		String pageStr = req.getPostData().get("page");
		if (pageStr != null) {
			try {
				page = Integer.valueOf(pageStr.trim());
			}catch(NumberFormatException nfe) {
				logger.info("page number cannot be interpreated as an integer, will use 1");
			}
		}
		try {
			terms = URLDecoder.decode(terms, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.info("<<<<decode string reviewsearch terms: " + terms + "error: ");
		}
		AmazonSearch as = AmazonSearch.getInstance();
		if(as == null) {
			return;
		}
		//get combined raw data
		Iterator<String> iter = as.getCombinedAmazonResultsForMultipleTerms(terms,"review");
		//get the right contents for the page specified
		List<String[]> searchedresultsdata = IteratorHelper.toList(iter, ROWS_PER_PAGE, page);
		//generate parameters for template
		List<String> params = TemplateHelper.genrateParamsForSearchTemplate(searchedresultsdata, terms);
		StaticFileHandler sfHandler = new StaticFileHandler(webRoot);
		sfHandler.setParams(params);
		StaticFileHandler.turnToStaticFile200OK(req, resp, "/reviewSearchResults.html", sfHandler);
	}
	


}
