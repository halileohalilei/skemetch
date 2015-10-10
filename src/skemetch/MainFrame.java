package skemetch;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

import model.SAlphaNumeric;
import model.SArrow;
import model.SBox;
import model.Sketch;
import model.SketchPoint;
import model.Symbol;
import model.SymbolFactory;

import org.jdom.JDOMException;

import util.ImageHandler;
import util.SymbolContainer;

import jpen.PLevel;
import jpen.PLevelEvent;
import jpen.PenManager;
import jpen.event.PenAdapter;
import jpen.owner.multiAwt.AwtPenToolkit;

public class MainFrame extends PenAdapter {

	Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

	Stack<List<SketchPoint>> states = new Stack<List<SketchPoint>>();

	JPanel panel = new JPanel();
	JFrame frame = new JFrame("Skemetch");

	JPanel buttonPanel = new JPanel();
	JPanel buttonPanelTop = new JPanel();
	JButton undo = new JButton("Undo");
	JButton clear = new JButton("Clear");
	JButton mode = new JButton("Change mode");

	JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	JPanel textPanel = new JPanel();
	JTextField schemeCodeText = new JTextField();
	JTextArea log = new JTextArea();

	Graphics2D g2d;
	BasicStroke stroke;

	Image box;

	float brushSize;
	float opacity;

	final double recognitionDelay = 1.2;

	Point2D.Float loc = new Point2D.Float();
	Point2D.Float prevLoc = new Point2D.Float();

	List<SketchPoint> points = new ArrayList<SketchPoint>();

	Sketch currentSketch = new Sketch();

	boolean hasNewData = false;
	boolean sketchMode = true;

	Timer timer = new Timer((int) Math.round(1000 * recognitionDelay),
			new PenActionListener());

	private class PenActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			OnlineSampleHandler.instance.saveInputSketch(points);
			try {
				double[] features = OnlineSampleHandler.instance.extract();
				double label = OnlineSampleHandler.instance
						.classifySketch(features);

				Symbol newSymbol = SymbolFactory.instance.getNewSymbol(
						(int) label, points);
				log.append("Recognized symbol: " + newSymbol.getClassName()
						+ "\n");
				if (SymbolContainer.instance.getSize() > 0) {
					if (newSymbol instanceof SArrow) {
						boolean symbolAssigned = false;
						for (Symbol s : SymbolContainer.instance
								.getAllSymbols()) {
							if (s instanceof SBox
									&& ((SBox) s).position
											.contains(((SArrow) newSymbol)
													.getStartPoint())) {
								if (((SBox) s)
										.insideLeftSlot(((SArrow) newSymbol)
												.getStartPoint())) {
									if (!((SBox) s).setLeftSlot(newSymbol)) {
										onInvalidSketch();
										return;
									}
								} else {
									if (!((SBox) s).setRightSlot(newSymbol)) {
										onInvalidSketch();
										return;
									}
								}
								symbolAssigned = true;
								((SArrow) newSymbol).start = (SBox) s;
								break;
							}
						}
						if (!symbolAssigned) {
							onInvalidSketch();
							return;
						}
					} else if (newSymbol instanceof SBox) {
						SArrow closestArrow = null;
						double minDist = 100;
						for (Symbol s : SymbolContainer.instance
								.getAllSymbols()) {
							if (s instanceof SArrow) {
								double distance = ((SArrow) s).getEndPoint()
										.distance(
												((SBox) newSymbol)
														.getLeftCenter());
								if (distance < minDist) {
									minDist = distance;
									closestArrow = (SArrow) s;
								}
							}
						}
						if (closestArrow == null || closestArrow.end != null
								|| minDist >= 100) {
							onInvalidSketch();
							return;
						}
						((SArrow) closestArrow).end = newSymbol;
						((SBox) newSymbol).pointsToThis.add(closestArrow);
					} else if (newSymbol instanceof SAlphaNumeric) {
						boolean symbolAssigned = false;
						double minDist = 100;
						SArrow closestArrow = null;
						for (Symbol s : SymbolContainer.instance
								.getAllSymbols()) {
							if (s instanceof SBox
									&& ((SBox) s).position
											.contains(((SAlphaNumeric) newSymbol).center)) {
								symbolAssigned = true;
								if (((SBox) s)
										.insideLeftSlot(((SAlphaNumeric) newSymbol).center)) {
									if (!((SBox) s).setLeftSlot(newSymbol)) {
										onInvalidSketch();
										return;
									}
								} else {
									if (!((SBox) s).setRightSlot(newSymbol)) {
										onInvalidSketch();
										return;
									}
								}
								((SAlphaNumeric) newSymbol).pointsToThis.add(s);
								break;
							} else if (s instanceof SArrow) {
								double distance = ((SArrow) s)
										.getEndPoint()
										.distance(
												((SAlphaNumeric) newSymbol).center);
								if (distance < minDist) {
									minDist = distance;
									closestArrow = (SArrow) s;
								}
							}
						}
						if (!symbolAssigned) {
							if (closestArrow != null
									&& closestArrow.end == null
									&& minDist < 100) {
								((SAlphaNumeric) newSymbol).pointsToThis
										.add(closestArrow);
								((SArrow) closestArrow).end = newSymbol;
							} else {
								onInvalidSketch();
								return;
							}
						}
					}
				}

				SymbolContainer.instance.addNewSymbol(newSymbol);
				SymbolContainer.instance.addNewSketch(currentSketch.clone());
				drawEverything();
				currentSketch.clear();
				points.clear();

				schemeCodeText.setText(SymbolContainer.instance
						.generateSchemeCode());
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (JDOMException e1) {
				e1.printStackTrace();
			}
			hasNewData = false;
		}
	}

	private class ClearActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, panel.getWidth(), panel.getHeight());

			SymbolContainer.instance.clearAllSymbols();
			SymbolContainer.instance.clearAllSketches();
			hasNewData = false;
			schemeCodeText.setText("");
			log.append("Cleared all symbols.\n");
		}

	}

	private class UndoActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			int size = SymbolContainer.instance.getSize();
			if (size > 0) {
				Symbol s = SymbolContainer.instance.removeLast();
				log.append("Undo: erased symbol " + s.getClassName() + "\n");
				schemeCodeText.setText(SymbolContainer.instance
						.generateSchemeCode());
			} else {
				schemeCodeText.setText("");
			}
			hasNewData = false;

			drawEverything();
		}
	}

	private void onInvalidSketch() {
		log.append("Invalid sketch!\n");
		points.clear();
		currentSketch.clear();
		drawEverything();
		hasNewData = false;
	}

	private void drawEverything() {
		//frame.repaint();
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, panel.getWidth(), panel.getHeight());
		//g2d.clearRect(0, 0, panel.getWidth(), panel.getHeight());
		

		if (sketchMode) {
			for (int i = 0; i < SymbolContainer.instance.getSize(); i++) {
				ImageHandler.instance.drawSketch(g2d, SymbolContainer.instance
						.getAllSketches().get(i), SymbolContainer.instance
						.getAllSymbols().get(i));
			}
		} else {
			for (Symbol s : SymbolContainer.instance.getAllSymbols()) {
				ImageHandler.instance.drawSymbol(g2d, s);
			}
		}
	}

	{
		OnlineSampleHandler.init();

		clear.addActionListener(new ClearActionListener());
		undo.addActionListener(new UndoActionListener());
		mode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sketchMode = !sketchMode;
				drawEverything();
			}
		});

		timer.setRepeats(false);

		schemeCodeText.setEditable(false);
		schemeCodeText.setFont(new Font("Courier", Font.PLAIN, 20));
		schemeCodeText.setHorizontalAlignment(JTextField.CENTER);

		textPanel.setPreferredSize(new Dimension(d.width, 100));
		textPanel.setLayout(new GridLayout());
		textPanel.add(schemeCodeText);
		frame.add(textPanel, BorderLayout.SOUTH);

		buttonPanel.setPreferredSize(new Dimension(250, d.height));
		buttonPanel.setLayout(new BorderLayout());
		buttonPanelTop.setLayout(new GridLayout());
		buttonPanelTop.add(undo);
		buttonPanelTop.add(clear);
		buttonPanel.add(mode, BorderLayout.SOUTH);

		buttonPanel.add(buttonPanelTop, BorderLayout.NORTH);

		JScrollPane scroll = new JScrollPane(log);
		log.setEditable(false);
		log.setWrapStyleWord(true);
		log.setColumns(15);
		buttonPanel.add(scroll, BorderLayout.CENTER);

		splitPane.setPreferredSize(new Dimension(2, 478));
		buttonPanel.add(splitPane, BorderLayout.EAST);

		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.white);
		frame.add(buttonPanel, BorderLayout.WEST);

		frame.add(panel);
		frame.setSize(1050, 600);
		frame.setResizable(false);
		frame.setVisible(true);

		panel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

		PenManager pm = AwtPenToolkit.getPenManager();
		pm.pen.levelEmulator.setPressureTriggerForLeftCursorButton(0.5f);

		g2d = (Graphics2D) panel.getGraphics();

		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		AwtPenToolkit.addPenListener(panel, this);

	}

	@Override
	public void penLevelEvent(PLevelEvent ev) {
		if (!ev.isMovement()) {
			if (hasNewData) {
				if (!timer.isRunning()) {
					timer.start();
				}
			}
			return;
		}

		float pressure = ev.pen.getLevelValue(PLevel.Type.PRESSURE);

		if (pressure > 0) {
			hasNewData = true;
			if (timer.isRunning()) {
				timer.stop();
				currentSketch.incrementNoOfStrokes();
			}
		} else {
			if (hasNewData) {
				if (!timer.isRunning()) {
					timer.start();
					// currentSketch.incrementNoOfStrokes();
				}
			}
		}

		brushSize = pressure * 5;
		opacity = pressure * 255;

		loc.x = ev.pen.getLevelValue(PLevel.Type.X);
		loc.y = ev.pen.getLevelValue(PLevel.Type.Y);

		if (brushSize > 0) {
			g2d.setColor(new Color((int) opacity, (int) opacity, (int) opacity,
					255));
			stroke = new BasicStroke(brushSize, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_MITER);
			g2d.setStroke(stroke);
			g2d.draw(new Line2D.Float(prevLoc, loc));
		}

		prevLoc.setLocation(loc);
		if (pressure > 0
				&& panel.getBounds().contains(
						new Point2D.Float(loc.x + 250, loc.y))) {
			SketchPoint point = new SketchPoint();
			point.x = loc.x;
			point.y = loc.y;
			point.pressure = pressure;
			point.time = System.currentTimeMillis();
			point.id = UUID.randomUUID().toString();

			currentSketch.addNewPoint(point);
			points.add(point);
		}
	}

}