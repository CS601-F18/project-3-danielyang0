package cs601.project3;


public class ReviewSearchHandler implements Handler{

	@Override
	public void handle(HttpRequest req, HttpResponse resp) {
		System.out.println(req.getMethod());
		if("GET".equals(req.getMethod())) {
			doGet(req,resp);
		}
	}
	
	public void doGet(HttpRequest req, HttpResponse resp) {
		StaticFileHandler staticFileHandler = resp.getStaticFileHandler();
		req.setMethod("get");
		req.setPathInfo("/reviewSearchPage.html");
		resp.setResponseHeader("HTTP/1.0 200 OK\n\r\n");
		staticFileHandler.handle(req, resp);
	}

}
