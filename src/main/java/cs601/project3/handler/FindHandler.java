package cs601.project3.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cs601.project3.AmazonSearch;
import cs601.project3.StaticFileHandler;
import cs601.project3.server.HttpRequest;
import cs601.project3.server.HttpResponse;

public class FindHandler implements Handler{
	private static Logger logger = Logger.getLogger(FindHandler.class);
	static {
		PropertyConfigurator.configure("./config/log4j.properties");
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
		StaticFileHandler staticFileHandler = resp.getStaticFileHandler();
		req.setMethod("GET");
		req.setPath("/findSearchPage.html");
		resp.setResponseHeader("HTTP/1.0 200 OK\nConnection: close\n\r\n");
		staticFileHandler.handle(req, resp);
	}
	
	private String generateTableTr(List<String[]> data) {
		StringBuffer sb = new StringBuffer();
		for (String[] tr : data) {
			sb.append("<tr>\n");
			for (String td : tr) {
				sb.append("<td>"+td+"</td>\n");
			}
			sb.append("</tr>\n");
		}
		return sb.toString();
	}
	
	public void doPost(HttpRequest req, HttpResponse resp) {
		StaticFileHandler staticFileHandler = resp.getStaticFileHandler();
		String asins = req.getPostData().get("asin");
		
		AmazonSearch as = AmazonSearch.getInstance();
		
		if(as == null) {
			return;
		}
		
		String[] splited = asins.split("\\s+");
		
		String title = "";
		List<String[]> foundresultsdata = new ArrayList<>();
		for (String asin : splited) {
			List<String[]> res = as.getFindResults(asin);
			String[] strings = res.remove(0);
			if(strings.length > 0) {
				title += strings[0]+"<br>";
			}
			for (String[] s : res) {
				foundresultsdata.add(s);
			}
		}

		List<String> params = new ArrayList<>();
		params.add(title);
		params.add(generateTableTr(foundresultsdata));
		staticFileHandler.setParams(params);
		req.setMethod("GET");
		req.setPath("/findSearchResults.html");
		resp.setResponseHeader("HTTP/1.0 200 OK\nConnection: close\n\r\n");
		staticFileHandler.handle(req, resp);
	}
}
