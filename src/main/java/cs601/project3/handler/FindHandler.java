package cs601.project3.handler;

import java.awt.geom.Line2D;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cs601.project3.AmazonSearch;
import cs601.project3.HTTPServer;
import cs601.project3.HttpConnection;
import cs601.project3.SearchApplication;
import cs601.project3.StaticFileHandler;
import cs601.project3.server.HttpRequest;
import cs601.project3.server.HttpResponse;

public class FindHandler implements Handler{
	private static Logger logger = Logger.getLogger(FindHandler.class);
	static {
		PropertyConfigurator.configure("./config/log4j.properties");
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
		HttpConnection.turnToStaticFile200OK(resp, "/findSearchPage.html", staticFileHandler);
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
		String asins = req.getPostData().get("asin");
		if(asins == null) {
			HttpConnection.turnTo400Page(resp, staticFileHandler);
			return;
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
		List<String[]> foundresultsdata = new ArrayList<>();
		for (String asin : splited) {
			asinsDecoded += asin+" ";
			List<String[]> res = as.getFindResults(asin);
//			String[] strings = res.remove(0);
//			if(strings.length > 0) {
//				title += strings[0]+"<br/>";
//			}
			for (String[] s : res) {
				foundresultsdata.add(s);
			}
		}
		List<String> params = new ArrayList<>();
		params.add(asinsDecoded);
		params.add(generateTableTr(foundresultsdata));
//		HTTPServer.staticFileHandler.setParams(params);
		StaticFileHandler sfHandler = new StaticFileHandler(webRoot);
		sfHandler.setParams(params);
		HttpConnection.turnToStaticFile200OK(resp, "/findSearchResults.html", sfHandler);
//		req.setMethod("GET");
//		req.setPath("/findSearchResults.html");
//		resp.setResponseHeader("HTTP/1.0 200 OK\nConnection: close\n\r\n");
//		HTTPServer.staticFileHandler.handle(req, resp);
	}
}
