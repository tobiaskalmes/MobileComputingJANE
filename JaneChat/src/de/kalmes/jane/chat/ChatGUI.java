package de.kalmes.jane.chat;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Tobias
 * Date: 25.07.13
 * Time: 17:42
 * To change this template use File | Settings | File Templates.
 */
public class ChatGUI extends JFrame {
    private ChatHandler chatHandler;

    public ChatGUI(ChatHandler handler, String clientAddress) {
        super("JANEChat - You are " + clientAddress);
        this.chatHandler = handler;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(500, 400);

        //chat log area
        JPanel chatPanel = new JPanel(new BorderLayout());
        JTextArea chatLog = new JTextArea(10, 40);
        chatLog.setLineWrap(true);
        chatLog.setWrapStyleWord(true);
        JScrollPane chatScroller = new JScrollPane(chatLog, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                                                   ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        chatLog.setEditable(false);
        chatPanel.add(chatScroller, BorderLayout.CENTER);
        add(chatPanel, BorderLayout.NORTH);

        //input area
        JPanel inputPanel = new JPanel(new BorderLayout());
        JComboBox receiverChooser = new JComboBox();
        inputPanel.add(receiverChooser, BorderLayout.NORTH);
        JTextArea inputTextArea = new JTextArea(3, 40);
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        JScrollPane inputScroller = new JScrollPane(inputTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                                                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        inputPanel.add(inputScroller, BorderLayout.CENTER);
        JButton sendButton = new JButton("Senden");
        inputPanel.add(sendButton, BorderLayout.SOUTH);
        add(inputPanel, BorderLayout.SOUTH);
    }
}
