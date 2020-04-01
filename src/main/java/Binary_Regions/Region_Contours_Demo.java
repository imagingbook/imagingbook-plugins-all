/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Binary_Regions;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.regions.Contour;
import imagingbook.pub.regions.ContourOverlay;
import imagingbook.pub.regions.RegionContourLabeling;
import imagingbook.pub.regions.RegionLabeling.BinaryRegion;

import imagingbook.pub.geometry.basic.Point;
import java.util.List;

/**
 * This ImageJ plugin demonstrates the use of the class {@link RegionContourLabeling}
 * to perform both region labeling and contour tracing simultaneously.
 * The resulting contours are displayed as a non-destructive vector overlay.
 * 
 * @author WB
 * @version 2020/04/01
 */
public class Region_Contours_Demo implements PlugInFilter {
	
	static boolean ListRegions = true;
	static boolean ListContourPoints = false;
	static boolean ShowContours = true;
	
	public int setup(String arg, ImagePlus im) { 
		return DOES_8G + NO_CHANGES; 
	}
	
	public void run(ImageProcessor ip) {
	   	if (!getUserInput())
    		return;
	   	
	   	// Make sure we have a proper byte image:
	   	ByteProcessor I = ip.convertToByteProcessor();
	   	
	   	// Create the region labeler / contour tracer:
		RegionContourLabeling seg = new RegionContourLabeling(I);
		
		// Get the list of detected regions (sort by size):
		List<BinaryRegion> regions = seg.getRegions(true);
		if (regions.isEmpty()) {
			IJ.error("No regions detected!");
			return;
		}

		if (ListRegions) {
			IJ.log("Detected regions: " + regions.size());
			for (BinaryRegion r: regions) {
				IJ.log(r.toString());
			}
		}
			
		if (ListContourPoints) {
			// Get the outer contour of the largest region:
			BinaryRegion largestRegion = regions.get(0);
			Contour oc =  largestRegion.getOuterContour();
			IJ.log("Points along outer contour of largest region:");
			Point[] points = oc.getPointArray();
			for (int i = 0; i < points.length; i++) {
				Point p = points[i];
				IJ.log("Point " + i + ": " + p.toString());
			}
			
			// Get all inner contours of the largest region:
			List<Contour> ics = largestRegion.getInnerContours();
			IJ.log("Inner regions (holes): " + ics.size());
		}
		
		
		// Display the contours if desired:
		if (ShowContours) {
			ImageProcessor lip = seg.makeLabelImage(false);
			ImagePlus lim = new ImagePlus("Region labels and contours", lip);
			Overlay oly = new ContourOverlay(seg);
			lim.setOverlay(oly);
			lim.show();
		}
	}
	
	boolean getUserInput() {
		GenericDialog gd = new GenericDialog(Region_Contours_Demo.class.getSimpleName());
		gd.addCheckbox("List regions", ListRegions);
		gd.addCheckbox("List contour points", ListContourPoints);
		gd.addCheckbox("Show contours", ShowContours);
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		ListRegions = gd.getNextBoolean();
		ListContourPoints = gd.getNextBoolean();
		ShowContours = gd.getNextBoolean();
		return true;
	}
}
