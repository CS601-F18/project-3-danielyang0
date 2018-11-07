package cs601.project3;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import cs601.project3.server.HttpRequest;
import cs601.project3.server.HttpRequestBuilder;

public class HttpRequestTest {
	
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
	/**
	 * with only request line, false
	 */
	@Test
	public void testReq1() {
		String str = constructReqString(null, "GET /reviewsearch HTTP/1.1");
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertFalse(req.isValid());
	}
	
	@Test
	public void testReq25() {
		String str = constructReqString(null, "GET /reviewsearch WORINGPROTOCOL/1.1","Host: localhost:8080");
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertFalse(req.isValid());
	}
	
	//worng protocol version
	@Test
	public void testReq26() {
		String str = constructReqString(null, "GET /reviewsearch HTTP/dsfdfdsffdsf","Host: localhost:8080");
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertFalse(req.isValid());
	}
	
	//path not start with /
	@Test
	public void testReq27() {
		String str = constructReqString(null, "GET reviewsearch HTTP/1.1","Host: localhost:8080");
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertFalse(req.isValid());
	}
	
	
	@Test
	public void testReq28() {
		String str = constructReqString(null, "GET /reviewsearch HTTP/1.1", "Host: localhost:8080");
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertTrue(req.isValid());
	}
	
	//valid, but method not supported
	@Test
	public void testReq29() {
		String str = constructReqString(null, "PUT /reviewsearch HTTP/1.1","Host: localhost:8080");
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertTrue(req.isValid() && !req.isMethodSupported());
	}
	
	@Test
	public void testReq2() {
		String str = constructReqString(null, "GET /reviewsearch HTTP/1.1", "Host: localhost:8080");
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertTrue(req.isValid(), requestDebug(req));
	}
	
	@Test
	public void testReq3() {
		String str = constructReqString(null, "GET /find HTTP/1.1");
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertFalse(req.isValid());
	}
	
	@Test
	public void testReq4() {
		String str = constructReqString(null, "GET /find HTTP/1.1", "Host: localhost:8080");
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertTrue(req.isValid(), requestDebug(req));
	}
	
	@Test
	public void testReq5() {
		String str = constructReqString(null, "POST /find HTTP/1.1", "Host: localhost:8080");
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertFalse(req.isValid(), requestDebug(req));
	}
	
	//lack content-length
	@Test
	public void testReq6() {
		String str = constructReqString("query=x12f", "POST /find HTTP/1.1", "Host: localhost:8080");
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertFalse(req.isValid(), requestDebug(req));
	}
	
	//content-length correct
	@Test
	public void testReq7() {
		String post="query=x12f";
		String str = constructReqString(post, "POST /find HTTP/1.1", "Host: localhost:8080","Content-Length: "+post.length());
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertTrue(req.isValid(), requestDebug(req));
	}
	
	//content-length not correct
	@Test
	public void testReq8() {
		String str = constructReqString("query=x12f", "POST /find HTTP/1.1", "Host: localhost:8080","Content-Length: 5");
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertFalse(req.isValid(), requestDebug(req));
	}
	
	//validity check will not require it has to be "query=xx"
	//it will be checked in the handler
	@Test
	public void testReq9() {
		String post="quer=x12f";
		String str = constructReqString("quer=x12f", "POST /find HTTP/1.1", "Host: localhost:8080","Content-Length: "+post.length());
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertTrue(req.isValid(), requestDebug(req));
	}
	
	@Test
	public void testReq10() {
		String post="quer=x12f&unused=abc";
		String str = constructReqString(post, "POST /find HTTP/1.1", "Host: localhost:8080","Content-Length: "+post.length());
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertTrue(req.isValid(), requestDebug(req));
	}
	
	@Test
	public void testReq11() {
		//repeat is allowd
		String post="quer=x12&quer=x12";
		String str = constructReqString(post, "POST /find HTTP/1.1", "Host: localhost:8080","Content-Length: "+post.length());
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertTrue(req.isValid(), requestDebug(req));
	}
	
	@Test
	public void testReq12() {
		//conflict is not allowd
		String post="quer=x12&quer=dffdsfdsfds";
		String str = constructReqString(post, "POST /find HTTP/1.1", "Host: localhost:8080","Content-Length: "+post.length());
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertFalse(req.isValid(), requestDebug(req));
	}
	
	//content lenth must be an integer
	@Test
	public void testReq21() {
		//conflict is not allowd
		String post="quer=x12&quer=dffdsfdsfds";
		String str = constructReqString(post, "POST /find HTTP/1.1", "Host: localhost:8080","Content-Length: "+ "notAnInteger");
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertFalse(req.isValid(), requestDebug(req));
	}
	
	
	
	
	
	@Test
	public void testReq13() {
		String str = constructReqString(null, "POST /reviewsearch HTTP/1.1", "Host: localhost:8080");
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertFalse(req.isValid(), requestDebug(req));
	}
	
	//lack content-length
	@Test
	public void testReq14() {
		String str = constructReqString("query=x12f", "POST /reviewsearch HTTP/1.1", "Host: localhost:8080");
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertFalse(req.isValid(), requestDebug(req));
	}
	
	//content-length correct
	@Test
	public void testReq15() {
		String post="query=x12f";
		String str = constructReqString(post, "POST /reviewsearch HTTP/1.1", "Host: localhost:8080","Content-Length: "+post.length());
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertTrue(req.isValid(), requestDebug(req));
	}
	
	//content-length not correct
	@Test
	public void testReq16() {
		String str = constructReqString("query=x12f", "POST /reviewsearch HTTP/1.1", "Host: localhost:8080","Content-Length: 5");
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertFalse(req.isValid(), requestDebug(req));
	}
	
	//validity check will not require it has to be "query=xx"
	//it will be checked in the handler
	@Test
	public void testReq17() {
		String post="quer=x12f";
		String str = constructReqString("quer=x12f", "POST /reviewsearch HTTP/1.1", "Host: localhost:8080","Content-Length: "+post.length());
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertTrue(req.isValid(), requestDebug(req));
	}
	
	@Test
	public void testReq18() {
		String post="quer=x12f&unused=abc";
		String str = constructReqString(post, "POST /reviewsearch HTTP/1.1", "Host: localhost:8080","Content-Length: "+post.length());
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertTrue(req.isValid(), requestDebug(req));
	}
	
	@Test
	public void testReq19() {
		//repeat is allowd
		String post="quer=x12&quer=x12";
		String str = constructReqString(post, "POST /reviewsearch HTTP/1.1", "Host: localhost:8080","Content-Length: "+post.length());
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertTrue(req.isValid(), requestDebug(req));
	}
	
	@Test
	public void testReq20() {
		//conflict is not allowd
		String post="quer=x12&quer=dffdsfdsfds";
		String str = constructReqString(post, "POST /reviewsearch HTTP/1.1", "Host: localhost:8080","Content-Length: "+post.length());
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertFalse(req.isValid(), requestDebug(req));
	}
	
	//content lenth must be an integer
	@Test
	public void testReq22() {
		//conflict is not allowd
		String post="quer=x12&quer=dffdsfdsfds";
		String str = constructReqString(post, "POST /reviewsearch HTTP/1.1", "Host: localhost:8080","Content-Length: "+ "notAnInteger");
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertFalse(req.isValid(), requestDebug(req));
	}
	
	@Test
	public void testReq23() {
		String str = constructReqString(null, "GET /slackbot HTTP/1.1", "Host: localhost:8080");
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertTrue(req.isValid(), requestDebug(req));
	}
	
	@Test
	public void testReq24() {
		//conflict is not allowd
		String post="message=testmesg";
		String str = constructReqString(post, "POST /slackbot HTTP/1.1", "Host: localhost:8080","Content-Length: "+ post.length());
		BufferedReader br = strToBufferedReader(str);
		HttpRequest req = new HttpRequestBuilder().parseRequest(br);
		assertTrue(req.isValid(), requestDebug(req));
	}
	
	
}
