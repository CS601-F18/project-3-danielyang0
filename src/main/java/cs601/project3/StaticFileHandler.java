package cs601.project3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import cs601.project3.handler.Handler;
import cs601.project3.server.HttpRequest;
import cs601.project3.server.HttpResponse;

/**
 * handle requests for a static file on server
 * @author yangzun
 *
 */
public class StaticFileHandler implements Handler{
	
	//webRoot location on server
	private String webRoot;
	private List<String> params;
	

	public StaticFileHandler(String webRoot) {
		this.webRoot = webRoot;
	}
	
	@Override
	public void handle(HttpRequest req, HttpResponse resp) {
		String pathInfo = req.getPath();
		OutputStream outputStream = resp.getOutputStream();
		PrintWriter pw = new PrintWriter(outputStream);
		pw.write(resp.getResponseHeader());
//		System.out.println(req.getPathInfo());
		if(params == null) { 
			responseStaticFile(pw, webRoot+req.getPath());
		}else{
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
				pw.write(line);
			}
		} catch(IOException e){
			System.out.println(Paths.get(filePath));
			System.out.println("file not found");
		}
		pw.flush();
	}
	
	public void responseStaticTemplete(PrintWriter pw, String filePath) {
		Charset charset = StandardCharsets.ISO_8859_1;
		try (
				BufferedReader br = Files.newBufferedReader(Paths.get(filePath),charset);
				) {
			String line;
			while((line = br.readLine()) != null) {
				
//				if(line.contains("{$0}")) {
//					line = line.replace("{$0}", param);
//				}
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

	public void setParams(List<String> params) {
		this.params = params;
	}
	

	

}
