package de.kalmes.jane.chat;

import javax.swing.*;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created with IntelliJ IDEA.
 * User: Tobias
 * Date: 25.07.13
 * Time: 17:51
 * To change this template use File | Settings | File Templates.
 */
public class test {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            Naming.rebind("ChatHandler", new ChatHandler());
            JFrame frame = new ChatGUI("192.168.2.13");
            frame.setVisible(true);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
