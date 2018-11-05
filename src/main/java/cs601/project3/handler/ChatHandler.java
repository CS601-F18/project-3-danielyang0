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
	
	/**
	 * handle http request for chat
	 */
	@Override
	public void handle(HttpRequest req, HttpResponse resp) {
		logger.debug("handled by FindHandler");
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
		StaticFileHandler staticFileHandler = resp.getStaticFileHandler();
		req.setMethod("GET");
		req.setPath("/chat.html");
		resp.setResponseHeader("HTTP/1.0 200 OK\nConnection: close\n\r\n");
		staticFileHandler.handle(req, resp);
	}
	
	/**
	 * handle http POST request for sending message
	 * send message to the slackbot client to send message to slack channel
	 * return the same page returned by GET method for user to input another message
	 * @param req
	 * @param resp
	 */
	public void doPost(HttpRequest req, HttpResponse resp) {
		//TO DO: decoding
		StaticFileHandler staticFileHandler = resp.getStaticFileHandler();
		String message = req.getPostData().get("message");
		if(message.startsWith("test")) {
			message = message.substring(4);
		}
		ChatClient chatClient = new ChatClient();
		logger.info("send message to slack: " + message);
		try {
			chatClient.sendMessage(message);
		} catch (Exception e) {
			logger.error("send message error: "+ message);
		}
		doGet(req,resp);
		
	}
}
