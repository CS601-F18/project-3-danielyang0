package cs601.project3;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cs601.project3.handler.Handler;
import cs601.project3.handler.StaticFileHandler;
import cs601.project3.server.HttpRequest;
import cs601.project3.server.HttpRequestBuilder;
import cs601.project3.server.HttpResponse;

/**
 * A thread for handling a socket.
 * when server get a http request, a sokcet opens and is put into HttpConnection to handle the request
 * @author yangzun
 *
 */
public class HttpConnection extends Thread {
	private Socket socket;
	private int id;
	private Map<String,Handler> handlers;
	
	public final StaticFileHandler staticFileHandler;
	
	private static Logger logger = Logger.getLogger(HTTPServer.class);
	static {
		PropertyConfigurator.configure("./config/log4j.properties");
	}
	
	/**
	 * initialize with socket and other related objects 
	 * @param socket
	 * @param id
	 * @param handlers
	 * @param webRoot
	 */
	public HttpConnection(Socket socket, int id, Map<String,Handler> handlers, String webRoot) {
		super();
		this.socket = socket;
		this.id = id;
		this.handlers = handlers;
		staticFileHandler = new StaticFileHandler(webRoot);
	}

	public Handler getHandler(HttpRequest req) {
		return handlers.get(req.getPath());
	}
	
	/**
	 * do all the things related to a socket connection.
	 * close socket anyway after operations
	 */
	@Override
	public void run() {
		logger.info("running connection thread "+id+"...");
		try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter writer = new PrintWriter(socket.getOutputStream())) {
			//parse a request to validate and get the HTTP request object
			HttpRequest req = new HttpRequestBuilder().parseRequest(br);
			HttpResponse resp = new HttpResponse(socket.getOutputStream());
			//if invalid, response with 400 status
			if(!req.isValid()) {
				StaticFileHandler.turnTo400Page(resp, staticFileHandler);
				logger.warn(">>>>>>>>"+req.getRequestLines() + " " +req.getPostData() + " 400 Bad request!");
				return;
			}
			if(!req.isMethodSupported()) {
				StaticFileHandler.turnTo405Page(resp, staticFileHandler);
				logger.warn(">>>>>>>>"+req.getRequestLines() + " " +req.getPostData() + "405 Method not supported!");
				return;
			}
//			Handler handler = handlers.get(req.getPath());
			Handler handler = getHandler(req);
			if(handler != null) {
				handler.handle(req, resp);
			}else{
				StaticFileHandler.turnTo404Page(resp, staticFileHandler);
				logger.warn(">>>>>>>>"+req.getRequestLines() + " " +req.getPostData() + " 404 not found!");
				return;
			}
		}catch(IOException e) {
			logger.info("io exception when run a http connection");
		}finally{
			//close sokcet anyway
			if(socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
