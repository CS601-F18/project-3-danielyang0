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

import cs601.project3.handler.Handler;
import cs601.project3.server.HttpRequest;
import cs601.project3.server.HttpResponse;



public class HTTPServer {
	private static boolean running = true;	
	private int port;
	public Map<String,Handler> handlers = new HashMap<>();
	private static final String BASE = "webRoot";
	public static final StaticFileHandler staticFileHandler = new StaticFileHandler(BASE);

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
				new HttpConnection(socket, id++, handlers).start();
			}catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
	};

}

