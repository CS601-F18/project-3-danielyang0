package cs601.project3;

import cs601.project3.handler.FindHandler;
import cs601.project3.handler.ReviewSearchHandler;
import cs601.project3.tools.PropertyReader;

//TO DO: when user request before the database has been intialized, return a web page
public class SearchApplication {
	public static void main(String[] args) {
		AmazonSearch.getInstance();
		PropertyReader reader = new PropertyReader("./config","httpconfig.properties");
		int port = reader.readIntValue("searchport", 1025);
		HTTPServer server = new HTTPServer(port);
		//The request GET /reviewsearch will be dispatched to the 
		//handle method of the ReviewSearchHandler.
		server.addMapping("/reviewsearch", new ReviewSearchHandler());
		//The request GET /find will be dispatched to the 
		//handle method of the FindHandler.
		server.addMapping("/find", new FindHandler());
		server.startup();
	}
	
	
	
	
	
}
