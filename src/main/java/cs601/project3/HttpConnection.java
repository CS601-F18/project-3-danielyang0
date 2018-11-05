package cs601.project3;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cs601.project3.handler.Handler;
import cs601.project3.server.HttpRequest;
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
	private static StaticFileHandler staticFileHandler;
	private static final String URL_ERROR_PAGE = "/404.html";
	private static final String METHODE_NOT_ALLOWED_PAGE = "/405.html";

	private static Logger logger = Logger.getLogger(HTTPServer.class);
	static {
		PropertyConfigurator.configure("./config/log4j.properties");
		staticFileHandler = HTTPServer.staticFileHandler;
	}
	
	public HttpConnection(Socket socket, int id, Map<String,Handler> handlers) {
		super();
		this.socket = socket;
		this.id = id;
		this.handlers = handlers;
	}

	private void parseReqInfo(BufferedReader br, HttpRequest req) throws IOException {
		String requestLines = br.readLine();
		logger.info(requestLines);
		if(requestLines == null) return; 
		String[] requestLineParts = requestLines.split("\\s+");
		req.setMethod(requestLineParts[0]);
		req.setPath(requestLineParts[1].split("\\?")[0]);
		req.setProtocol(requestLineParts[2]);
		String line = br.readLine();
		StringBuffer headLines = new StringBuffer();
		while(line != null && !line.trim().isEmpty()) {
			if(line.toUpperCase().startsWith("CONNECTION")) {
				if(line.toUpperCase().substring("CONNECTION: ".length()).equals("keep-alive")) {
					req.setKeepAlive(true);
				}
			}else if(line.toUpperCase().startsWith("CONTENT-LENGTH: ")) {
				try{
					req.setContentLength(Integer.parseInt(line.substring("CONTENT-LENGTH: ".length())));
				}catch(NumberFormatException e) {
				}
			}
			headLines.append(line).append("\n");
			line = br.readLine();
		}
		logger.debug("headlines are: \n" + headLines);
		if ("POST".equals(req.getMethod())) {
			byte b = 0;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			System.out.println(req.getContentLength());
			for (int i = 0; i < req.getContentLength(); i++) {
				b = (byte) br.read();
				bout.write(b);
			}
			String postData = new String(bout.toByteArray());
			req.setPostData(postData);
			logger.debug("post data is:\n "+ postData);
			logger.debug("http method is: " + req.getMethod());
			logger.debug(req.getPostData());
		}
	}

	@Override
	public void run() {
		logger.info("running connection thread "+id+"...");
		try (BufferedReader instream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter writer = new PrintWriter(socket.getOutputStream())) {
			HttpRequest req = new HttpRequest();
			parseReqInfo(instream, req);
			if(req.isEmpty()) return;
			HttpResponse resp = new HttpResponse(socket.getOutputStream());
			resp.setStaticFileHandler(staticFileHandler);
			if(!req.isMethodSupported()) {
				logger.warn(req.getMethod() + " method is not supported! ");
				req.setMethod("GET");
				req.setPath(METHODE_NOT_ALLOWED_PAGE);
				resp.setResponseHeader("HTTP/1.1 405 Method Not Allowed\n\r\n");
				staticFileHandler.handle(req, resp);
				return;
			}

			Handler handler = handlers.get(req.getPath());
			if(handler != null) {
				handler.handle(req, resp);
			}else{
				req.setMethod("GET");
				req.setPath(URL_ERROR_PAGE);
				resp.setResponseHeader("HTTP/1.1 404 NOT FOUND\n\r\n");
				staticFileHandler.handle(req, resp);
				return;
			}
		}catch(IOException e) {
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
