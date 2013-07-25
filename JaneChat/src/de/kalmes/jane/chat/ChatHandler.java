package de.kalmes.jane.chat;

import de.uni_trier.jane.basetypes.Address;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Tobias
 * Date: 25.07.13
 * Time: 18:22
 * To change this template use File | Settings | File Templates.
 */
public class ChatHandler {
    private static final int PORT = 10042;
    private Map<Address, String> chatLogMap;
    private IChatReceiver        receiver;
    private ServerSocket         serverSocket;
    private Socket               socket;
    private InputStream          in;
    private OutputStream         out;

    public ChatHandler(IChatReceiver receiver) throws IOException {
        this.receiver = receiver;
        chatLogMap = new HashMap<Address, String>();
        serverSocket = new ServerSocket(PORT);
        socket = serverSocket.accept();
        in = socket.getInputStream();
        out = socket.getOutputStream();
        //TODO: check for exceptions
        //Start listener
        Thread thread = new Thread() {
            @Override
            public void run() {
            }
        };
        thread.start();
    }

    public String initializeChat(Address receiver) {
        String returnValue;
        if (!chatLogMap.containsKey(receiver)) {
            chatLogMap.put(receiver, "");
        }
        returnValue = chatLogMap.get(receiver);

        //init chat

        return returnValue;
    }
}
