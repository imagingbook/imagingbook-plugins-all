package _Delaunay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.pub.corners.Corner;
import imagingbook.pub.corners.HarrisCornerDetector;
import imagingbook.pub.geometry.basic.Point;
import imagingbook.pub.geometry.delaunay.DelaunayTriangulation;
import imagingbook.pub.geometry.delaunay.Triangle;
import imagingbook.pub.geometry.delaunay.guibas.TriangulationGuibas;


/**
 * This plugin runs the Harris corner detector, applies the Delaunay
 * triangulation to the N strongest corners and displays the result.
 * 
 * @author W. Burger
 * @version 2020-01-02
 */
public class Corners_Demo implements PlugInFilter {
	
	private static int CornerCount = 0;					// number of corners to show (0 = show all)
	private static Color DelaunayColor = Color.blue;	// color for graph edges
	private static Color PointColor = Color.magenta;		// color for point markers
	
	private static float StrokeWidth = 0.1f;
	private static double PointRadius = 1.5;
	
	private static String title = Corners_Demo.class.getSimpleName();
	

    public int setup(String arg, ImagePlus im) {
        return DOES_ALL + NO_CHANGES;
    }
    
    public void run(ImageProcessor ip) {
		
    	HarrisCornerDetector.Parameters params = new HarrisCornerDetector.Parameters();
		if (!showDialog(params)) {
			return;
		}
		
		HarrisCornerDetector cd = new HarrisCornerDetector(ip, params);
		List<Corner> corners = cd.findCorners();

		DelaunayTriangulation dt = new TriangulationGuibas(toPointList(corners));
		
		ImageProcessor cp = ip.convertToByteProcessor();
		Overlay oly = makeOverlay(dt);

		ImagePlus im = new ImagePlus(title, cp);
		im.setOverlay(oly);
		im.show();
    }
    
	private boolean showDialog(HarrisCornerDetector.Parameters params) {
		// display dialog , return false if canceled or on error.
		GenericDialog dlg = new GenericDialog("Harris Corner Detector");
		dlg.addNumericField("Alpha", params.alpha, 3);
		dlg.addNumericField("Threshold", params.tH, 0);
		dlg.addCheckbox("Clean up corners", params.doCleanUp);
		dlg.addNumericField("Corners to show (0 = show all)", CornerCount, 0);
		dlg.showDialog();
		if(dlg.wasCanceled())
			return false;	
		params.alpha = dlg.getNextNumber();
		params.tH = (int) dlg.getNextNumber();
		params.doCleanUp = dlg.getNextBoolean();
		CornerCount = (int) dlg.getNextNumber();
		if(dlg.invalidNumber()) {
			IJ.error("Input Error", "Invalid input number");
			return false;
		}	
		return true;
	}
	
	// ---------------------------------------------------------------------------
	
	/**
	 * TODO: Make class {@link Corner} implement {@link Point}.
	 * @param corners
	 * @return
	 */
	private List<Point> toPointList(List<Corner> corners) {
		Point[] points = new Point[corners.size()];
		int i = 0;
		for (Corner c : corners) {
			points[i] = new Point.Imp(c.getX(), c.getY());
			i++;
		}
		return Arrays.asList(points);
	}
	
	private Overlay makeOverlay(DelaunayTriangulation dt) {
		Collection<Triangle> triangles = dt.getTriangles();
		Collection<Point> allPoints = dt.getPoints();
		BasicStroke stroke = new BasicStroke(StrokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		Overlay oly = new Overlay();
	
		double r = PointRadius;
		for (Point p : allPoints) {
			double x = p.getX();
			double y = p.getY();
			Roi roi = new ShapeRoi(new Rectangle2D.Double(x - r, y - r, 2 * r, 2 * r));
			roi.setStroke(stroke);
			roi.setStrokeColor(PointColor);
			roi.setAntiAlias(true);
			oly.add(roi);
		}

		Path2D path = new Path2D.Double();
		for (Triangle trgl : triangles) {
			Point[] pts = trgl.getPoints();
			Point a = pts[0];
			Point b = pts[1];
			Point c = pts[2];
			path.moveTo(a.getX(), a.getY());
			path.lineTo(b.getX(), b.getY());
			path.lineTo(c.getX(), c.getY());
			path.lineTo(a.getX(), a.getY());
		}
		Roi roi = new ShapeRoi(path);
		roi.setStroke(stroke);
		roi.setStrokeColor(DelaunayColor);
		roi.setAntiAlias(true);
		oly.add(roi);	
		oly.translate(0.5, 0.5);
		return oly;
	}
	
}
