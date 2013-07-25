package de.kalmes.jane.chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

/**
 * Created with IntelliJ IDEA.
 * User: Tobias
 * Date: 25.07.13
 * Time: 17:42
 * To change this template use File | Settings | File Templates.
 */
public class ChatGUI extends JFrame implements IMessageReceiver {
    private ChatHandler chatHandler;
    private JTextArea   chatLog;
    private JComboBox   receiverChooser;
    private JTextArea   inputTextArea;

    public ChatGUI(String clientAddress) throws RemoteException {
        super("JANEChat - You are " + clientAddress);
        chatHandler = new ChatHandler();
        chatHandler.addMessageReceiver(this);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(500, 400);

        //chat log area
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatLog = new JTextArea(10, 40);
        chatLog.setLineWrap(true);
        chatLog.setWrapStyleWord(true);
        JScrollPane chatScroller = new JScrollPane(chatLog, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                                                   ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        chatLog.setEditable(false);
        chatPanel.add(chatScroller, BorderLayout.CENTER);
        add(chatPanel, BorderLayout.NORTH);

        //input area
        JPanel inputPanel = new JPanel(new BorderLayout());
        receiverChooser = new JComboBox();
        inputPanel.add(receiverChooser, BorderLayout.NORTH);
        inputTextArea = new JTextArea(3, 40);
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        JScrollPane inputScroller = new JScrollPane(inputTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                                                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        inputPanel.add(inputScroller, BorderLayout.CENTER);
        JButton sendButton = new JButton("Senden");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chatLog.append("You: " + inputTextArea.getText() + "\n");
                chatHandler.sendMessage((String) receiverChooser.getSelectedItem(), inputTextArea.getText());
            }
        });
        inputPanel.add(sendButton, BorderLayout.SOUTH);
        add(inputPanel, BorderLayout.SOUTH);
    }

    @Override
    public void receiveMessage(String sender, String message) {
        chatLog.append(sender + ": " + message + "\n");
    }
}
