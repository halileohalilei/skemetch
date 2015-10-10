package model;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

public class SArrow implements Symbol {

	int type;

	private static final int EAST = 0;
	private static final int NORTH = 1;
	private static final int NORTHEAST = 2;
	private static final int NORTHWEST = 3;
	private static final int SOUTH = 4;
	private static final int SOUTHEAST = 5;
	private static final int SOUTHWEST = 6;
	private static final int WEST = 7;

	public SBox start;
	public Symbol end;

	public Rectangle position;

	public SArrow(Rectangle position, int label) {
		this.position = position;
		this.type = label;
	}
	
	public  int isCloseEnough(Symbol s){
		return Integer.MAX_VALUE;
	}

	@Override
	public void detach() {
		if (start != null) {
			if (start.leftSlot == this) {
				start.leftSlot = null;
			} else {
				start.rightSlot = null;
			}
		}
	}

	@Override
	public String generateSchemeCode() {
		return end == null ? "nil" : end.generateSchemeCode();
	}

	public Point2D.Float getEndPoint() {
		int x = position.x;
		int y = position.y;
		int w = position.width;
		int h = position.height;

		switch (this.type) {
		case EAST:
			return new Point2D.Float(x + w, y + h / 2);
		case NORTH:
			return new Point2D.Float(x + w / 2, y);
		case NORTHEAST:
			return new Point2D.Float(x + w, y);
		case NORTHWEST:
			return new Point2D.Float(x, y);
		case SOUTH:
			return new Point2D.Float(x + w / 2, y + h);
		case SOUTHEAST:
			return new Point2D.Float(x + w, y + h);
		case SOUTHWEST:
			return new Point2D.Float(x, y + h);
		case WEST:
			return new Point2D.Float(x, y + h / 2);
		}

		return null;
	}

	public Point2D.Float getStartPoint() {
		int x = position.x;
		int y = position.y;
		int w = position.width;
		int h = position.height;

		switch (this.type) {
		case EAST:
			return new Point2D.Float(x, y + h / 2);
		case NORTH:
			return new Point2D.Float(x + w / 2, y + h);
		case NORTHEAST:
			return new Point2D.Float(x, y + h);
		case NORTHWEST:
			return new Point2D.Float(x + w, y + h);
		case SOUTH:
			return new Point2D.Float(x + w / 2, y);
		case SOUTHEAST:
			return new Point2D.Float(x, y);
		case SOUTHWEST:
			return new Point2D.Float(x + w, y);
		case WEST:
			return new Point2D.Float(x + w, y + h / 2);
		}
		return null;
	}

	public String toString() {
		return "Arrow";
	}

	@Override
	public String getClassName() {
		return SymbolFactory.instance.getClassString(this.type);
	}

	@Override
	public Rectangle getPosition() {
		return this.position;
	}

}
