package cs601.project3.handler;

import java.awt.geom.Line2D;
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
import cs601.project3.SearchApplication;
import cs601.project3.StaticFileHandler;
import cs601.project3.server.HttpRequest;
import cs601.project3.server.HttpResponse;
import cs601.project3.tools.IteratorHelper;
import cs601.project3.tools.PropertyReader;

public class FindHandler implements Handler{
	private static Logger logger = Logger.getLogger(FindHandler.class);
	private static final int ROWS_PER_PAGE;
	static {
		PropertyConfigurator.configure("./config/log4j.properties");
		PropertyReader reader = new PropertyReader("./config","project3.properties");
		ROWS_PER_PAGE = reader.readIntValue("rowsPerPage", 30);
	}
	private StaticFileHandler staticFileHandler;
	private String webRoot;
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
//		req.setMethod("GET");
//		req.setPath("/findSearchPage.html");
//		resp.setResponseHeader("HTTP/1.0 200 OK\nConnection: close\n\r\n");
//		staticFileHandler.handle(req, resp);
		HttpConnection.turnToStaticFile200OK(req, resp, "/findSearchPage.html", staticFileHandler);
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
			if(!openedTable) {
				sb.append("<table>\n");
				openedTable = true;
			}
			sb.append("<tr>\n");
			for (String colunmData : lineData) {
				colunmData = StringEscapeUtils.escapeHtml4(StringEscapeUtils.unescapeHtml4(colunmData));
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
		String asins = req.getPostData().get("asin");
		if(asins == null) {
			HttpConnection.turnTo400Page(resp, staticFileHandler);
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
			asins = URLDecoder.decode(asins, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.info("<<<<decode string asin: " + asins + "error: ");
		}
		
		AmazonSearch as = AmazonSearch.getInstance();
		
		if(as == null) {
			return;
		}
		
		String[] splited = asins.split("\\s+");
		
		String asinsDecoded = "";
//		List<String[]> foundresultsdata = new ArrayList<>();
		List<Iterator<String>> iters = new ArrayList<>();
		for (String asin : splited) {
			asinsDecoded += asin+" ";
			Iterator<String> res = as.getFindResults(asin);
//			String[] strings = res.remove(0);
//			if(strings.length > 0) {
//				title += strings[0]+"<br/>";
//			}
			iters.add(res);
//			for (String[] s : res) {
//				foundresultsdata.add(s);
//			}
		}
		Iterator<String> iter = IteratorHelper.combineMultipleIterator(iters);
		List<String[]> foundresultsdata = IteratorHelper.toList(iter, ROWS_PER_PAGE, page);
		String[] pageInfo = foundresultsdata.remove(0);
		//TO DO
		List<String> params = new ArrayList<>();
		params.add(asinsDecoded);
		String buttons = generatePrevNextButton(pageInfo, asinsDecoded);
		params.add(buttons);
		params.add(generateTableTr(foundresultsdata));
//		HTTPServer.staticFileHandler.setParams(params);
		StaticFileHandler sfHandler = new StaticFileHandler(webRoot);
		sfHandler.setParams(params);
		HttpConnection.turnToStaticFile200OK(req, resp, "/findSearchResults.html", sfHandler);
//		req.setMethod("GET");
//		req.setPath("/findSearchResults.html");
//		resp.setResponseHeader("HTTP/1.0 200 OK\nConnection: close\n\r\n");
//		HTTPServer.staticFileHandler.handle(req, resp);
	}
	
	private String generatePrevNextButton(String[] pageInfo, String searchTerms) {
		if(pageInfo.length != 3) return "";
		StringBuffer sb = new StringBuffer();
		int page = 1;
		try {
			page = Integer.valueOf(pageInfo[1]);
		}catch(NumberFormatException nfe) {
		}
		if(pageInfo[0] != null) {
			sb.append("<button type=\"button\" onclick=\"jsPost('"+searchTerms+"','"+ (page -1) +"');\">prevPage</button>\n");
		}
		sb.append("<span>current_Page:"+pageInfo[1]+"</span>\n");
		if(pageInfo[2] != null) {
			sb.append("<button type=\"button\" onclick=\"jsPost('"+searchTerms+"','"+(page+1)+"');\">nextPage</button>\n");
		}
		return sb.toString();
	}
	
}
