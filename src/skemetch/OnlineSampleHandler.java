package skemetch;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import tr.edu.ku.iui.features.Extractor;
import tr.edu.ku.iui.features.Feature;
import tr.edu.ku.iui.idm.IDMParameters;

import libsvm.*;
import model.SketchPoint;

public class OnlineSampleHandler {

	private final static IDMParameters param = new IDMParameters(4, 10.0, 50);
	private svm_model model;

	public static OnlineSampleHandler instance = new OnlineSampleHandler();

	// loads the pre-calculated SVM classifier
	public static void init() {
		try {
			svm.svm_load_model("classifier");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// saves the points of a sketch to an XML file.
	// it should be noted that this method takes a list of points WITHOUT their respective stroke counts
	public void saveInputSketch(List<SketchPoint> points) {
		System.out.println("SAVEINPUTSKETCH IS CALLED");
		try {
			Element sketch = new Element("sketch");
			sketch.setAttribute(new Attribute("id", UUID.randomUUID()
					.toString()));
			Document doc = new Document(sketch);
			doc.setRootElement(sketch);

			for (SketchPoint p : points) {
				Element point = new Element("point");
				point.setAttribute(new Attribute("id", p.id));
				point.setAttribute(new Attribute("x", String
						.format("%.2f", p.x)));
				point.setAttribute(new Attribute("y", String
						.format("%.2f", p.y)));
				point.setAttribute(new Attribute("time", String.format("%d",
						p.time)));
				point.setAttribute(new Attribute("pressure", String.format(
						"%.2f", p.pressure)));

				doc.getRootElement().addContent(point);
			}
			Element stroke = new Element("stroke");
			stroke.setAttribute(new Attribute("id", UUID.randomUUID()
					.toString()));
			stroke.setAttribute(new Attribute("visible", "true"));
			for (SketchPoint p : points) {
				Element arg = new Element("arg");
				arg.setAttribute(new Attribute("type", "point"));
				arg.setText(p.id);

				stroke.addContent(arg);
			}

			doc.getRootElement().addContent(stroke);

			XMLOutputter xmlOutput = new XMLOutputter();

			xmlOutput.setFormat(Format.getPrettyFormat());
			FileWriter writer = new FileWriter(
					"online/newSketch/online_sketch.xml");

			xmlOutput.output(doc, writer);
			writer.close();

			System.out.println("File Saved!");
		} catch (IOException io) {
			System.out.println(io.getMessage());
		}

	}

	// extracts features from the newly created XML file containing point data
	public double[] extract() throws IOException, JDOMException {
		Feature onlineSketch = null;
//		try
//		{
			List<Feature> samples = Extractor.extract("online/", param, ".xml");
			onlineSketch = samples.get(0);
//		} 
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
		return onlineSketch.getFeatureData();
	}

	public double classifySketch(double[] sketchData) throws IOException {
		if (model == null) {
			model = svm.svm_load_model("classifier");
		}

		svm_node[] features = new svm_node[sketchData.length];
		for (int i = 0; i < features.length; i++) {
			svm_node node = new svm_node();
			node.value = sketchData[i];
			node.index = i;

			features[i] = node;
		}

		return svm.svm_predict(model, features);
	}
}
