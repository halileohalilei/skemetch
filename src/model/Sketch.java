package model;

import java.util.ArrayList;
import java.util.List;

public class Sketch {
	private int noOfStrokes = 0;
	private List<List<SketchPoint>> points;

	public Sketch() {
		this.points = new ArrayList<List<SketchPoint>>();
	}

	public void incrementNoOfStrokes() {
		this.noOfStrokes++;
	}

	public List<List<SketchPoint>> getAllPoints() {
		return this.points;
	}

	public void addNewPoint(SketchPoint point) {
		try {
			if (this.points.size() == this.noOfStrokes) {
				this.points.add(new ArrayList<SketchPoint>());
			}

			this.points.get(this.noOfStrokes).add(point);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void clear() {
		for (int i = 0; i < this.noOfStrokes + 1; i++) {
			this.points.get(i).clear();
		}
		this.points.clear();
		this.noOfStrokes = 0;
	}

	@Override
	public Sketch clone() {
		Sketch newSketch = new Sketch();
		for (int i = 0; i < this.noOfStrokes + 1; i++) {
			newSketch.points.add(new ArrayList<SketchPoint>());
			for (int j = 0; j < this.points.get(i).size(); j++) {
				newSketch.points.get(i).add(this.points.get(i).get(j));
			}
		}
		newSketch.noOfStrokes = this.noOfStrokes;
		return newSketch;
	}
}
