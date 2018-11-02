package cs601.project3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cs601.project3.server.HttpRequest;
import cs601.project3.server.HttpResponse;



public class HTTPServer {
	private static boolean running = true;	
	private int port;
	public Map<String,Handler> handlers = new HashMap<>();
	private static final String BASE = "webRoot";
	private static final String URL_ERROR_PAGE = "/404.html";
	private static final String METHODE_NOT_ALLOWED_PAGE = "/405.html";
	private static final StaticFileHandler staticFileHandler = new StaticFileHandler(BASE);

	private static Logger logger = Logger.getLogger(HTTPServer.class);
	static {
		PropertyConfigurator.configure("./config/log4j.properties");
	}

	public HTTPServer(int port) {
		super();
		this.port = port;
		List<String> supportedHttpMethod = new ArrayList<>();
		supportedHttpMethod.add("GET");
		supportedHttpMethod.add("POST");
		HttpRequest.setSupportedHttpMethod(supportedHttpMethod);
	}
	public void addMapping(String path, Handler handler){
		handlers.put(path, handler);
	}

	public void startup(){
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		logger.info("Listening for connection on port "+ port +" ...."); 
		int id = 0;
		while(running){
			try{
				Socket socket = server.accept();
				socket.setSoTimeout(3000);
				new Connection(socket, id++).start();
			}catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
	};


	private class Connection extends Thread {
		private Socket socket;
		private int id;

		public Connection(Socket socket, int id) {
			super();
			this.socket = socket;
			this.id = id;
		}
		private void parseReqInfo(BufferedReader br, HttpRequest req) throws IOException {
			String requestLines = br.readLine();
			logger.info(requestLines);
			if(requestLines == null) return; 
			String[] requestLineParts = requestLines.split("\\s+");
			req.setMethod(requestLineParts[0]);
			req.setPath(requestLineParts[1]);
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
			logger.debug(headLines);
			
			if ("POST".equals(req.getMethod())) {
				byte b = 0;
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				for (int i = 0; i < req.getContentLength(); i++) {
					b = (byte) br.read();
					bout.write(b);
				}
				String postData = new String(bout.toByteArray());
				req.setPostData(postData);
				logger.debug("post data is: "+ postData);
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
					req.setPath(URL_ERROR_PAGE);
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

}

