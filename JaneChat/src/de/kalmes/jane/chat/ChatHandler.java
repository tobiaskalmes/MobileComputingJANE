package de.kalmes.jane.chat;

/**
 * Created with IntelliJ IDEA.
 * User: Tobias
 * Date: 25.07.13
 * Time: 18:22
 * To change this template use File | Settings | File Templates.
 */
public class ChatHandler implements IChatHandler {
    private ChatService chatService;

    public ChatHandler(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public void sendMessage(String sender, String receiver, String message) {
        chatService.sendMessage(sender, receiver, message);
    }
}
