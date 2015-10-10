package model;

public class SketchPoint {

	public float x, y;
	public long time;
	public float pressure;
	public String id;

	@Override
	public String toString() {
		return String.format(
				"x: %4.2f y: %4.2f time: %d pressure: %4.2f id: %s\n", x, y,
				time, pressure, id);
	}
}
