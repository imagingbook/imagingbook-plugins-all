package Binary_Regions;

import static imagingbook.lib.math.Arithmetic.sqr;
import static java.lang.Math.sqrt;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.Locale;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.ij.IjUtils;
import imagingbook.lib.math.Eigensolver2x2;
import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.regions.BinaryRegionSegmentation.BinaryRegion;
import imagingbook.pub.regions.NeighborhoodType;
import imagingbook.pub.regions.SegmentationRegionContour;

/**
 * Performs binary region segmentation, then
 * displays each region's major axis (scaled by eccentricity)
 * and equivalent ellipse as a vector overlay.
 * Eccentricity values are limited to {@link #MaxEccentricity},
 * axes are marked red if exceeded.
 * Axes for regions with {@code NaN} eccentricity value (single-pixel regions) 
 * are not displayed.
 * Axis and ellipse parameters are calculated from the region's central
 * moments.
 * <br>
 * This plugin expects a binary (black and white) image with background = 0 and 
 * foreground &gt; 0.
 * 
 * @author W. Burger
 * @version 2021/04/18
 */
public class Region_Ellipse_Demo implements PlugInFilter {
	
	static {
		Locale.setDefault(Locale.US);
	}
	
	private static NeighborhoodType NhT = NeighborhoodType.N4;
	
	private static double 	AxisScale = 1.0;
	private static int 		MinRegionSize = 10;
	private static double 	MaxEccentricity = 100;
	
	private static Color 	AxisColor = Color.magenta;
	private static Color 	AxisColorVoid = Color.red;
	private static Color	MarkerColor = Color.orange;
	private static Color	EllipseColor = Color.green;
	
	private static double 	AxisLineWidth = 1.5;
	private static double 	MarkerRadius = 3;
	private static double 	MarkerLineWidth = 0.75;
	
	private static boolean 	ShowCenterMark = true;
	private static boolean 	ShowMajorAxis = true;
	private static boolean 	ShowEllipse = true;
	
	private ImagePlus im = null;

	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_8G;
	}

	public void run(ImageProcessor ip) {
		if (!IjUtils.isBinary(ip)) {
			IJ.showMessage("Plugin requires a binary image!");
			return;
		}
		
		if (!getUserInput()) {
			return;
		}
		
		int w = ip.getWidth();
		int h = ip.getHeight();
		double unitLength = sqrt(w * h) * 0.005 * AxisScale;
				
		Overlay oly = new Overlay();
		
		// perform region segmentation:
		SegmentationRegionContour segmenter = new SegmentationRegionContour((ByteProcessor) ip, NhT);
		List<BinaryRegion> regions = segmenter.getRegions();

		for (BinaryRegion r : regions) {
			int n = r.getSize();
			if (n < MinRegionSize) {
				continue;
			}
			
			Pnt2d ctr = r.getCenter();
			double xc = ctr.getX() + 0.5;
			double yc = ctr.getY() + 0.5;
			
			if (ShowCenterMark) {
				Roi marker = makeCenterMark(xc, yc, MarkerRadius);
				marker.setStrokeColor(MarkerColor);
				marker.setStrokeWidth(MarkerLineWidth);
				oly.add(marker);
			}
			
			double[] mu = r.getCentralMoments();	// = (mu10, mu01, mu20, mu02, mu11)
			double mu20 = mu[2];
			double mu02 = mu[3];
			double mu11 = mu[4];
			
			double theta = 0.5 * Math.atan2(2 * mu11, mu20 - mu02);	// axis angle	
				
			double A = mu20 + mu02;
			double B = sqr(mu20 - mu02) + 4 * sqr(mu11);
			if (B < 0) {
				throw new RuntimeException("negative B: " + B); // this should never happen
			}		
			double a1 = A + sqrt(B);		// see book eq. 10.34
			double a2 = A - sqrt(B);
			double ecc = a1 / a2;			// = (A + sqrt(B)) / (A - sqrt(B))				
			
			if (ShowMajorAxis && !Double.isNaN(ecc)) {
				// ignore single pixel regions: A + sqrt(B) == A - sqrt(B) == 0
				Color axisCol = AxisColor;			// default color
				if (ecc > MaxEccentricity) {		// limit eccentricity (may be infinite)
					ecc = MaxEccentricity;
					axisCol = AxisColorVoid;		// mark as beyond maximum
				}
				double len = ecc * unitLength;
				double dx = Math.cos(theta) * len;
				double dy = Math.sin(theta) * len;
				Roi roi = new ShapeRoi(new Line2D.Double(xc, yc, xc + dx, yc + dy));
				roi.setStrokeWidth(AxisLineWidth);
				roi.setStrokeColor(axisCol);
				oly.add(roi);
			}
			
			if (ShowEllipse) {
				double ra = sqrt(2 * a1 / n);
				double rb = sqrt(2 * a2 / n);
				Roi roi = makeEllipse(xc, yc, ra, rb, theta);
				roi.setStrokeWidth(AxisLineWidth);
				roi.setStrokeColor(EllipseColor);
				oly.add(roi);
			}
			
			// same result via Eigenvalues:
			Eigensolver2x2 es = new Eigensolver2x2(mu20, mu11, mu11, mu02);
			double lambda1 = es.getEigenvalue1();
			double lambda2 = es.getEigenvalue2();
			double ra2 = 2 * sqrt(lambda1 / n);
			double rb2 = 2 * sqrt(lambda2 / n);
			IJ.log(String.format("V2: ra=%.2f rb=%.2f", ra2, rb2));
			IJ.log(String.format("    a1=%.2f   lam1=%.2f  2*lam1=%.2f", a1, lambda1, 2 * lambda1));
			IJ.log(String.format("    a2=%.2f   lam2=%.2f  2*lam2=%.2f", a2, lambda2, 2 * lambda2));
			
			IJ.log("");
		}
		
		im.setOverlay(oly);
	}
	
	public Roi makeEllipse(double xc, double yc, double ra, double rb, double theta) {
		Ellipse2D e = new Ellipse2D.Double(-ra, -rb, 2 * ra, 2 * rb);
		AffineTransform t = new AffineTransform();
		t.translate(xc, yc);
		t.rotate(theta);
		return new ShapeRoi(t.createTransformedShape(e));
	}
	
	private Roi makeCenterMark(double x, double y, double r) {
		Path2D.Double m = new Path2D.Double();
		m.moveTo(x - r, y - r);
		m.lineTo(x + r, y + r);
		m.moveTo(x + r, y - r);
		m.lineTo(x - r, y + r);
		return new ShapeRoi(m);
	}
	
	// -----------------------------------------------------------------
	
	private boolean getUserInput() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addEnumChoice("Neighborhood type", NhT);
		gd.addNumericField("Min. region size", MinRegionSize, 0);
		gd.addNumericField("Max. eccentricity", MaxEccentricity, 0);
		gd.addNumericField("Axis scale", AxisScale, 1);
		gd.addCheckbox("Show center marks", ShowCenterMark);
		gd.addCheckbox("Show major axes", ShowMajorAxis);
		gd.addCheckbox("Show ellipses", ShowEllipse);
				
		gd.showDialog();
		if (gd.wasCanceled())
			return false;

		NhT = gd.getNextEnumChoice(NeighborhoodType.class);
		MinRegionSize = (int) gd.getNextNumber();
		MaxEccentricity = gd.getNextNumber();
		AxisScale = gd.getNextNumber();
		
		ShowCenterMark = gd.getNextBoolean();
		ShowMajorAxis = gd.getNextBoolean();
		ShowEllipse = gd.getNextBoolean();
		return true;
	}

}