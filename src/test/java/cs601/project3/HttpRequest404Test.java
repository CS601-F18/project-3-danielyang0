package cs601.project3;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

import cs601.project3.handler.Handler;
import cs601.project3.server.HttpRequest;
import cs601.project3.server.HttpRequestBuilder;
import cs601.project3.server.HttpResponse;

public class HttpRequest404Test {
	
	//a method to turn a string to buffered reader
//	https://www.mkyong.com/java/how-to-convert-string-to-inputstream-in-java/
	public static BufferedReader strToBufferedReader(String str){
		// convert String into InputStream
		InputStream is = new ByteArrayInputStream(str.getBytes());
		// read it with BufferedReader
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		return br;
	 }
	
	public String requestDebug(HttpRequest req) {
		return req.getRequestLines() + "\n==========\n" +req.getHeaderLines();
	}
	
	
	public String constructReqString(String postData, String... requestLineAndheaderLines){
		StringBuffer sb = new StringBuffer();
		for (String s : requestLineAndheaderLines) {
			sb.append(s).append("\n");
		}
		sb.append("\n");
		if(postData != null) {
			sb.append(postData);
		}
		return sb.toString();
	}
	
	//just a empty test object
	private Handler testHandler = new Handler() {
		@Override
		public void handle(HttpRequest req, HttpResponse resp) {
		}
	};
	
	@Test
	public void testReq1() {
		Map<String,Handler> handlers = new HashMap<>();
		handlers.put("/a", testHandler);
		handlers.put("/b", testHandler);
		HttpConnection hc = new HttpConnection(null, 1, handlers, "");
		
		String str = constructReqString(null, "GET /reviewsearch HTTP/1.1", "Host: localhost:8080");
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertTrue(req.isValid() && hc.getHandler(req)== null);
	}

	
	@Test
	public void testReq2() {
		
		Map<String,Handler> handlers = new HashMap<>();
		handlers.put("/a", testHandler);
		handlers.put("/b", testHandler);
		HttpConnection hc = new HttpConnection(null, 1, handlers, "");
		String str = constructReqString(null, "GET /a HTTP/1.1", "Host: localhost:8080");
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertTrue(req.isValid() && hc.getHandler(req) != null);
	}
	
}