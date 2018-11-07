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
import cs601.project3.server.HttpRequest;
import cs601.project3.server.HttpResponse;
import cs601.project3.tools.IteratorHelper;
import cs601.project3.tools.PropertyReader;
import cs601.project3.tools.TemplateHelper;
/**
 * a handler for handle finding documents with product asins in amazon inverted index database
 * @author yangzun
 *
 */
public class FindHandler implements Handler{
	private static Logger logger = Logger.getLogger(FindHandler.class);
	private static final int ROWS_PER_PAGE;
	private StaticFileHandler staticFileHandler;
	private String webRoot;
	static {
		PropertyConfigurator.configure("./config/log4j.properties");
		PropertyReader reader = new PropertyReader("./config","project3.properties");
		ROWS_PER_PAGE = reader.readIntValue("rowsPerPage", 30);
	}

	public FindHandler(String webRoot) {
		super();
		this.webRoot = webRoot;
		staticFileHandler = new StaticFileHandler(webRoot);
	}
	
	@Override
	public void handle(HttpRequest req, HttpResponse resp) {
		logger.debug("handled by FindHandler");
		if("GET".equals(req.getMethod())) {
			doGet(req,resp);
		}else if("POST".equals(req.getMethod())) {
			doPost(req,resp);
		}
	}
	
	public void doGet(HttpRequest req, HttpResponse resp) {
		StaticFileHandler.turnToStaticFile200OK(req, resp, "/findSearchPage.html", staticFileHandler);
	}
	
	public void doPost(HttpRequest req, HttpResponse resp) {
		String asins = req.getPostData().get("asin");
		if(asins == null) {
			StaticFileHandler.turnTo400Page(resp, staticFileHandler);
			return;
		}
		//try to get page parameter for post body, if there isn't, set dafault value 1
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
			asins = URLDecoder.decode(asins, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.info("<<<<decode string asin: " + asins + "error: ");
		}
		AmazonSearch as = AmazonSearch.getInstance();
		if(as == null) {
			return;
		}
		//get combined raw data
		Iterator<String> iter = as.getCombinedAmazonResultsForMultipleTerms(asins,"find");
		//get the right contents for the page specified
		List<String[]> foundresultsdata = IteratorHelper.toList(iter, ROWS_PER_PAGE, page);
		//generate parameters for template
		List<String> params = TemplateHelper.genrateParamsForSearchTemplate(foundresultsdata, asins);
		StaticFileHandler sfHandler = new StaticFileHandler(webRoot);
		sfHandler.setParams(params);
		StaticFileHandler.turnToStaticFile200OK(req, resp, "/findSearchResults.html", sfHandler);
	}
	
}
