package de.uni_trier.jane.visualization.shapes;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.visualization.*;

/**
 * @author langec
 * @see de.uni_trier.jane.visualization.shapes.GridShape
 * 
 * This shape displays a grid of hierarchically nested squares suitable for
 * visualizing the Grid Location Service. It is intended to fill the whole area
 * of visualization.
 */
public class HierarchicalGridShape implements Shape {
	private final Rectangle area;
	private final double order1SquareSize;
	private final int maxSquareOrder;
	private final Color minOrderColor;
	private final Color maxOrderColor;

	/**
	 * Creates a new HierarchicalGridShape. The squares of order 1 are painted
	 * in <code>minOrderColor</code>, the squares of maximum order are
	 * painted in <code>maxOrderColor</code>. All squares of orders in
	 * between are painted in combination colors (see
	 * <code>Color.mixColor</code>).
	 * 
	 * @param area
	 *            the area to be covered by the grid
	 * @param order1SquareSize
	 *            the size of an order-1 square (i.e. the smallest square)
	 * @param maxSquareOrder
	 *            the maximum possible order of squares, should be consistent
	 *            with <code>area</code> and <code>order1SquareSize</code>
	 * @param minOrderColor
	 *            the color for order-1 squares
	 * @param maxOrderColor
	 *            the color of the biggest possible squares
	 * @see Color#mixColor
	 */
	public HierarchicalGridShape(Rectangle area, double order1SquareSize, int maxSquareOrder, Color minOrderColor,
			Color maxOrderColor) {
		this.area = area;
		this.order1SquareSize = order1SquareSize;
		this.maxSquareOrder = maxSquareOrder;
		this.minOrderColor = minOrderColor;
		this.maxOrderColor = maxOrderColor;
	}

	/**
	 * Creates a new HierarchicalGridShape. The squares of order 1 are painted
	 * in <code>minOrderColor</code>, the squares of maximum order are
	 * painted in <code>maxOrderColor</code>. All squares of orders in
	 * between are painted in combination colors (see
	 * <code>Color.mixColor</code>).
	 * 
	 * @param area
	 *            the area to be covered by the grid
	 * @param order1SquareSize
	 *            the size of an order-1 square (i.e. the smallest square)
	 * @param minOrderColor
	 *            the color for order-1 squares
	 * @param maxOrderColor
	 *            the color of the biggest possible squares
	 * @see Color#mixColor
	 */
	public HierarchicalGridShape(Rectangle area, double order1SquareSize, Color minOrderColor, Color maxOrderColor) {
		this(area, order1SquareSize,
		/* calculate maxSquareOrder, see GlobalGrid constructor */
		(int) Math.ceil((Math.log(
		/* calculate numOrder1Squares */
		Math.ceil(Math.max(area.getTopRight().getX(), area.getBottomLeft().getY()) / order1SquareSize)) / Math.log(2))), minOrderColor,
				maxOrderColor);
	}

	/**
	 * @see de.uni_trier.jane.visualization.Shape#visualize(Position, Worldspace, DeviceIDPositionMap)
	 */
	public void visualize(Position pos, Worldspace worldspace, DeviceIDPositionMap addressPositionMap) {
		Matrix matrix = worldspace.getTransformation();
		Position position = pos.transform(matrix);
		Canvas canvas = worldspace.getCanvas(); //FIXME
		double squareSize = order1SquareSize;
		double divisor = maxSquareOrder - 1;
		for (int squareOrder = 1; squareOrder <= maxSquareOrder; squareOrder++) {
			Color currentColor = Color.mixColor(minOrderColor, maxOrderColor, (squareOrder - 1) / divisor);
			double nextSquareSize = 2 * squareSize;
			for (double x = squareSize; x <= area.getTopRight().getX(); x += nextSquareSize) {
				canvas.drawLine(new Position(x, 0), new Position(x, area.getTopRight().getY()), currentColor, 1);
			}
			for (double y = squareSize; y <= area.getTopRight().getY(); y += nextSquareSize) {
				canvas.drawLine(new Position(0, y), new Position(area.getTopRight().getX(), y), currentColor, 1);
			}
			squareSize = nextSquareSize;
		}
	}

    /**
     * TODO: this one is unused
     */
	public Rectangle getRectangle(Position position, Matrix matrix) {
		return area;
	}
}