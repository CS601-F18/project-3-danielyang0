package cs601.project3.amazonSearch.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Helper class ,used for reading a reviews file or QAs file and output a list of json objects
 * @author yangzun
 *
 */
public class AmazonFileParser {
	/**
	 * 
	 * @param fileName the json file which stores documents
	 * @param clazz the documents' class object
	 * @return a list of documents parsed from the file
	 * @throws IOException
	 */
	public static <T> List<T> parseJsonFileToObjects(String fileName, Class<T> clazz) throws IOException {
		Gson gson = new Gson();
		List<T> documents = new ArrayList<>();
		Charset charset = java.nio.charset.StandardCharsets.ISO_8859_1;
		int countSkip = 0;
		try (
				BufferedReader br = Files.newBufferedReader(Paths.get(fileName),charset);
				) {
			String line;
			while((line = br.readLine()) != null) {
				try {
					T item = gson.fromJson(line, clazz);
					documents.add(item);
				} catch(JsonSyntaxException e) {
					countSkip++;
				}
			}
		} catch(IOException e){
			throw e;
		}
		if(countSkip != 0) {
			System.out.println("\tskip " + countSkip + " line(s) in file: " + fileName);
		}
		return documents;
	}

}
