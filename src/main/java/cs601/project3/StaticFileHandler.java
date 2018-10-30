package cs601.project3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * handle requests for a static file on server
 * @author yangzun
 *
 */
public class StaticFileHandler implements Handler {
	
	//webRoot location on server
	private String webRoot;

	public StaticFileHandler(String webRoot) {
		this.webRoot = webRoot;
	}
	
	@Override
	public void handle(HttpRequest req, HttpResponse resp) {
		String pathInfo = req.getPathInfo();
		OutputStream outputStream = resp.getOutputStream();
		PrintWriter pw = new PrintWriter(outputStream);
		pw.write(resp.getResponseHeader());
		System.out.println(req.getPathInfo());
		writeFromFile(pw, webRoot+req.getPathInfo());

	}
	
	/**
	 * read static file, and write to client
	 * @param pw
	 * @param filePath
	 */
	public void writeFromFile(PrintWriter pw, String filePath) {
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

}
