package cs601.project3.chat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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
	public String sendMessage(String msg) throws IOException{
//		String encodedMsg = URLEncoder.encode(msg, "UTF-8");
		logger.debug("prepare to send message to slack: " + msg);
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
	    logger.debug("============slack response Headers: =============");
		boolean ok200 = printHeaders(connection);
		logger.debug("============slack reponse Body: =============");
		String errorMsg = printBody(connection);
		if(!ok200) {
			return "1";
		}
		if(!errorMsg.equals("0")) {
			return errorMsg;
		}
		return "0";
	}

	public static boolean printHeaders(URLConnection connection) {
		Map<String,List<String>> headers = connection.getHeaderFields();
		logger.debug(headers);
		List<String> statusList = headers.get(null);
		if(statusList != null && statusList.size() > 0 && "HTTP/1.1 200 OK".equals(statusList.get(0))) {
			return true;
		}
		return false;
//		for(String key: headers.keySet()) {
//			logger.debug(key+":\t");
//			List<String> values = headers.get(key);
//			for(String value: values) {
//				logger.debug("\t" + value);
//			}
//		}
	}

	public static String printBody(URLConnection connection) throws IOException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(connection.getInputStream()));
		StringBuffer sb = new StringBuffer();
		String line;
		while((line = reader.readLine()) != null) {
			logger.debug(line);
			sb.append(line).append("\n");
		}
		reader.close();
		
		String[] kvs = sb.toString().split(",");
		String errorMsg = null;
		boolean isError = false;
		for (String kvStr : kvs) {
			String[] kv = kvStr.split(":");
			if(kv[0].trim().equals("{\"ok\"") || kv[0].trim().equals("\"ok\"")) {
				if(kv[1].equals("false")) {
					isError = true;
				}
			}else if(kv[0].trim().equals("{\"error\"") || kv[0].trim().equals("\"error\"")) {
				errorMsg = kv[1];
			}
			if(isError && errorMsg != null) {
				return errorMsg;
			}
		}
		return "0";
	}
	
}
