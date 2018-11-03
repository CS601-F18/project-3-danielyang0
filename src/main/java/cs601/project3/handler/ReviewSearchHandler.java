package cs601.project3.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cs601.project3.AmazonSearch;
import cs601.project3.StaticFileHandler;
import cs601.project3.chat.ChatClient;
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
		String term = req.getPostData().get("query");
		
//		ChatClient chatClient = new ChatClient();
//		System.out.println(term);
//		try {
//			chatClient.sendMessage(term);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		AmazonSearch as = AmazonSearch.getInstance();
		if(as == null) {
			return;
		}
		
//		staticFileHandler.setParam(as.getSearchResults(term).replace("\n", "<br>"));
		List<String[]> searchedresultsdata = as.getSearchResults(term);
		String[] strings = searchedresultsdata.remove(0);
		String title = "";
		if(strings.length > 0) {
			title = strings[0];
		}
		List<String> params = new ArrayList<>();
		params.add(title);
		params.add(generateTableTr(searchedresultsdata));
		staticFileHandler.setParams(params);
		req.setMethod("GET");
		req.setPath("/reviewSearchResults.html");
		resp.setResponseHeader("HTTP/1.0 200 OK\nConnection: close\n\r\n");
		staticFileHandler.handle(req, resp);
	}

}