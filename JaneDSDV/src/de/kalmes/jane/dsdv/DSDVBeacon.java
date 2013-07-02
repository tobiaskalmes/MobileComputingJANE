package de.kalmes.jane.dsdv;

import de.uni_trier.jane.basetypes.ClassDataID;
import de.uni_trier.jane.basetypes.Data;
import de.uni_trier.jane.basetypes.DataID;

/**
 * Created with IntelliJ IDEA.
 * User: Tobias
 * Date: 02.07.13
 * Time: 14:24
 * To change this template use File | Settings | File Templates.
 */
public class DSDVBeacon implements Data {
    public static DataID id = new ClassDataID(DSDVBeacon.class);
    public BeaconContent content;


    public DSDVBeacon(BeaconContent content) {
        super();
        this.content = content;
    }

    @Override
    public DataID getDataID() {
        return id;
    }

    @Override
    public int getSize() {
        return 1024;
    }
}
