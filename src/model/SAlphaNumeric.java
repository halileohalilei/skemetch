package model;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.ArrayList;
import java.util.List;

public class SAlphaNumeric implements Symbol {

	public String value;
	public List<Symbol> pointsToThis = new ArrayList<Symbol>();

	public Rectangle position;
	public Point2D.Float center;

	public SAlphaNumeric(Rectangle position, int label) {
		this.value = SymbolFactory.instance.getClassString(label);
		this.position = position;
		this.center = new Float(this.position.x + this.position.width / 2,
				this.position.y + this.position.height / 2);
	}

	@Override
	public void detach() {
		if (pointsToThis != null) {
			for (Symbol currentPointer : pointsToThis) {
				if (currentPointer instanceof SArrow) {
					((SArrow) currentPointer).end = null;
				} else {
					if (((SBox) currentPointer).leftSlot == this) {
						((SBox) currentPointer).leftSlot = null;
					} else {
						((SBox) currentPointer).rightSlot = null;
					}
				}
			}
		}
	}

	public boolean isCharacter() {
		return Character.isLetter(this.value.charAt(0));
	}

	@Override
	public String generateSchemeCode() {
		return (isCharacter() ? "'" : "") + this.value;
	}

	public String toString() {
		return (isCharacter() ? "'" : "") + this.value;
	}

	@Override
	public String getClassName() {
		return value;
	}

	@Override
	public Rectangle getPosition() {
		return this.position;
	}
}
