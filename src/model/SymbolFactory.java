package model;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;


public class SymbolFactory {

	public static SymbolFactory instance = new SymbolFactory();


	private static List<String> types;
	
	{
		types = new ArrayList<String>();

		types.add("arrow_east");
		types.add("arrow_north");
		types.add("arrow_northeast");
		types.add("arrow_northwest");
		types.add("arrow_south");
		types.add("arrow_southeast");
		types.add("arrow_southwest");
		types.add("arrow_west");

		types.add("box");

		types.add("a");
		types.add("b");
		types.add("c");
		types.add("d");
		types.add("e");
		types.add("f");
		types.add("g");
		types.add("h");
		types.add("i");
		types.add("j");
		types.add("k");
		types.add("l");
		types.add("m");
		types.add("n");
		types.add("o");
		types.add("p");
		types.add("q");
		types.add("r");
		types.add("s");
		types.add("t");
		types.add("u");
		types.add("v");
		types.add("w");
		types.add("x");
		types.add("y");
		types.add("z");

		types.add("0");
		types.add("1");
		types.add("2");
		types.add("3");
		types.add("4");
		types.add("5");
		types.add("6");
		types.add("7");
		types.add("8");
		types.add("9");
	}

	public Symbol getNewSymbol(int label, List<SketchPoint> points) {

		if (label < 8) {
			return new SArrow(findPosition(points), label);
		} else if (label == 8) {
			return new SBox(findPosition(points));
		} else {
			return new SAlphaNumeric(findPosition(points), label);
		}

	}
	
	public String getClassString(int label){
		return types.get(label);
	}

	private Rectangle findPosition(List<SketchPoint> points) {
		Rectangle pos = new Rectangle();
		double minX = 9999;
		double minY = 9999;
		double maxX = 0;
		double maxY = 0;

		for (SketchPoint point : points) {
			if (point.x > maxX)
				maxX = point.x;
			if (point.y > maxY)
				maxY = point.y;
			if (point.x < minX)
				minX = point.x;
			if (point.y < minY)
				minY = point.y;
		}

		pos.x = (int) minX;
		pos.y = (int) minY;
		pos.width = (int) (maxX - minX);
		pos.height = (int) (maxY - minY);
		
		return pos;
	}

}
