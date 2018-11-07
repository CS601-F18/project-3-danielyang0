package cs601.project3;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.Test;

import cs601.project3.chat.ChatClient;

public class ChatTest {
	
	//Manually check if the msg have been sent to channel
	@Test
	public void test(){
		ChatClient cc = new ChatClient();
		String sendMessage = null;
		try {
			sendMessage = cc.sendMessage("adsfds");
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue(sendMessage.equals("0"));
	}
}
