package de.kalmes.jane.chat;

import de.kalmes.jane.dsdv.DSDVService;
import de.uni_trier.jane.basetypes.Address;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Set;
import java.util.Vector;

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
    private DSDVService dsdvService;

    public ChatGUI(String ownAddress, DSDVService dsdvService) throws RemoteException {
        super("JANEChat - You are " + ownAddress);
        this.dsdvService = dsdvService;
        chatHandler = new ChatHandler();
        chatHandler.addMessageReceiver(this);
        chatHandler.setOwnAddress(ownAddress);
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

        //refresh neighbors
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vector<String> devices = getReachableDevices();
                receiverChooser.setModel(new DefaultComboBoxModel(devices));
                repaint();
                System.out.println("Reachable Devices updated.");
            }
        });
        chatPanel.add(refresh, BorderLayout.NORTH);

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
                inputTextArea.setText("");
            }
        });
        inputPanel.add(sendButton, BorderLayout.SOUTH);
        add(inputPanel, BorderLayout.SOUTH);
    }

    private Vector<String> getReachableDevices() {
        Vector<String> devices = new Vector<String>();
        Set<Address> reachables = dsdvService.getAllReachableDevices();
        for (Address a : reachables) {
            devices.add(a.toString());
        }
        return devices;
    }

    @Override
    public void receiveMessage(String sender, String message) {
        chatLog.append(sender + ": " + message + "\n");
    }
}
