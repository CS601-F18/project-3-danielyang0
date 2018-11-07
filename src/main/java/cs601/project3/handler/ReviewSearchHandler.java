package cs601.project3.handler;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cs601.project3.AmazonSearch;
import cs601.project3.HTTPServer;
import cs601.project3.HttpConnection;
import cs601.project3.StaticFileHandler;
import cs601.project3.chat.ChatClient;
import cs601.project3.server.HttpRequest;
import cs601.project3.server.HttpResponse;

public class ReviewSearchHandler implements Handler{

	private static Logger logger = Logger.getLogger(ReviewSearchHandler.class);
	static {
		PropertyConfigurator.configure("./config/log4j.properties");
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
//		req.setMethod("GET");
//		req.setPath("/reviewSearchPage.html");
//		resp.setResponseHeader("HTTP/1.0 200 OK\nConnection: close\n\r\n");
//		staticFileHandler.handle(req, resp);
		HttpConnection.turnToStaticFile200OK(resp, "/reviewSearchPage.html", staticFileHandler);
		
	}
	
	private String generateTableTr(List<String[]> data) {
		//if <table> tag has existed
		boolean openedTable = false;
		StringBuffer sb = new StringBuffer();
		for (String[] lineData : data) {
			if(lineData.length == 1) {
				//this shoule be put into a <h> tag
				if(openedTable) {
					sb.append("</table>\n");
					openedTable = false;
				}
				sb.append("<h2>"+lineData[0]+"</h2><br/>");
				sb.append("<table>\n");
				openedTable = true;
				continue;
			}
			sb.append("<tr>\n");
			for (String colunmData : lineData) {
				sb.append("<td>"+colunmData+"</td>\n");
			}
			sb.append("</tr>\n");
		}
		if(openedTable) {
			sb.append("</table><br/>\n");
		}
		return sb.toString();
	}

	
	public void doPost(HttpRequest req, HttpResponse resp) {
		
//		String decodedKey = null;
//		String decodedValue = null;
//		try {
//			decodedKey = decodeUrl(splited[0]);
//			decodedValue = decodeUrl(splited[1]);
//		} catch (UnsupportedEncodingException e) {
//		}
//		
//		if(decodedKey == null) {
//			logger.warn("post data" + splited[0] + "decode url failed!");
//		}
//		if(decodedValue == null) {
//			logger.warn("post data" + splited[1] + "decode url failed!");
//		}
//		if(decodedKey == null || decodedValue == null) {
//			break;
//		}
//		logger.info("====post key and value: "+decodedKey + ": "+decodedValue);
		String terms = req.getPostData().get("query");
		if(terms == null) {
			HttpConnection.turnTo400Page(resp, staticFileHandler);
			return;
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
		
		String[] splitedTerms = terms.split("\\s+");
		String termsDecoded = "";
		List<String[]> searchedresultsdata = new ArrayList<>();
		for (String term : splitedTerms) {
			termsDecoded += term+" ";
			List<String[]> res = as.getSearchResults(term);
//			String[] strings = res.remove(0);
//			if(strings.length > 0) {
//				title += strings[0]+"<br/>";
//			}
			for (String[] s : res) {
				searchedresultsdata.add(s);
			}
		}
		
		List<String> params = new ArrayList<>();
		params.add(termsDecoded);
		params.add(generateTableTr(searchedresultsdata));
		StaticFileHandler sfHandler = new StaticFileHandler(webRoot);
		sfHandler.setParams(params);
		HttpConnection.turnToStaticFile200OK(resp, "/reviewSearchResults.html", sfHandler);
	}

}
