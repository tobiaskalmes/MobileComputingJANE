import de.uni_trier.jane.basetypes.Dispatchable;
import de.uni_trier.jane.basetypes.Extent;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.network.link_layer.LinkLayerMessage;
import de.uni_trier.jane.signaling.SignalListener;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.RectangleShape;
import de.uni_trier.jane.visualization.shapes.Shape;


public class TestMessage implements LinkLayerMessage {

	private String content;


	public TestMessage(String content) {
		super();
		this.content = content;
	}

	@Override
	public void handle(LinkLayerInfo info, SignalListener listener) {
		String sender = info.getSender().toString();

		//Dieser Handler wird beim Erhalt einer Nachricht aufgerufen
		((TestService)listener).handleMessage(sender, content);

	}

	@Override
	public Dispatchable copy() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public Class getReceiverServiceClass() {
		// TODO Auto-generated method stub
		return TestService.class;
	}

	//Symbol f�r eine Nachricht in der GUI
	//Ist die Datenrate im Netz zu hoch kann es passieren, dass man das Symbol nicht sieht. Zeitraffer in der GUI heruntersetzen
	@Override
	public Shape getShape() {
		return new RectangleShape(new Extent(10,10), Color.RED,false);
	}

	//Gesch�tzte Nachrichtengr��e :)
	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 1024;
	}

}
