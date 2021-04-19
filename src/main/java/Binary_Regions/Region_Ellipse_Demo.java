package Binary_Regions;

import static imagingbook.lib.math.Arithmetic.sqr;
import static java.lang.Math.sqrt;

import java.awt.Color;
import java.awt.Shape;
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
 * Displays each region's equivalent ellipse as a vector overlay.
 * Infinite eccentricity values (e.g., from straight lines) is limited to
 * {@link #MaxEccentricity} and marked red.
 * {@code NaN} eccentricity values (caused by single-pixel regions) are not displayed.
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
	private static Color 	AxisColor = Color.green;
	private static Color 	AxisColorMax = Color.red;
	private static Color	MarkerColor = Color.yellow;
	private static double 	MarkerLineWidth = 0.5;
	private static double 	MarkerRadius = 1.5;
	private static double 	AxisLineWidth = 0.5;
	private static double 	AxisScale = 1.0;
	private static int 		MinRegionSize = 10;
	private static double 	MaxEccentricity = 100;
	
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
			if (r.getSize() < MinRegionSize) {
				continue;
			}
			
			Pnt2d ctr = r.getCentroid();
			double xc = ctr.getX() + 0.5;
			double yc = ctr.getY() + 0.5;
			//oly.add(new PointRoi(xc, yc));		// mark the region's center
			
			Roi marker = makeMarker(xc, yc, MarkerRadius);
			marker.setStrokeColor(MarkerColor);
			marker.setStrokeWidth(MarkerLineWidth);
			oly.add(marker);
			
			double[] mu = getCentralMoments(r);
			double mu20 = mu[0];
			double mu02 = mu[1];
			double mu11 = mu[2];
			
			double theta = 0.5 * Math.atan2(2 * mu11, mu20 - mu02);	// orientation angle	
			
			double A = mu20 + mu02;
			double B = sqr(mu20 - mu02) + 4 * sqr(mu11);
			if (B < 0) {
				throw new RuntimeException("negative B: " + B);
			}
			
			double a1 = A + sqrt(B);		// see book eq. 10.34
			double a2 = A - sqrt(B);
			//double ecc = (A + sqrt(B)) / (A - sqrt(B));
			double ecc = a1 / a2;
					
			if (Double.isNaN(ecc)) {	// single pixel region: A + sqrt(B) == A - sqrt(B) == 0
				continue;
			}
				
			Color axisCol = AxisColor;			// default color
			if (ecc > MaxEccentricity) {		// limit eccentricity (may be infinite)
				ecc = MaxEccentricity;
				axisCol = AxisColorMax;			// mark as beyond maximum
			}
			
			double len = ecc * unitLength;
			double dx = Math.cos(theta) * len;
			double dy = Math.sin(theta) * len;
			
						
			Roi roi = new ShapeRoi(new Line2D.Double(xc, yc, xc + dx, yc + dy));
			roi.setStrokeWidth(AxisLineWidth);
			roi.setStrokeColor(axisCol);
			oly.add(roi);
			
			int n = r.getSize();
			double ra1 = sqrt(2 * a1 / n);
			double rb1 = sqrt(2 * a2 / n);
			IJ.log(String.format("V1: ra=%.2f rb=%.2f", ra1, rb1));
			
			Roi re = makeEllipse(xc, yc, ra1, rb1, theta);
			oly.add(re);
			
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
	
	/**
	 * Calculate the region's central moments mu20, mu02, mu11.
	 * @param r the binary region
	 * @return a double array with mu20, mu02, mu11
	 */
	private double[] getCentralMoments(BinaryRegion r) {	
		final Pnt2d xctr = r.getCentroid();
		final double xc = xctr.getX();
		final double yc = xctr.getY();
		double mu11 = 0;
		double mu20 = 0;
		double mu02 = 0;
		for (Pnt2d p : r) {
			double dx = (p.getX() - xc);
			double dy = (p.getY() - yc);
			mu20 = mu20 + dx * dx;
			mu02 = mu02 + dy * dy;
			mu11 = mu11 + dx * dy;
		}
		return new double[] {mu20, mu02, mu11};
	}
	
	public Roi makeEllipse(double xc, double yc, double ra, double rb, double theta) {
		Ellipse2D e = new Ellipse2D.Double(-ra, -rb, 2 * ra, 2 * rb);
		AffineTransform t = new AffineTransform();
		t.translate(xc, yc);
		t.rotate(theta);
		return new ShapeRoi(t.createTransformedShape(e));
	}
	
	private Roi makeMarker(double x, double y, double r) {
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
		
		gd.showDialog();
		if (gd.wasCanceled())
			return false;

		NhT = gd.getNextEnumChoice(NeighborhoodType.class);
		MinRegionSize = (int) gd.getNextNumber();
		MaxEccentricity = gd.getNextNumber();
		AxisScale = gd.getNextNumber();
		return true;
	}

}