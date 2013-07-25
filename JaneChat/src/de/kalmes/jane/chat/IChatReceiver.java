package de.kalmes.jane.chat;

import de.uni_trier.jane.basetypes.Address;

/**
 * Created with IntelliJ IDEA.
 * User: Tobias
 * Date: 25.07.13
 * Time: 18:30
 * To change this template use File | Settings | File Templates.
 */
public interface IChatReceiver {
    public void chatUpdate(Address sender, String updatedChat);
}
