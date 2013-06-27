package de.kalmes.jane.twohop;

import de.uni_trier.jane.basetypes.ClassDataID;
import de.uni_trier.jane.basetypes.Data;
import de.uni_trier.jane.basetypes.DataID;


public class TestBeacon implements Data {

    public static DataID id = new ClassDataID(TestBeacon.class);
    public BeaconContent content;


    public TestBeacon(BeaconContent content) {
        super();
        this.content = content;
    }

    @Override
    public DataID getDataID() {
        // TODO Auto-generated method stub
        return id;
    }

    @Override
    public int getSize() {
        // TODO Auto-generated method stub
        return 1024;
    }

}
