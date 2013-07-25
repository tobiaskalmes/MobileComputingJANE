package de.kalmes.jane.chat;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created with IntelliJ IDEA.
 * User: Tobias
 * Date: 25.07.13
 * Time: 22:14
 * To change this template use File | Settings | File Templates.
 */
public interface IChatHandler extends Remote {
    public void receiveMessage(String sender, String message) throws RemoteException;
}
