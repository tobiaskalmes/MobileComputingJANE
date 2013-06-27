package de.uni_trier.jane.service.routing.multicast.spbm;

import de.uni_trier.jane.basetypes.Position;

class Grid {
	/* Grids are immutable */
	Grid parent = null;

	int part = 0; // 1 ... 4

	double minx = 0;

	double maxx = 0;

	double miny = 0;

	double maxy = 0;

	public Grid(double minx, double maxx, double miny, double maxy) {
		this.minx = minx;
		this.maxx = maxx;
		this.miny = miny;
		this.maxy = maxy;
	}

	public Grid(Grid parent, int part) {
		this.part = part;
		this.parent = parent;
		if (part == 1) {
			minx = parent.minx;
			miny = parent.miny;
			maxx = (parent.maxx - parent.minx) / 2 + parent.minx;
			maxy = (parent.maxy - parent.miny) / 2 + parent.miny;
		}
		if (part == 2) {
			maxx = parent.maxx;
			miny = parent.miny;
			minx = (parent.maxx - parent.minx) / 2 + parent.minx;
			maxy = (parent.maxy - parent.miny) / 2 + parent.miny;
		}
		if (part == 3) {
			maxx = parent.maxx;
			maxy = parent.maxy;
			minx = (parent.maxx - parent.minx) / 2 + parent.minx;
			miny = (parent.maxy - parent.miny) / 2 + parent.miny;
		}
		if (part == 4) {
			minx = parent.minx;
			maxy = parent.maxy;
			maxx = (parent.maxx - parent.minx) / 2 + parent.minx;
			miny = (parent.maxy - parent.miny) / 2 + parent.miny;
		}
	}

	public boolean equals(Object o) {
		Grid g = (Grid) o;
		if (g.minx == minx && g.maxx == maxx && g.miny == miny && g.maxy == maxy)
			return true;
		return false;
	}

	public boolean isInside(Grid g) {
        if (g==null)return false;
		if (g.minx >= minx && g.maxx <= maxx && g.miny >= miny && g.maxy <= maxy)
			return true;
		return false;
	}

	public boolean isInside(Position p) {
		if (p.getX() >= minx && p.getX() <= maxx && p.getY() >= miny && p.getY() <= maxy)
			return true;
		return false;
	}

	public String toString() {
		if (part == 0)
			return "";
		if (parent != null) {
			return parent.toString() + part;
		} else {
			return Integer.toString(part);
		}
	}

	public static Grid posToGrid(Grid root, Position p, int depth) {
		if (depth == 0)
			return root;
		for (int i = 1; i <= 4; i++) {
			Grid g = new Grid(root, i);
			if (g.isInside(p))
				return posToGrid(g, p, depth - 1);
		}
		return null;
	}

	public int getDepth() {
		if (parent == null) {
			return 0;
		} else {
			return parent.getDepth() + 1;
		}
	}

	public Grid getAtDepth(int d) {
		int mydepth = getDepth();
		if (d > mydepth)
			return null;
		if (d == mydepth)
			return this;
		return parent.getAtDepth(d);
	}
}
