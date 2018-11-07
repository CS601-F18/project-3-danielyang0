package cs601.project3;
import cs601.project3.handler.ChatHandler;
import cs601.project3.tools.PropertyReader;

/**
 * slack chat application
 * @author yangzun
 *
 */
public class ChatApplication {
	public static void main(String[] args) {
		PropertyReader reader = new PropertyReader("./config","project3.properties");
		String BASE = reader.readStringValue("chatWebRoot", "webRoot");
		int port = reader.readIntValue("chatport", 1024);
		HTTPServer server = new HTTPServer(port, BASE);
		server.addMapping("/slackbot", new ChatHandler(BASE));
		server.startup();
	}
}
