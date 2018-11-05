package cs601.project3.chat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.google.gson.JsonObject;

import cs601.project3.HTTPServer;

public class ChatClient {
//  POST https://slack.com/api/chat.postMessage
//	Content-type: application/json
//	Authorization: Bearer xoxp-378520430422-399500190231-469474756640-3510da3d2f507e447bc3a8f0783ffdf1
//	{"channel":"project3","text":"zun yang"}

	
	private static Logger logger = Logger.getLogger(ChatClient.class);
	static {
		PropertyConfigurator.configure("./config/log4j.properties");
	}
	
	
	//TO DO:  anything other than a 200 OK from the Slack API then you should send an appropriate reply to the client.
	
	public void sendMessage(String msg) throws Exception {
		//create URL object
		URL url = new URL("https://slack.com/api/chat.postMessage");

		//create secure connection 
		HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
		//set HTTP method
		connection.setRequestMethod("POST");

		connection.setRequestProperty("Authorization", "Bearer xoxp-378520430422-399500190231-469474756640-3510da3d2f507e447bc3a8f0783ffdf1");
		connection.setRequestProperty("Content-Type", "application/json");
		JsonObject postData = new JsonObject();
		postData.addProperty("channel","project3");
		postData.addProperty("text",msg);
		
//		connection.setRequestProperty("Content-Length", "" + postData.toString().getBytes().length);
//		https://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/
		connection.setDoOutput(true);
		DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
		OutputStreamWriter outputStream = new OutputStreamWriter(dos,"UTF-8");
	    outputStream.write(postData.toString());
	    outputStream.flush();
	    outputStream.close();
	    
	    int responseCode = connection.getResponseCode();
	    logger.info("\nSending 'POST' request to URL : " + url);
	    logger.info("Post parameters : " + postData);
	    logger.info("Response Code : " + responseCode);
	    logger.debug("============Headers: =============");
		printHeaders(connection);
		logger.debug("============Body: =============");
		printBody(connection);
	    

	}

	public static void printHeaders(URLConnection connection) {
		Map<String,List<String>> headers = connection.getHeaderFields();
		for(String key: headers.keySet()) {
			logger.debug(key+":\t");
			List<String> values = headers.get(key);
			for(String value: values) {
				logger.debug("\t" + value);
			}
		}		
	}

	public static void printBody(URLConnection connection) throws IOException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(connection.getInputStream()));
		String line;
		while((line = reader.readLine()) != null) {
			logger.debug(line);
		}
		reader.close();
	}
	
	
	
}
