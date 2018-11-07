package cs601.project3;

import cs601.project3.handler.FindHandler;
import cs601.project3.handler.ReviewSearchHandler;
import cs601.project3.tools.PropertyReader;

/**
 * a search application for search reviews in the amazon inverted index database
 * @author yangzun
 *
 */
public class SearchApplication {
	public static void main(String[] args) {
		AmazonSearch.getInstance();
		PropertyReader reader = new PropertyReader("./config","project3.properties");
		String BASE = reader.readStringValue("searchWebRoot", "webRoot");
		int port = reader.readIntValue("searchport", 1025);
		HTTPServer server = new HTTPServer(port, BASE);
		//The request GET /reviewsearch will be dispatched to the 
		//handle method of the ReviewSearchHandler.
		server.addMapping("/reviewsearch", new ReviewSearchHandler(BASE));
		//The request GET /find will be dispatched to the 
		//handle method of the FindHandler.
		server.addMapping("/find", new FindHandler(BASE));
		server.startup();
	}
}

//ssh -L 8080:mcvm145.cs.usfca.edu:8080 zyang65@stargate.cs.usfca.edu
//ssh -L 9090:mcvm145.cs.usfca.edu:9090 zyang65@stargate.cs.usfca.edu
//nohup java -jar >> filename.out &
//ps aux | grep java
