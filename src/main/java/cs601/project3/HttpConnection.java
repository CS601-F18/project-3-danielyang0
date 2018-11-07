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
import cs601.project3.server.HttpRequest;
import cs601.project3.server.HttpRequestBuilder;
import cs601.project3.server.HttpResponse;

/**
 * one request opens a HttpConnection
 * @author yangzun
 *
 */
public class HttpConnection extends Thread {
	private Socket socket;
	private int id;
	private Map<String,Handler> handlers;
	
	private static final String URL_ERROR_PAGE = "/404.html";
	private static final String METHODE_NOT_ALLOWED_PAGE = "/405.html";
	private static final String BAD_REQUEST_PAGE = "/400.html";
	public final StaticFileHandler staticFileHandler;
	
	private static Logger logger = Logger.getLogger(HTTPServer.class);
	static {
		PropertyConfigurator.configure("./config/log4j.properties");
	}
	
	public HttpConnection(Socket socket, int id, Map<String,Handler> handlers, String webRoot) {
		super();
		this.socket = socket;
		this.id = id;
		this.handlers = handlers;
		staticFileHandler = new StaticFileHandler(webRoot);
	}
	
	public static void turnTo405Page(HttpResponse resp, StaticFileHandler sfHandler) {
		HttpRequest req = new HttpRequest();
		req.setMethod("GET");
		req.setPath(METHODE_NOT_ALLOWED_PAGE);
		resp.setResponseHeader("HTTP/1.1 405 Method Not Allowed\nConnection: close\n\r\n");
		sfHandler.handle(req, resp);
	}
	
	public static void turnTo404Page(HttpResponse resp, StaticFileHandler sfHandler) {
		HttpRequest req = new HttpRequest();
		req.setMethod("GET");
		req.setPath(URL_ERROR_PAGE);
		resp.setResponseHeader("HTTP/1.1 404 NOT FOUND\nConnection: close\n\r\n");
		sfHandler.handle(req, resp);
	}
	
	public static void turnTo400Page(HttpResponse resp, StaticFileHandler sfHandler) {
		HttpRequest req = new HttpRequest();
		req.setMethod("GET");
		req.setPath(BAD_REQUEST_PAGE);
		resp.setResponseHeader("HTTP/1.1 400 BAD REQUEST\nConnection: close\n\r\n");
		sfHandler.handle(req, resp);
	}
	
	public static void turnToStaticFile200OK(HttpResponse resp, String filePath, StaticFileHandler sfHandler) {
		HttpRequest req = new HttpRequest();
		req.setMethod("GET");
		req.setPath(filePath);
		resp.setResponseHeader("HTTP/1.1 200 OK\nConnection: close\n\r\n");
		sfHandler.handle(req, resp);
	}

	@Override
	public void run() {
		logger.info("running connection thread "+id+"...");
		try (BufferedReader instream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter writer = new PrintWriter(socket.getOutputStream())) {
			HttpRequest req = new HttpRequestBuilder().parseRequest(instream);
			HttpResponse resp = new HttpResponse(socket.getOutputStream());
			if(!req.isValid()) {
				logger.warn(req.getMethod() + " request invalid, 400 bad request");
				turnTo400Page(resp, staticFileHandler);
				return;
			}
			if(!req.isMethodSupported()) {
				logger.warn(req.getMethod() + " method is not supported! ");
				turnTo405Page(resp, staticFileHandler);
				return;
			}
			Handler handler = handlers.get(req.getPath());
			if(handler != null) {
				handler.handle(req, resp);
			}else{
				turnTo404Page(resp, staticFileHandler);
				return;
			}
		}catch(IOException e) {
			logger.info("io exception when run a http connection");
		}finally{
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
