package cs601.project3;
import cs601.project3.handler.ChatHandler;
import cs601.project3.tools.PropertyReader;

public class ChatApplication {
	public static void main(String[] args) {
		PropertyReader reader = new PropertyReader("./config","httpconfig.properties");
		int port = reader.readIntValue("chatport", 1024);
		HTTPServer server = new HTTPServer(port);
		server.addMapping("/slackbot", new ChatHandler());
		server.startup();
	}
}
