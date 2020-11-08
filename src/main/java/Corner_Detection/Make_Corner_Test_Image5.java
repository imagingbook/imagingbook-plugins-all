package Corner_Detection;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import imagingbook.lib.image.ImageGraphics;
import imagingbook.lib.math.Matrix;
import imagingbook.pub.corners.Corner;
import imagingbook.pub.corners.GradientCornerDetector;
import imagingbook.pub.corners.HarrisCornerDetector;
import imagingbook.pub.corners.HarrisCornerDetector.Parameters;
import imagingbook.pub.corners.subpixel.MaxLocator.Method;
import imagingbook.pub.corners.util.CornerOverlay;
import imagingbook.pub.geometry.basic.Point;

/**
 * Creates a small image containing a single rotated rectangle.
 * @author WB
 *
 */
public class Make_Corner_Test_Image5 implements PlugIn {
	
	// size of the original image
	static int origWidth = 64;
	static int origHeight = 64;
	
	static int angleDeg = 20; //40; //10; //30; //20; //11;
	
	static double rectW = 30;
	static double rectH = 50;
	
	double angle = angleDeg / 180.0 * Math.PI;
	
	double cornerSize = 1.5;
	double cornerStrokeWidth = 0.25;
	Color cornerColor = Color.blue;
	
	Color renderBoxColor = Color.red;
	Color cornerBoxColor = Color.green.darker();

	Rectangle2D rectOrig;
	Shape rectRotated;
	ByteProcessor ip;
	
	@Override
	public void run(String arg) {
		
		IJ.log("angle = " + (angle / Math.PI * 180));

		ip = new ByteProcessor(origWidth, origHeight);
		ip.setColor(128);
		ip.fill();
		
		double xc = 0.5 * origWidth;
		double yc = 0.5 * origHeight;
		
		rectOrig = new Rectangle2D.Double(xc - rectW/2, yc - rectH/2, rectW, rectH);
		AffineTransform R = AffineTransform.getRotateInstance(angle, xc, yc);
		
//		AffineTransform T = AffineTransform.getTranslateInstance(2.3, 0);
		rectRotated = R.createTransformedShape(rectOrig);
		
		try (ImageGraphics ig = new ImageGraphics(ip)) {
			ig.setColor(255);
//			g.setLineWidth(0);
			Graphics2D g2 = ig.getGraphics();
			g2.fill(rectRotated);
		}
		
		runTest(Method.QuadraticTaylor); //Method.QuadraticLeastSquares; //Method.Quartic; //Method.Parabolic; //Method.None; //
		runTest(Method.Quartic);
		runTest(Method.None);
	}
	
	void runTest(Method subPixMethod) {
		Parameters params = new Parameters();
		params.doCleanUp = false;
		params.border = 2;
		params.maxLocatorMethod = subPixMethod;
		GradientCornerDetector cd = new HarrisCornerDetector(ip, params);
		List<Corner> corners = cd.getCorners();
		
		CornerOverlay.DefaultMarkerSize = 3;
		CornerOverlay oly = new CornerOverlay();
//		oly.strokeColor(Color.green);
//		oly.strokeWidth(1.0);
		//oly.addItems(corners);

		// draw the true rectangle
//		List<Point> rectPoints = getPoints(rectR);
//		Roi roiR = makePolygon(rectPoints);
		Roi roiR = new ShapeRoi(rectRotated);
		roiR.setStrokeColor(renderBoxColor);
		roiR.setStrokeWidth(0.25);
		oly.addRoi(roiR);
			
		// find the closest corner for each rectangle point
		List<Point> cornerPoints = new LinkedList<>();
		List<Point> rectPoints = getPoints(rectRotated);
		for (int i = 0; i < 4; i++) {
			Point p = rectPoints.get(i);
			Corner c = findClosest(p, corners);
			cornerPoints.add(c);
		}
			
		// create the detected corner polygon
		Roi roiC = makePolygon(cornerPoints);
		roiC.setStrokeColor(cornerBoxColor);
		roiC.setStrokeWidth(0.25);
		oly.addRoi(roiC, true);
		
		
		// add corners (on top)
		for (Corner c : corners) {
			oly.addRoi(makeCrossShape(c.getX(), c.getY()), true);
		}
		
		ImagePlus im = new ImagePlus("RectCorners-" + angleDeg + "-" + subPixMethod.name(), ip);
		im.setOverlay(oly);
		im.show();
	}
	
	// ---------------------------------------------------------------------
	
	ShapeRoi makeCrossShape(double xc, double yc) {
		Path2D path = new Path2D.Double();
		path.moveTo(xc - cornerSize, yc);
		path.lineTo(xc + cornerSize, yc);
		path.moveTo(xc, yc - cornerSize);
		path.lineTo(xc, yc + cornerSize);
		ShapeRoi cross = new ShapeRoi(path);
		cross.setStrokeWidth(cornerStrokeWidth);
		cross.setStrokeColor(cornerColor);
		return cross;
	}
	
	void listPoints(Shape s) {
		IJ.log("rectangle: ");
		PathIterator pi = s.getPathIterator(null);
		double[] coords = new double[6];
		while (!pi.isDone()) {
			pi.currentSegment(coords);
			IJ.log("  " + Matrix.toString(coords));
			pi.next();
		}
	}
	
	List<Point> getPoints(Shape s) {
		List<Point> points = new LinkedList<>();
		PathIterator pi = s.getPathIterator(null);
		double[] coords = new double[6];
		while (!pi.isDone()) {
			pi.currentSegment(coords);
			points.add(Point.create(coords[0], coords[1]));
			pi.next();
		}
		return points;
	}
	
	Corner findClosest(Point p, List<Corner> corners) {
		Corner minCorner = null;
		double minDist = Double.POSITIVE_INFINITY;
		for (Corner c : corners) {
			double dist2 = p.distance2(c);
			if (dist2 < minDist) {
				minCorner = c;
				minDist = dist2;
			}
		}
		return minCorner;
	}
	
	ShapeRoi makePolygon(List<Point> points) {
		Point[] pa = points.toArray(new Point[0]);
		Path2D poly = new Path2D.Double();
		poly.moveTo(pa[0].getX(), pa[0].getY());
		for (int i=1; i<pa.length; i++) {
			poly.lineTo(pa[i].getX(), pa[i].getY());
		}
		poly.closePath();
		return new ShapeRoi(poly);
	}
	
//	Roi makePolygon(List<Point> points) {
//		Point[] pointA = points.toArray(new Point[0]);
//		
//		float[] xPoints = new float[pointA.length];
//		float[] yPoints = new float[pointA.length];
//		
//		for (int i = 0; i < pointA.length; i++) {
//			xPoints[i] = (float) pointA[i].getX();
//			yPoints[i] = (float) pointA[i].getY();
//		}
//		PolygonRoi pRoi = new PolygonRoi(xPoints, yPoints, Roi.POLYLINE);
//		
//		return pRoi;
//	}

}