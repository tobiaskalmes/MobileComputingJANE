package de.kalmes.jane.chat;

import de.kalmes.jane.dsdv.DSDVService;

/**
 * Created with IntelliJ IDEA.
 * User: Tobias
 * Date: 25.07.13
 * Time: 18:22
 * To change this template use File | Settings | File Templates.
 */
public class ChatHandler implements IChatHandler {
    private DSDVService dsdvService;

    public ChatHandler(DSDVService dsdvService) {
        this.dsdvService = dsdvService;
    }

    @Override
    public void sendMessage(String sender, String receiver, String message) {
        dsdvService.sendMessage(sender, receiver, message);
    }
}
