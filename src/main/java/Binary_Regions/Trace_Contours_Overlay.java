/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/
package Binary_Regions;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Overlay;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.regions.ContourOverlay;
import imagingbook.pub.regions.RegionContourLabeling;
import imagingbook.pub.regions.RegionLabeling.BinaryRegion;

import java.awt.Color;
import java.util.List;

/**
 * This plugin implements the combined contour tracing and 
 * component labeling algorithm as described in  Chang, Chun-Jen: 
 * "A Component-Labeling Algorithm Using Contour Tracing 
 * Technique", Proc. ICDAR03, p. 741-75, IEEE Comp. Soc., 2003.
 * It uses the ContourTracer class to create lists of points 
 * representing the internal and external contours of each region in
 * the binary image.  Instead of drawing directly into the image, 
 * we make use of ImageJ's Overlay class to draw the contours 
 * in a separate vector layer on top of the image.
 * 2012-08-06: adapted to new 'regions' package.
 * 2014-11-12: updated overlay generation.
*/
public class Trace_Contours_Overlay implements PlugInFilter {
	
	static float strokeWidth = 0.5f;
	static Color outerColor = Color.green;
	static Color innerColor = Color.blue;
	
	ImagePlus origImage = null;
	String origTitle = null;
	static boolean verbose = true;
	
	public int setup(String arg, ImagePlus im) { 
    	origImage = im;
		origTitle = im.getTitle();
		return DOES_8G + NO_CHANGES; 
	}
	
	public void run(ImageProcessor ip) {
		ByteProcessor I = ip.convertToByteProcessor();
		//  label regions and trace contours
		RegionContourLabeling segmenter = new RegionContourLabeling(I);
		
		// extract contours and regions
		List<BinaryRegion> regions = segmenter.getRegions();
		if (verbose) printRegions(regions);

		// change lookup-table to show gray regions
		I.setMinAndMax(0, 512);
		
		// create an image with overlay to show the contours
		Overlay oly = new ContourOverlay(segmenter);
		ImagePlus im2 = new ImagePlus("Contours of " + origTitle, I);
		im2.setOverlay(oly);
		im2.show();
	}
	
	void printRegions(List<BinaryRegion> regions) {
		for (BinaryRegion r: regions) {
			IJ.log(r.toString());
		}
	}
}
