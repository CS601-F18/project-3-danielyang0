package cs601.project3;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.junit.Test;

import cs601.project3.server.HttpRequest;

public class HttpConnectionTest {
	
	private String encodeUrl(String s) throws UnsupportedEncodingException {
		String encoded = URLEncoder.encode(s, "UTF-8");
		System.out.println(encoded);
		return encoded;
	}
	
	private String encodeAndDecode(String s) throws UnsupportedEncodingException {
		System.out.println(s);
		String s2 = HttpRequest.decodeUrl(encodeUrl(s));
		System.out.println(s2);
		return s2;
	}
	
	
	@Test
	public void testDecodeUrl2() {
		try {
			System.out.println(HttpRequest.decodeUrl("%C3%83%C2%A4%C3%82%C2%B8%C3%82%C2%AD%C3%83%C2%A6%C3%82%E2%80%93%C3%82%E2%80%A1"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDecodeUrl() {
		try {
			encodeAndDecode("&#27979;  &#35797;");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}
}
