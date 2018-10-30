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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


public class HTTPServer {
	private static boolean running = true;	
	private int port;
	public Map<String,Handler> handlers = new HashMap<>();
	private static final String BASE = "webRoot";
	private static final String URL_ERROR_PAGE = "/404.html";
	private static final StaticFileHandler staticFileHandler = new StaticFileHandler(BASE);
	
	public HTTPServer(int port) {
		super();
		this.port = port;
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
		System.out.println("Listening for connection on port "+ port +" ...."); 
		int id = 0;
		while(running){
			try{
				Socket socket = server.accept();
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

		@Override
		public synchronized void run() {
			System.out.println("running connection thread "+id+"...");
			BufferedReader instream = null;
			PrintWriter writer = null;
			try{
				instream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream());
				System.out.println("<<<<<<<");
				String requestLines = instream.readLine();
				System.out.println(">>>>>>>");
				String headLines = "";
				String line = instream.readLine();
				while(line != null && !line.trim().isEmpty()) {// 
					headLines += line + "\n";
					line = instream.readLine();
				}
				
				//TODO: null pointer exception when doing nothing for a while
				String[] requestLineParts = requestLines.split(" ");
				String method = requestLineParts[0];
				String path = requestLineParts[1];
				String protocol = requestLineParts[2];
				
				HttpRequest req = new HttpRequest(method, path, protocol);
				HttpResponse resp = new HttpResponse(socket.getOutputStream());
				resp.setStaticFileHandler(staticFileHandler);
				Handler handler = handlers.get(path);
				if(handler != null) {
					handler.handle(req, resp);
				}else{
					req.setMethod("get");
					req.setPathInfo(URL_ERROR_PAGE);
					resp.setResponseHeader("HTTP/1.0 404 NOT FOUND\n\r\n");
					staticFileHandler.handle(req, resp);
				}

			}catch(IOException ioe) {
				ioe.printStackTrace();
			}finally{
				if(instream != null) {
					try {
						instream.close();
					} catch (IOException e) {
					}
				}
				if(writer != null) {
					writer.close();
				}
			}
		}
	}
	/**
	 * Read a line of bytes until \n character.
	 * @param instream
	 * @return
	 * @throws IOException
	 */
	private static String oneLine(InputStream instream) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte b = (byte) instream.read();
		while(b != '\n') {
			bout.write(b);
			b = (byte) instream.read();
		}
		return new String(bout.toByteArray());
	}
	
}

