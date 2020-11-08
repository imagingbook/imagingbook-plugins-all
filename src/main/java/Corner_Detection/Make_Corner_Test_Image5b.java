package Corner_Detection;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Arrow;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.gui.PointRoi;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import imagingbook.lib.image.ImageGraphics;
import imagingbook.lib.math.Complex;
import imagingbook.lib.math.Matrix;
import imagingbook.pub.corners.Corner;
import imagingbook.pub.corners.GradientCornerDetector;
import imagingbook.pub.corners.HarrisCornerDetector;
import imagingbook.pub.corners.HarrisCornerDetector.Parameters;
import imagingbook.pub.corners.util.CornerOverlay;
import imagingbook.pub.geometry.basic.Point;
import imagingbook.pub.corners.subpixel.MaxLocator.Method;

/**
 * Creates a small image containing a single rotated rectangle.
 * @author WB
 *
 */
public class Make_Corner_Test_Image5b implements PlugIn {
	
	// size of the original image
	static int origWidth = 64;
	static int origHeight = 64;
	
	static double rectW = 30;
	static double rectH = 50;
	static double angle = 0.2;
	
	double cornerSize = 2;
	Color cornerColor = Color.green.darker();

	ByteProcessor ip;
	Shape rectR;

	@Override
	public void run(String arg) {

		ip = new ByteProcessor(origWidth, origHeight);
		ip.setColor(128);
		ip.fill();
		
		double xc = 0.5 * origWidth;
		double yc = 0.5 * origHeight;
		
		Rectangle2D rect = new Rectangle2D.Double(xc - rectW/2, yc - rectH/2, rectW, rectH);
		AffineTransform R = AffineTransform.getRotateInstance(angle, xc, yc);
		
		AffineTransform T = AffineTransform.getTranslateInstance(2.3, 0);
		rectR = R.createTransformedShape(rect);
		
		try (ImageGraphics ig = new ImageGraphics(ip)) {
			ig.setColor(255);
//			g.setLineWidth(0);
			Graphics2D g2 = ig.getGraphics();
			g2.fill(rectR);
		}
		
		runTest(Method.None);
		runTest(Method.QuadraticLeastSquares);


		//IJ.runPlugIn(Find_Corners_Harris.class.getName(), null);
	}
	
	
	
	// ---------------------------------------------------------------------------------------
	
	void runTest(Method subPixelMethod) {
		Parameters params = new Parameters();
		params.doCleanUp = false;
		params.border = 5;
		params.maxLocatorMethod = subPixelMethod; //Method.QuadraticLeastSquares; //Method.Quartic; //Method.Parabolic; //Method.None; //
		GradientCornerDetector cd = new HarrisCornerDetector(ip, params);
		List<Corner> corners = cd.getCorners();
		
		CornerOverlay.DefaultMarkerSize = 5;
		CornerOverlay oly = new CornerOverlay(); //new CornerOverlay();
//		oly.strokeColor(Color.green.darker());
//		oly.strokeWidth(2.0);
//		oly.addItems(corners);
		for (Corner c : corners) {
			ShapeRoi cross = this.makeCrossShape(c.getX(), c.getY());
			cross.setStrokeWidth(2);
			cross.setStrokeColor(cornerColor);
			oly.add(this.makeCrossShape(c.getX(), c.getY()));
		}
		
		
		// draw the true rectangle
//		List<Point> rectPoints = getPoints(rectR);
//		Roi roiR = makePolygon(rectPoints);
		Roi roiR = new ShapeRoi(rectR);
		roiR.setStrokeColor(Color.blue);
		roiR.setStrokeWidth(0.25);
		oly.add(roiR);
			
		// find the closest corner for each rectangle point
		List<Point> cornerPoints = new LinkedList<>();
		List<Point> rectPoints = getPoints(rectR);
		for (int i = 0; i < 4; i++) {
			Point p = rectPoints.get(i);
			Corner c = findClosest(p, corners);
			cornerPoints.add(c);
		}
			
		// create the detected corner polygon
		Roi roiC = makePolygon(cornerPoints);
		roiC.setStrokeColor(Color.red);
		roiC.setStrokeWidth(0.25);
		oly.add(roiC);
		
		
		ImagePlus im = new ImagePlus(subPixelMethod.name(), ip);
		im.setOverlay(oly);
		im.show();
	}
	
	
	// ---------------------------------------------------------------------------------------
	
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
	
	// ---------------------------------------------------------------------------
	
	ShapeRoi makeCrossShape(double xc, double yc) {
		Path2D path = new Path2D.Double();
		path.moveTo(xc - cornerSize, yc);
		path.lineTo(xc + cornerSize, yc);
		path.moveTo(xc, yc - cornerSize);
		path.lineTo(xc, yc + cornerSize);
		return new ShapeRoi(path);
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
