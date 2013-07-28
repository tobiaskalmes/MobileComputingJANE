package de.kalmes.jane.chat;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created with IntelliJ IDEA.
 * User: Tobias
 * Date: 25.07.13
 * Time: 18:22
 * To change this template use File | Settings | File Templates.
 */
public class ChatHandler extends UnicastRemoteObject implements IChatHandler {
    private IMessageReceiver messageReceiver;
    private String           lastChatPartner;
    private IChatHandler     chatPartner;
    private String           ownAddress;

    public ChatHandler() throws RemoteException {
        super();
    }

    public void setOwnAddress(String ownAddress) {
        this.ownAddress = ownAddress;
    }

    public void addMessageReceiver(IMessageReceiver messageReceiver) {
        this.messageReceiver = messageReceiver;
    }

    public void sendMessage(String receiver, String message) {
        //RMI send
        try {
            if (lastChatPartner == null || !lastChatPartner.equals(receiver)) {
                chatPartner = (ChatHandler) Naming.lookup(receiver);
                lastChatPartner = receiver;
            }
            chatPartner.receiveMessage(ownAddress, message);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void receiveMessage(String sender, String message) throws RemoteException {
        messageReceiver.receiveMessage(sender, message);
    }
}
