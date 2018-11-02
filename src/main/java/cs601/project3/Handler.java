package cs601.project3;

import cs601.project3.server.HttpRequest;
import cs601.project3.server.HttpResponse;

public interface Handler {
	public void handle(HttpRequest req, HttpResponse resp);
}
