package Binary_Regions;

import static imagingbook.lib.math.Arithmetic.sqr;
import static java.lang.Math.sqrt;

import java.awt.Color;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Arrow;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.ij.IjUtils;
import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.regions.BinaryRegionSegmentation.BinaryRegion;
import imagingbook.pub.regions.SegmentationRegionContour;

/**
 * Shows each region's equivalent ellipse.
 * Requires a binary (segmented) image.
 * 
 * @author W. Burger
 * @version 2021/04/18
 */
public class Region_Eccentricity_Demo implements PlugInFilter {
	
	static Color MajorAxisColor = Color.green;
	static double MajorAxisLineWidth = 1.0;
	static int MinRegionSize = 0;
	
	private ImagePlus im = null;
	private double AxisLength = 50;

	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_8G;
	}

	public void run(ImageProcessor ip) {
		if (!IjUtils.isBinary(ip)) {
			IJ.showMessage("Plugin requires a binary image!");
			return;
		}
		
		int w = ip.getWidth();
		int h = ip.getHeight();
		AxisLength = (w + h) * 0.01;
		Overlay oly = new Overlay();
		
		// perform region segmentation:
		SegmentationRegionContour segmenter = new SegmentationRegionContour((ByteProcessor) ip);
		List<BinaryRegion> regions = segmenter.getRegions(true);

		
		for (BinaryRegion r : regions) {
			if (r.getSize() < MinRegionSize) {
				continue;
			}
			// calculate region properties:
			double[] mu = getCentralMoments(r);
			double mu20 = mu[0];
			double mu02 = mu[1];
			double mu11 = mu[2];
			
			double theta = 0.5 * Math.atan2(2 * mu11, mu20 - mu02);	// orientation angle	
			
			double A = mu20 + mu02;
			double B = sqr(mu20 - mu02) + 4 * sqr(mu11);
			if (B <= 0) {
				IJ.log("negative B: " + B);
			}
			double ecc = (A + sqrt(B)) / (A - sqrt(B));
			ecc = sqrt(ecc);
			
			IJ.log("A = " + A);
			IJ.log("B = " + B);
			IJ.log("A - sqrt(B) = " + (A - sqrt(B)));
			IJ.log("ecc = " + ecc);
			
			double dx = Math.cos(theta) * AxisLength * ecc;
			double dy = Math.sin(theta) * AxisLength * ecc;
			
			Pnt2d ctr = r.getCentroid();
			double xc = ctr.getX();
			double yc = ctr.getY();
			
			Roi roi = new Line(xc, yc, xc + dx, yc + dy);	// Arrow
			roi.setStrokeWidth(MajorAxisLineWidth);
			roi.setStrokeColor(MajorAxisColor);
			oly.add(roi);			
			oly.add(new PointRoi(xc, yc));		// mark the region's center
		}
		
		this.im.setOverlay(oly);
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

}