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

/**
 * one httpserver is associated with every application.
 * repsonsible for receiving sockets,and putting them into threads
 * runs forever if not interrupted
 * @author yangzun
 *
 */
public class HTTPServer {
	private static boolean running = true;	
	private int port;
	private final String webRoot;
	private Map<String,Handler> handlers = new HashMap<>();
	private static Logger logger = Logger.getLogger(HTTPServer.class);
	static {
		PropertyConfigurator.configure("./config/log4j.properties");
	}

	public HTTPServer(int port, String webRoot) {
		super();
		this.port = port;
		this.webRoot = webRoot;
	}
	/**
	 * add a mapping of a path to a handler
	 * @param path
	 * @param handler
	 */
	public void addMapping(String path, Handler handler){
		handlers.put(path, handler);
	}

	/**
	 * start a server and spin around as long as the running indicater is true
	 */
	public void startup(){
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		logger.info("Listening for connection on port "+ port +" ...."); 
		int id = 0;
		//TO DO: add a method for turning down the server.
		while(running){
			try{
				Socket socket = server.accept();
				socket.setSoTimeout(30000);
				new HttpConnection(socket, id++, handlers, webRoot).start();
			}catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
	};

}

