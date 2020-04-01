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
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.util.Enums;
import imagingbook.pub.geometry.basic.Point;
import imagingbook.pub.regions.BreadthFirstLabeling;
import imagingbook.pub.regions.DepthFirstLabeling;
import imagingbook.pub.regions.RecursiveLabeling;
import imagingbook.pub.regions.RegionContourLabeling;
import imagingbook.pub.regions.RegionLabeling;
import imagingbook.pub.regions.RegionLabeling.BinaryRegion;
import imagingbook.pub.regions.SequentialLabeling;

/**
 * This ImageJ plugin is an example for how to use the region
 * labeling classes in the "regions" package:
 * {@link BreadthFirstLabeling},
 * {@link DepthFirstLabeling},
 * {@link RecursiveLabeling},
 * {@link SequentialLabeling},
 * {@link RegionContourLabeling}.
 * One of four labeling types can be selected (see the {@code run()} method).
 * All should produce the same results.
 * 
 * @author WB
 * @version 2020/04/01
 * 
 */
public class Region_Labeling_Demo implements PlugInFilter {
	
	public enum LabelingMethod {
			BreadthFirst, DepthFirst, Recursive, Sequential, RegionAndContours
	};

	static LabelingMethod method = LabelingMethod.BreadthFirst;
	static boolean recolor = false;
	static boolean listRegions = true;
	
    public int setup(String arg, ImagePlus im) {
		return DOES_8G + NO_CHANGES;
    }
	
    public void run(ImageProcessor ip) {
    	if (!getUserInput())
    		return;
    	
    	if (method == LabelingMethod.Recursive && 
    			!IJ.showMessageWithCancel("Recursive labeling", "This may run out of stack memory!\n" + "Continue?")) {
			return;
    	}
    	
    	// Copy the original to a new byte image:
    	ByteProcessor bp = ip.convertToByteProcessor(false);
    
		// Select a labeling method:
		RegionLabeling segmenter = null;
		switch (method) {
			case BreadthFirst:		segmenter = new BreadthFirstLabeling(bp); break;
			case DepthFirst:		segmenter = new DepthFirstLabeling(bp); break;
			case Recursive:			segmenter = new RecursiveLabeling(bp); break; 
			case Sequential:		segmenter = new SequentialLabeling(bp); break;
			case RegionAndContours:	segmenter = new RegionContourLabeling(bp); break;
		}

		// Retrieve the list of detected regions:
		List<BinaryRegion> regions = segmenter.getRegions(true);	// regions are sorted by size
		if (listRegions) {
			IJ.log("Detected regions (sorted by size): " + regions.size());
			for (BinaryRegion r: regions) {
				IJ.log(r.toString());
			}
		}
		
		// Show the resulting labeling as a random color image
		ImageProcessor labelIp = segmenter.makeLabelImage(recolor);
		(new ImagePlus("Label Image", labelIp)).show();
		
		// Example for processing all regions:
		for (BinaryRegion r : regions) {
			double mu11 = mu_11(r);	// example for calculating region statistics (see below)
			IJ.log("Region " + r.getLabel() + ": mu11=" + mu11);
		}
    }
    
	/**
	 * This method demonstrates how a particular region's central moment
     * mu_11 could be calculated from the finished region labeling.
	 * @param r a binary region
	 * @return
	 */
    double mu_11 (BinaryRegion r) {
    	Point ctr = r.getCenterPoint();
    	final double xc = ctr.getX();	// centroid of this region
    	final double yc = ctr.getY();
    	double mu11 = 0;
    	// iterate through all pixels of regions r:
    	for (Point p : r) {
    		mu11 = mu11 + (p.getX() - xc) * (p.getY() - yc);
    	}
    	return mu11;
    }
    
	boolean getUserInput() {
		GenericDialog gd = new GenericDialog(Region_Labeling_Demo.class.getSimpleName());
		String[] mNames = Enums.getEnumNames(LabelingMethod.class);
		gd.addChoice("Labeling method", mNames, mNames[0]);
		gd.addCheckbox("Color result", recolor);
		gd.addCheckbox("List regions", listRegions);
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		method = LabelingMethod.valueOf(gd.getNextChoice());
		recolor = gd.getNextBoolean();
		listRegions = gd.getNextBoolean();
		return true;
	}
    
    
}



