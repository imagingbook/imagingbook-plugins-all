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
import imagingbook.lib.ij.IjUtils;
import imagingbook.lib.util.Enums;
import imagingbook.pub.regions.BreadthFirstLabeling;
import imagingbook.pub.regions.DepthFirstLabeling;
import imagingbook.pub.regions.LabelingMethod;
import imagingbook.pub.regions.NeighborhoodType;
import imagingbook.pub.regions.RecursiveLabeling;
import imagingbook.pub.regions.RegionContourLabeling;
import imagingbook.pub.regions.RegionLabeling;
import imagingbook.pub.regions.RegionLabeling.BinaryRegion;
import imagingbook.pub.regions.SequentialLabeling;
import imagingbook.pub.regions.utils.Images;

/**
 * This ImageJ plugin is an example for how to use the region
 * labeling classes in the "regions" package:
 * {@link BreadthFirstLabeling},
 * {@link DepthFirstLabeling},
 * {@link RecursiveLabeling},
 * {@link RegionContourLabeling},
 * {@link SequentialLabeling}.
 * One of four labeling types can be selected (see the {@code run()} method).
 * All should produce the same results (except {@link RegionContourLabeling},
 * which may run out of memory easily).
 * Requires a binary (segmented) image.
 * 
 * @author WB
 * @version 2020/12/20
 * 
 */
public class Region_Labeling_Demo implements PlugInFilter {

	static LabelingMethod method = LabelingMethod.BreadthFirst;
	
	static boolean ColorRegions = false;
	static boolean ListRegions = false;
	
    public int setup(String arg, ImagePlus im) {
		return DOES_8G + NO_CHANGES;
    }
	
    public void run(ImageProcessor ip) {
    	
		if (!IjUtils.isBinary(ip)) {
			IJ.showMessage("Plugin requires a binary image!");
			return;
		}
		
    	if (!getUserInput())
    		return;
    	
    	if (method == LabelingMethod.Recursive && 
    			!IJ.showMessageWithCancel("Recursive labeling", "This may run out of stack memory!\n" + "Continue?")) {
			return;
    	}
    	
    	// Copy the original to a new byte image:
    	ByteProcessor bp = ip.convertToByteProcessor(false);
		RegionLabeling segmenter = LabelingMethod.getInstance(method, bp);
		segmenter.neighborhood = NeighborhoodType.N4;
		if (!segmenter.segment()) {
			IJ.error("Segmentation failed!");
		}

		// Retrieve the list of detected regions:
		List<BinaryRegion> regions = segmenter.getRegions(true);	// regions are sorted by size
		
		IJ.log("Detected regions: " + regions.size());
		IJ.log("MaxLabel: " + segmenter.getMaxLabel());
		
		if (ListRegions) {
			IJ.log("Regions sorted by size: ");
			for (BinaryRegion r: regions) {
				IJ.log(r.toString());
			}
		}
		
		// Show the resulting labeling as a random color image
		ImageProcessor labelIp = Images.makeLabelImage(segmenter, ColorRegions);
		(new ImagePlus(method.name(), labelIp)).show();
		
		// Example for processing all regions:
//		for (BinaryRegion r : regions) {
//			double mu11 = mu_11(r);	// example for calculating region statistics (see below)
//			IJ.log("Region " + r.getLabel() + ": mu11=" + mu11);
//		}
    }
    
//	/**
//	 * This method demonstrates how a particular region's central moment
//     * mu_11 could be calculated from the finished region labeling.
//	 * @param r a binary region
//	 * @return
//	 */
//    private double mu_11 (BinaryRegion r) {
//    	Point ctr = r.getCenterPoint();
//    	final double xc = ctr.getX();	// centroid of this region
//    	final double yc = ctr.getY();
//    	double mu11 = 0;
//    	// iterate through all pixels of regions r:
//    	for (Point p : r) {
//    		mu11 = mu11 + (p.getX() - xc) * (p.getY() - yc);
//    	}
//    	return mu11;
//    }
    
    private boolean getUserInput() {
		GenericDialog gd = new GenericDialog(Region_Labeling_Demo.class.getSimpleName());
		String[] mNames = Enums.getEnumNames(LabelingMethod.class);
		gd.addChoice("Labeling method", mNames, mNames[0]);
		gd.addCheckbox("Color result", ColorRegions);
		gd.addCheckbox("List regions", ListRegions);
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		method = LabelingMethod.valueOf(gd.getNextChoice());
		ColorRegions = gd.getNextBoolean();
		ListRegions = gd.getNextBoolean();
		return true;
	}
    
    
}



