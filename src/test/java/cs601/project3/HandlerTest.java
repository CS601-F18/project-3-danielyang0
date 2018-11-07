package cs601.project3;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import cs601.project3.handler.ChatHandler;
import cs601.project3.handler.FindHandler;
import cs601.project3.handler.ReviewSearchHandler;
import cs601.project3.server.HttpRequest;
import cs601.project3.server.HttpRequestBuilder;
import cs601.project3.server.HttpResponse;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class HandlerTest {
	
	@Test
	public void testGetReview() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		HttpResponse resp = new HttpResponse(stream);
		ReviewSearchHandler reviewSearchHandler = new ReviewSearchHandler("webRoot");
		String str = constructReqString(null, "GET /reviewsearch HTTP/1.1", "Host: localhost:8080");
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		reviewSearchHandler.handle(req, resp);
		ByteArrayOutputStream outputStream = (ByteArrayOutputStream) resp.getOutputStream();
		String htmlPage = new String(outputStream.toByteArray());
		System.out.println(htmlPage);
		assertTrue(htmlPage.startsWith("HTTP/1.1 200 OK"));
	}
	
	@Test
	public void testPOSTReview() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		HttpResponse resp = new HttpResponse(stream);
		ReviewSearchHandler reviewSearchHandler = new ReviewSearchHandler("webRoot");
		
		String post ="query=computer+science";
		String str = constructReqString(post, "POST /reviewsearch HTTP/1.1", "Host: localhost:8080","Content-Length: "+post.length());
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		
		reviewSearchHandler.handle(req, resp);
		ByteArrayOutputStream outputStream = (ByteArrayOutputStream) resp.getOutputStream();
		String htmlPage = new String(outputStream.toByteArray());
		System.out.println(htmlPage);
		assertTrue(htmlPage.startsWith("HTTP/1.1 200 OK"));
	}
	
	//quer
	@Test
	public void testPOSTReview2() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		HttpResponse resp = new HttpResponse(stream);
		ReviewSearchHandler reviewSearchHandler = new ReviewSearchHandler("webRoot");
		
		String post ="quer=computer+science";
		String str = constructReqString(post, "POST /reviewsearch HTTP/1.1", "Host: localhost:8080","Content-Length: "+post.length());
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		
		reviewSearchHandler.handle(req, resp);
		ByteArrayOutputStream outputStream = (ByteArrayOutputStream) resp.getOutputStream();
		String htmlPage = new String(outputStream.toByteArray());
		System.out.println(htmlPage);
		assertTrue(htmlPage.startsWith("HTTP/1.1 400 BAD REQUEST"));
	}


	@Test
	public void testGetFind() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		HttpResponse resp = new HttpResponse(stream);
		FindHandler findHandler = new FindHandler("webRoot");
		String str = constructReqString(null, "GET /find HTTP/1.1", "Host: localhost:8080");
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		findHandler.handle(req, resp);
		ByteArrayOutputStream outputStream = (ByteArrayOutputStream) resp.getOutputStream();
		String htmlPage = new String(outputStream.toByteArray());
		System.out.println(htmlPage);
		assertTrue(htmlPage.startsWith("HTTP/1.1 200 OK"));
	}
	
	@Test
	public void testPOSTFind() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		HttpResponse resp = new HttpResponse(stream);
		FindHandler findHandler = new FindHandler("webRoot");
		
		String post ="asin=342342432";
		String str = constructReqString(post, "POST /find HTTP/1.1", "Host: localhost:8080","Content-Length: "+post.length());
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		
		findHandler.handle(req, resp);
		ByteArrayOutputStream outputStream = (ByteArrayOutputStream) resp.getOutputStream();
		String htmlPage = new String(outputStream.toByteArray());
		System.out.println(htmlPage);
		assertTrue(htmlPage.startsWith("HTTP/1.1 200 OK"));
	}
	
	//quer
	@Test
	public void testPOSTFind2() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		HttpResponse resp = new HttpResponse(stream);
		FindHandler findHandler = new FindHandler("webRoot");
		
		String post ="ain=c342342432";
		String str = constructReqString(post, "POST /find HTTP/1.1", "Host: localhost:8080","Content-Length: "+post.length());
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		
		findHandler.handle(req, resp);
		ByteArrayOutputStream outputStream = (ByteArrayOutputStream) resp.getOutputStream();
		String htmlPage = new String(outputStream.toByteArray());
		System.out.println(htmlPage);
		assertTrue(htmlPage.startsWith("HTTP/1.1 400 BAD REQUEST"));
	}
	
	
	
	@Test
	public void testPOSTChat() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		HttpResponse resp = new HttpResponse(stream);
		ChatHandler chatHandler = new ChatHandler("webRoot");
		
		String post ="message=test message";
		String str = constructReqString(post, "POST /slackbot HTTP/1.1", "Host: localhost:8080","Content-Length: "+post.length());
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		
		chatHandler.handle(req, resp);
		ByteArrayOutputStream outputStream = (ByteArrayOutputStream) resp.getOutputStream();
		String htmlPage = new String(outputStream.toByteArray());
		System.out.println(htmlPage);
		assertTrue(htmlPage.startsWith("HTTP/1.1 200 OK"));
	}
	
	//paramter error
	@Test
	public void testPOSTChat2() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		HttpResponse resp = new HttpResponse(stream);
		ChatHandler chatHandler = new ChatHandler("webRoot");
		
		String post ="mss=test message";
		String str = constructReqString(post, "POST /slackbot HTTP/1.1", "Host: localhost:8080","Content-Length: "+post.length());
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		
		chatHandler.handle(req, resp);
		ByteArrayOutputStream outputStream = (ByteArrayOutputStream) resp.getOutputStream();
		String htmlPage = new String(outputStream.toByteArray());
		System.out.println(htmlPage);
		assertTrue(htmlPage.startsWith("HTTP/1.1 400 BAD REQUEST"));
	}
	
	@Test
	public void testGETChat() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		HttpResponse resp = new HttpResponse(stream);
		ChatHandler chatHandler = new ChatHandler("webRoot");
		
		String str = constructReqString(null, "GET /slackbot HTTP/1.1", "Host: localhost:8080");
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		
		chatHandler.handle(req, resp);
		ByteArrayOutputStream outputStream = (ByteArrayOutputStream) resp.getOutputStream();
		String htmlPage = new String(outputStream.toByteArray());
		System.out.println(htmlPage);
		assertTrue(htmlPage.startsWith("HTTP/1.1 200 OK"));
	}
	
	
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
}
