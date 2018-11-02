package cs601.project3.chat;

public class ChatClientTest {
	public static void main(String[] args) {
		ChatClient chatClient = new ChatClient();
		try {
			chatClient.sendMessage("msg");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
