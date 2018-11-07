package cs601.project3.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cs601.project3.server.HttpRequest;
import cs601.project3.server.HttpResponse;
import cs601.project3.tools.PropertyReader;

/**
 * handler for handling requests for a static file on server
 * and also containing some methods for turning to a static page
 * @author yangzun
 *
 */
public class StaticFileHandler implements Handler{
	
	private static Logger logger = Logger.getLogger(StaticFileHandler.class);
	private static final String URL_ERROR_PAGE;
	private static final String METHODE_NOT_ALLOWED_PAGE;
	private static final String BAD_REQUEST_PAGE;
	//webRoot location on server
	private String webRoot;
	private List<String> params;
	
	static {
		PropertyConfigurator.configure("./config/log4j.properties");
		PropertyReader reader = new PropertyReader("./config","project3.properties");
		URL_ERROR_PAGE = reader.readStringValue("urlErrorPage", "/404.html");
		METHODE_NOT_ALLOWED_PAGE = reader.readStringValue("mothedNotAllowedPage", "/405.html");
		BAD_REQUEST_PAGE = reader.readStringValue("badRequestPage", "/400.html");
	}
	
	public StaticFileHandler(String webRoot) {
		this.webRoot = webRoot;
	}
	
	/**
	 * handle with a static file or a template,
	 * according to whether the handler has params set
	 */
	@Override
	public void handle(HttpRequest req, HttpResponse resp) {
		String pathInfo = req.getPath();
		OutputStream outputStream = resp.getOutputStream();
		PrintWriter pw = new PrintWriter(outputStream);
		pw.write(resp.getResponseHeader());
		if(params == null) {
			responseStaticFile(pw, webRoot+req.getPath());
		}else{
			//if params is set, open the template
			responseStaticTemplete(pw, webRoot+req.getPath());
		}
	}
	
	/**
	 * read static file, and write to client
	 * @param pw
	 * @param filePath
	 */
	public void responseStaticFile(PrintWriter pw, String filePath) {
		Charset charset = StandardCharsets.ISO_8859_1;
		try (
				BufferedReader br = Files.newBufferedReader(Paths.get(filePath),charset);
				) {
			String line;
			while((line = br.readLine()) != null) {
				pw.write(line+"\n");
			}
		} catch(IOException e){
			System.out.println(Paths.get(filePath));
			System.out.println("file not found");
		}
		pw.flush();
	}
	
	/**
	 * read static template, fill it with params, and write to client
	 * @param pw
	 * @param filePath
	 */
	public void responseStaticTemplete(PrintWriter pw, String filePath) {
		Charset charset = StandardCharsets.ISO_8859_1;
		try (
				BufferedReader br = Files.newBufferedReader(Paths.get(filePath),charset);
				) {
			String line;
			while((line = br.readLine()) != null) {
				for(int i=0; i < params.size(); i++) {
					String param = params.get(i);
					line = line.replace("{$"+i+"}", param);
				}
				pw.write(line);
			}
		} catch(IOException e){
			System.out.println(Paths.get(filePath));
			System.out.println("file not found");
		}
		pw.flush();
	}

	/**
	 * static method for going to 405 page
	 * @param resp
	 * @param sfHandler
	 */
	public static void turnTo405Page(HttpResponse resp, StaticFileHandler sfHandler) {
		HttpRequest req = new HttpRequest();
		req.setMethod("GET");
		req.setPath(METHODE_NOT_ALLOWED_PAGE);
		resp.setResponseHeader("HTTP/1.1 405 Method Not Allowed\nConnection: close\n\r\n");
		sfHandler.handle(req, resp);
	}
	
	/**
	 * static method for going to 400 page
	 * @param resp
	 * @param sfHandler
	 */
	public static void turnTo404Page(HttpResponse resp, StaticFileHandler sfHandler) {
		HttpRequest req = new HttpRequest();
		req.setMethod("GET");
		req.setPath(URL_ERROR_PAGE);
		resp.setResponseHeader("HTTP/1.1 404 NOT FOUND\nConnection: close\n\r\n");
		sfHandler.handle(req, resp);
	}
	
	/**
	 * static method for going to 400 page
	 * @param resp
	 * @param sfHandler
	 */
	public static void turnTo400Page(HttpResponse resp, StaticFileHandler sfHandler) {
		HttpRequest req = new HttpRequest();
		req.setMethod("GET");
		req.setPath(BAD_REQUEST_PAGE);
		resp.setResponseHeader("HTTP/1.1 400 BAD REQUEST\nConnection: close\n\r\n");
		sfHandler.handle(req, resp);
	}
	
	/**
	 * static method for going to 200 page
	 * @param originalRequest
	 * @param resp
	 * @param filePath
	 * @param sfHandler
	 */
	public static void turnToStaticFile200OK(HttpRequest originalRequest, HttpResponse resp, String filePath, StaticFileHandler sfHandler) {
		HttpRequest req = new HttpRequest();
		req.setMethod("GET");
		req.setPath(filePath);
		resp.setResponseHeader("HTTP/1.1 200 OK\nConnection: close\n\r\n");
		sfHandler.handle(req, resp);
		logger.warn(">>>>>>>>"+originalRequest.getRequestLines() + " " +originalRequest.getPostData() + " 200 OK");
	}

	public void setParams(List<String> params) {
		this.params = params;
	}

}
