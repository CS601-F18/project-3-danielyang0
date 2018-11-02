package cs601.project3;

public class SearchApplication {
	public static void main(String[] args) {
		AmazonSearch.getInstance();
		int port = 1028;
		HTTPServer server = new HTTPServer(port);
		//The request GET /reviewsearch will be dispatched to the 
		//handle method of the ReviewSearchHandler.
		server.addMapping("/reviewsearch", new ReviewSearchHandler());
		//The request GET /find will be dispatched to the 
		//handle method of the FindHandler.
//		server.addMapping("/find", new FindHandler());
		server.startup();
	}
	
	
	
	
	
}
