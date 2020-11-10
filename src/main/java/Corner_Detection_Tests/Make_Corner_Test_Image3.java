package Corner_Detection_Tests;

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
import imagingbook.pub.corners.subpixel.SubpixelMaxInterpolator.Method;
import imagingbook.pub.corners.util.CornerOverlay;
import imagingbook.pub.geometry.basic.Point;

public class Make_Corner_Test_Image3 implements PlugIn {
	
	static int width = 512;
	static int height = 512;
	
	static int Nx = 5;
	static int Ny = 5;


	@Override
	public void run(String arg) {

		ByteProcessor ip = new ByteProcessor(width, height);
		ip.setColor(128);
		ip.fill();
		
		int N = Nx * Ny;
		
		double dx = (double) width / (Nx + 1);
		double dy = (double) height / (Ny + 1);
		
		double rectW = 30;
		double rectH = 50;
		
		double deltaAngle = Math.PI / 2 / N;
		
		List<Shape> rectangles = new LinkedList<>();
		
		try (ImageGraphics g = new ImageGraphics(ip)) {
			g.setColor(255);
//			g.setLineWidth(0);
			Graphics2D g2 = g.getGraphics();
			
			int k = 0;
			for (int j = 0; j < Ny; j++) {
				double y = dy * (j + 1);
				for (int i = 0; i < Nx; i++) {
					double x = dx * (i + 1);
					Shape rect = new Rectangle2D.Double(x - rectW/2, y - rectH/2, rectW, rectH);
					double angle = deltaAngle * k;
					AffineTransform R = AffineTransform.getRotateInstance(angle, x, y);
					Shape rectR = R.createTransformedShape(rect);
					rectangles.add(rectR);
					//listPoints(rectR);
					g2.fill(rectR);
					k++;
				}
			}
		}
		
		Parameters params = new Parameters();
		params.doCleanUp = false;
		params.maxLocatorMethod = Method.QuadraticLeastSquares; //Method.Quartic; //Method.Parabolic; //Method.None; //
		GradientCornerDetector cd = new HarrisCornerDetector(ip, params);
		List<Corner> corners = cd.getCorners();
		
//		CornerOverlay.DefaultMarkerSize = 3;
		CornerOverlay oly = new CornerOverlay();
		oly.strokeColor(Color.green);
		oly.strokeWidth(0.25);
		oly.addItems(corners);
		
		
		// iterate over all rectangles
		for (Shape r : rectangles) {
			//listPoints(r);
			// create the true rectangle polygon
			List<Point> rectPoints = getPoints(r);
			Roi roiR = makePolygon(rectPoints);
			roiR.setStrokeColor(Color.blue);
			roiR.setStrokeWidth(0.25);
			oly.addRoi(roiR);
			
			// find the closest corner for each rectangle point
			List<Point> cornerPoints = new LinkedList<>();
			for (int i = 0; i < 4; i++) {
				Point p = rectPoints.get(i);
				Corner c = findClosest(p, corners);
				cornerPoints.add(c);
			}
			
			// create the detected corner polygon
			Roi roiC = makePolygon(cornerPoints);
			roiC.setStrokeColor(Color.red);
			roiC.setStrokeWidth(0.25);
			oly.addRoi(roiC, true);
		}
		
		
		ImagePlus im = new ImagePlus("Corner Test", ip);
		im.setOverlay(oly);
		im.show();
		

		//IJ.runPlugIn(Find_Corners_Harris.class.getName(), null);
	}
	
	Shape makeTriangle(double x0, double y0, double x1, double y1, double x2, double y2) {
		Path2D poly = new Path2D.Double();	
		poly.moveTo(x0, y0);
		poly.lineTo(x1, y1);
		poly.lineTo(x2, y2);
		poly.closePath();
		return poly;
	}
	
	void listPoints(Shape s) {
		IJ.log("rectangle: ");
		PathIterator pi = s.getPathIterator(null);
		double[] coords = new double[6];
		int i = 0;
		while (!pi.isDone()) {
			pi.currentSegment(coords);
			IJ.log("  " + Matrix.toString(coords));
			pi.next();
			i++;
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
