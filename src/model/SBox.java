package model;

import java.awt.Rectangle;
import java.awt.geom.Point2D.Float;
import java.util.ArrayList;
import java.util.List;

public class SBox implements Symbol {

	public Symbol leftSlot, rightSlot;

	public List<SArrow> pointsToThis = new ArrayList<SArrow>();
	public Rectangle position;
	public double mid;

	public SBox(Rectangle position) {
		this.position = (Rectangle) position.clone();
		this.mid = position.x + position.width / 2;
	}

	@Override
	public void detach() {
		if (pointsToThis != null) {
			for (SArrow currentPointer : pointsToThis) {
				currentPointer.end = null;
			}
		}
	}

	@Override
	public String generateSchemeCode() {
		/*
		 * Map<String, Symbol> map = SymbolContainer.instance
		 * .getSymbolsWithMultiplePointers(); String code = "(cons "; if
		 * (leftSlot == null) { code += "nil "; } else { if
		 * (map.containsValue(this)) { for (String key : map.keySet()) { if
		 * (map.get(key) == this) { code += key + " "; break; } } } }
		 * 
		 * if (rightSlot == null) { code += "nil"; } else { if
		 * (map.containsValue(this)) { for (String key : map.keySet()) { if
		 * (map.get(key) == this) { code += key; break; } } } } code += ")";
		 */
		return "(cons "
				+ (leftSlot == null ? "nil" : leftSlot.generateSchemeCode())
				+ " "
				+ (rightSlot == null ? "nil" : rightSlot.generateSchemeCode())
				+ ")";
	}

	public String toString() {
		return "Box";
	}

	public boolean insideLeftSlot(Float point) {
		return point.x < this.mid;
	}

	public Float getLeftCenter() {
		return new Float(this.position.x + this.position.width / 4,
				this.position.y + this.position.height / 2);
	}

	@Override
	public String getClassName() {
		return "box";
	}

	@Override
	public Rectangle getPosition() {
		return this.position;
	}

	public boolean setLeftSlot(Symbol s) {
		if (this.leftSlot == null) {
			this.leftSlot = s;
			return true;
		} else
			return false;
	}

	public boolean setRightSlot(Symbol s) {
		if (this.rightSlot == null) {
			this.rightSlot = s;
			return true;
		} else
			return false;
	}
}
