package de.kalmes.jane.chat;

/**
 * Created with IntelliJ IDEA.
 * User: Tobias
 * Date: 25.07.13
 * Time: 22:14
 * To change this template use File | Settings | File Templates.
 */
public interface IChatHandler {
    public void sendMessage(String sender, String receiver, String message);
}
