package Binary_Regions;

import java.awt.Color;
import java.util.List;

import ij.ImagePlus;
import ij.io.LogStream;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.regions.SegmentationRegionContour;
import imagingbook.pub.regions.BinaryRegionSegmentation.BinaryRegion;

/**
 * Shows each region's major axis as a vector scaled by the region's eccentricity.
 * Also demonstrates the use of the region property scheme, i.e., how
 * to assign numeric properties to regions and retrieve them afterwards.
 * @author W. Burger
 * @version 2015-12-03
 * @deprecated
 */
public class Major_Axis_Eccentricity_Demo implements PlugInFilter {
	
	static {LogStream.redirectSystem();}
	
	static double EccentricityScale = 10;
	static Color CenterColor = Color.magenta;
	static Color MajorAxisColor = Color.blue;
	
	private String title = null;

	public int setup(String arg, ImagePlus im) {
		title = im.getShortTitle();
		return DOES_8G + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		
		// perform region segmentation:
		SegmentationRegionContour segmenter = new SegmentationRegionContour((ByteProcessor) ip);
		List<BinaryRegion> regions = segmenter.getRegions(true);

		// calculate and register certain region properties:
		for (BinaryRegion r : regions) {
			calculateRegionProperties(r);
		}
		
		// create the output (color) image:
		ColorProcessor cp = ip.convertToColorProcessor();
		cp.add(128);
		
		// draw major axis vectors (scaled by eccentricity): 
		
		for (BinaryRegion r : regions) {
			if (r.getSize() > 10) {
				double theta = r.getProperty("theta");
				double ecc = r.getProperty("ecc1");
	//			double ecc = r.getProperty("ecc2");
				Pnt2d ctr = r.getCenter();
				double xc = ctr.getX();
				double yc = ctr.getY();
				int u0 = (int) Math.round(xc);
				int v0 = (int) Math.round(yc);
				int u1 = (int) Math.round(xc + EccentricityScale * ecc * Math.cos(theta));
				int v1 = (int) Math.round(yc + EccentricityScale * ecc * Math.sin(theta));
				
				drawCenter(cp,  u0,  v0);
				drawAxis(cp, u0, v0, u1, v1);
			}
		}
		
		// display the output image
		new ImagePlus(title + "-major-axis", cp).show();
	}
	
	private void calculateRegionProperties(BinaryRegion r) {
		// calculate central moment mu11, mu20, mu02:
		Pnt2d ctr = r.getCenter();
		double xc = ctr.getX();
		double yc = ctr.getY();
		double mu11 = 0;
		double mu20 = 0;
		double mu02 = 0;
		for (Pnt2d p : r) {
			double dx = (p.getX() - xc);
			double dy = (p.getY() - yc);
			mu11 = mu11 + dx * dy;
			mu20 = mu20 + dx * dx;
			mu02 = mu02 + dy * dy;
		}
		
		// calculate orientation of major axis
		r.setProperty("theta", 0.5 * Math.atan2(2 * mu11, mu20 - mu02));
		
		// calculate 2 versions of eccentricity
		double a = mu20 + mu02;
		double b = Math.sqrt(Math.pow(mu20 - mu02, 2) + 4 * mu11 * mu11);
		r.setProperty("ecc1", (a + b) / (a - b));
		r.setProperty("ecc2", (Math.pow(mu20 - mu02,  2) + 4 * mu11 * mu11) / Math.pow(mu20 + mu02, 2));
	}
	
	void drawCenter(ImageProcessor ip, int uc, int vc) {
		ip.setColor(CenterColor);
		ip.setLineWidth(1);
		ip.drawRect(uc - 2, vc - 2, 5, 5);
	}
	
	void drawAxis(ImageProcessor ip, int u0, int v0, int u1, int v1) {
		ip.setColor(MajorAxisColor);
		ip.setLineWidth(1);
		ip.drawLine(u0, v0, u1, v1);
	}

}