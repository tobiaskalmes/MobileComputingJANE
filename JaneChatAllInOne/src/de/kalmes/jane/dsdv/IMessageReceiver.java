package de.kalmes.jane.dsdv;

/**
 * Created with IntelliJ IDEA.
 * User: Tobias
 * Date: 25.07.13
 * Time: 22:18
 * To change this template use File | Settings | File Templates.
 */
public interface IMessageReceiver {
    public void receiveMessage(String sender, String chatLog);
}
