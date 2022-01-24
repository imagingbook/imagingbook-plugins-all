/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Binary_Regions;

import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.geometry.basic.NeighborhoodType2D;
import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.regions.BinaryRegion;
import imagingbook.pub.regions.Contour;
import imagingbook.pub.regions.segment.RegionContourSegmentation;
import imagingbook.pub.regions.utils.ContourOverlay;
import imagingbook.pub.regions.utils.Display;

/**
 * This ImageJ plugin demonstrates the use of the class {@link RegionContourSegmentation}
 * to perform both region labeling and contour tracing simultaneously.
 * The resulting contours are displayed as a non-destructive vector overlay.
 * 
 * @author WB
 * @version 2020/12/20
 */
public class Region_Contours_Demo implements PlugInFilter {
	
	static NeighborhoodType2D NT = NeighborhoodType2D.N8;
	
	static boolean ListRegions = true;
	static boolean ListContours = true;
	static boolean ShowContours = true;
	
	public int setup(String arg, ImagePlus im) { 
		return DOES_8G + NO_CHANGES; 
	}
	
	public void run(ImageProcessor ip) {
	   	if (!getUserInput())
    		return;
	   	
	   	// Make sure we have a proper byte image:
	   	ByteProcessor bp = ip.convertToByteProcessor();
	   	
	   	// Create the region segmenter / contour tracer:
		RegionContourSegmentation seg = new RegionContourSegmentation(bp, NT);
		
		// Get the list of detected regions (sort by size):
		List<BinaryRegion> regions = seg.getRegions(true);
		if (regions == null || regions.isEmpty()) {
			IJ.showMessage("No regions detected!");
			return;
		}

		if (ListRegions) {
			IJ.log("\nDetected regions: " + regions.size());
			for (BinaryRegion R : regions) {
				IJ.log(R.toString());
			}
		}
		
		// Get the outer contour of the largest region:
		BinaryRegion Rmax = regions.get(0);
		Contour oc =  Rmax.getOuterContour();
		IJ.log("Points on outer contour of largest region:");
		for (Pnt2d p : oc) {
			IJ.log("Point " + p);
		}
	
			
		if (ListContours) {
			IJ.log("\nCountours:");
			for (BinaryRegion R : regions) {
				IJ.log("   " + R.toString());
				IJ.log("       " + oc);
				
				List<Contour> ics = R.getInnerContours();
				if (ics != null && !ics.isEmpty()) {
					for(Contour ic : R.getInnerContours()) {
						IJ.log("       " + ic);
					}
				}
			}
			
			
//			// Get the outer contour of the largest region:
//			BinaryRegion largestRegion = regions.get(0);
//			Contour oc =  largestRegion.getOuterContour();
//			IJ.log("Points along outer contour of largest region:");
//			Point[] points = oc.getPointArray();
//			for (int i = 0; i < points.length; i++) {
//				Point p = points[i];
//				IJ.log("Point " + i + ": " + p.toString());
//			}
//			
//			// Get all inner contours of the largest region:
//			List<? extends Contour> ics = largestRegion.getInnerContours();
//			IJ.log("Inner regions (holes): " + ics.size());
		}
		
		// Display the contours if desired:
		if (ShowContours) {
			ImageProcessor lip = Display.makeLabelImage(seg, false);
			ImagePlus lim = new ImagePlus("Region labels and contours", lip);
			Overlay oly = new ContourOverlay(seg);
			lim.setOverlay(oly);
			lim.show();
		}
	}
	
	// --------------------------------------------------------------------------
	
	private boolean getUserInput() {
		GenericDialog gd = new GenericDialog(Region_Contours_Demo.class.getSimpleName());
		gd.addEnumChoice("Neighborhood type", NT);
		gd.addCheckbox("List regions", ListRegions);
		gd.addCheckbox("List contours", ListContours);
		gd.addCheckbox("Show contours", ShowContours);
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		NT = gd.getNextEnumChoice(NeighborhoodType2D.class);
		ListRegions  = gd.getNextBoolean();
		ListContours = gd.getNextBoolean();
		ShowContours = gd.getNextBoolean();
		return true;
	}
}
