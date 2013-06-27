package de.uni_trier.jane.service.planarizer.rdg;

public class PartialTriangulationFactory {

	public static PartialTriangulation getPartialTriangulation() {
		return new PartialTriangulationImpl();
	}

}
