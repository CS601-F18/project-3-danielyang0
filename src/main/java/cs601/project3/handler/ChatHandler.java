package cs601.project3.handler;

import java.io.IOException;
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

/**
 * handler for slackbot chat
 * @author yangzun
 *
 */
public class ChatHandler implements Handler{
	private static Logger logger = Logger.getLogger(ChatHandler.class);
	static {
		PropertyConfigurator.configure("./config/log4j.properties");
	}
	
	private String webRoot;
	private StaticFileHandler staticFileHandler;
	
	public ChatHandler(String webRoot) {
		super();
		this.webRoot = webRoot;
		staticFileHandler = new StaticFileHandler(webRoot);
		List<String> params = new ArrayList<>();
		params.add("");
		staticFileHandler.setParams(params);
	}
	
	/**
	 * handle http request for chat
	 */
	@Override
	public void handle(HttpRequest req, HttpResponse resp) {
		logger.debug("handled by ChatHandler");
		if("GET".equals(req.getMethod())) {
			doGet(req,resp);
		}else if("POST".equals(req.getMethod())) {
			doPost(req,resp);
		}
	}
	
	/**
	 * handle http GET request for chat
	 * response with the static html page for user to input message 
	 * @param req
	 * @param resp
	 */
	public void doGet(HttpRequest req, HttpResponse resp) {
//		req.setMethod("GET");
//		req.setPath("/chat.html");
//		resp.setResponseHeader("HTTP/1.0 200 OK\nConnection: close\n\r\n");
//		staticFileHandler.handle(req, resp);
		HttpConnection.turnToStaticFile200OK(req, resp, "/chat.html", staticFileHandler);
	}
	
	/**
	 * handle http POST request for sending message
	 * send message to the slackbot client to send message to slack channel
	 * return the same page returned by GET method for user to input another message
	 * @param req
	 * @param resp
	 */
	public void doPost(HttpRequest req, HttpResponse resp) {
		String message = req.getPostData().get("message");
		if(message == null) {
			HttpConnection.turnTo400Page(resp, staticFileHandler);
			return;
		}
		try {
			message = URLDecoder.decode(message, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.info("<<<<decode string message: " + message + "error: ");
		}
		ChatClient chatClient = new ChatClient();
		logger.info("decode message for slack: " + message);
		List<String> params = new ArrayList<>();
		String display = "<h2>";
		try {
			String status = chatClient.sendMessage(message);
			if(status.equals("0")) {
				display +="send message success!"+"</h2>";
			}else if(status.equals("1")) {
				display +="send message failed: not 200 OK!"+"</h2>";
			}else {
				display +="send message failed: "+status + "</h2>";
			}
		} catch (IOException e) {
			logger.error("https connection exception when sending message: "+ message);
			display +="send message failed: https connection exception" + "</h2>";
		}
		params.add(display);
		StaticFileHandler sfHandler = new StaticFileHandler(webRoot);
		sfHandler.setParams(params);
		HttpConnection.turnToStaticFile200OK(req, resp, "/chat.html", sfHandler);
	}
}
