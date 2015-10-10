package util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import model.Sketch;
import model.SketchPoint;
import model.Symbol;

public class ImageHandler {

	public static ImageHandler instance = new ImageHandler();

	HashMap<String, Image> images = new HashMap<String, Image>();

	private ImageHandler() {

	}

	private Image getImage(String imageName) {
		Image image = null;
		if (images.containsKey(imageName)) {
			image = images.get(imageName);
		} else {
			try {
				image = ImageIO.read(new File("img/" + imageName + ".png"));
				images.put(imageName, image);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return image;
	}

	public void drawSymbol(Graphics2D g2d, Symbol newSymbol) {
		Rectangle pos = newSymbol.getPosition();
		g2d.drawImage(getImage(newSymbol.getClassName()), pos.x, pos.y,
				pos.width, pos.height, null);
	}

	public void drawSketch(Graphics2D g2d, Sketch sketch, Symbol symbol) {

		List<List<SketchPoint>> allPoints = sketch.getAllPoints();
		Point2D.Float loc = new Float();
		Point2D.Float prevLoc = new Float();
		BasicStroke stroke;
		Color strokeColor = null;
		if (symbol.getClassName().equalsIgnoreCase("box")) {
			strokeColor = new Color(255, 0, 0, 255);
		} else if (symbol.getClassName().contains("arrow")) {
			strokeColor = new Color(0, 255, 0, 255);
		} else if (Character.isDigit(symbol.getClassName().charAt(0))) {
			strokeColor = new Color(0, 0, 255, 255);
		} else {
			strokeColor = new Color(255, 128, 0, 255);
		}
		for (int i = 0; i < allPoints.size(); i++) {
			List<SketchPoint> currentStroke = allPoints.get(i);
			loc.x = currentStroke.get(0).x;
			loc.y = currentStroke.get(0).y;
			for (int j = 1; j < currentStroke.size(); j++) {
				prevLoc.x = loc.x;
				prevLoc.y = loc.y;

				loc.x = currentStroke.get(j).x;
				loc.y = currentStroke.get(j).y;

				g2d.setColor(strokeColor);
				stroke = new BasicStroke(0.5f * 5, BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_MITER);
				g2d.setStroke(stroke);
				g2d.draw(new Line2D.Float(prevLoc, loc));
			}
		}
	}
}
