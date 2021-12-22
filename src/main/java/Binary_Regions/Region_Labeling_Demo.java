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
import imagingbook.pub.regions.segment.BinaryRegionSegmentation;
import imagingbook.pub.regions.segment.BreadthFirstSegmentation;
import imagingbook.pub.regions.segment.DepthFirstSegmentation;
import imagingbook.pub.regions.segment.RecursiveSegmentation;
import imagingbook.pub.regions.segment.RegionContourSegmentation;
import imagingbook.pub.regions.segment.SequentialSegmentation;
import imagingbook.pub.regions.segment.BinaryRegionSegmentation.BinaryRegion;
import imagingbook.pub.regions.NeighborhoodType;
import imagingbook.pub.regions.utils.Display;

/**
 * This ImageJ plugin is an example for how to use the region
 * labeling classes in the "regions" package:
 * {@link BreadthFirstSegmentation},
 * {@link DepthFirstSegmentation},
 * {@link RecursiveSegmentation},
 * {@link RegionContourSegmentation},
 * {@link SequentialSegmentation}.
 * One of four labeling types can be selected (see the {@code run()} method).
 * All should produce the same results (except {@link RegionContourSegmentation},
 * which may run out of memory easily).
 * Requires a binary (segmented) image.
 * 
 * @author WB
 * @version 2020/12/20
 * 
 */
public class Region_Labeling_Demo implements PlugInFilter {
	
	private enum LabelingMethod {
		BreadthFirst, 
		DepthFirst, 
		Sequential,
		RegionAndContours,
		Recursive
	}

	static LabelingMethod Method = LabelingMethod.BreadthFirst;
	static NeighborhoodType Neighborhood = NeighborhoodType.N8;
	
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
    	
    	if (Method == LabelingMethod.Recursive && 
    			!IJ.showMessageWithCancel("Recursive labeling", "This may run out of stack memory!\n" + "Continue?")) {
			return;
    	}
    	
    	// Copy the original to a new byte image:
    	ByteProcessor bp = ip.convertToByteProcessor(false);
    	
    	
		BinaryRegionSegmentation segmenter = null;
		switch(Method) {
		case BreadthFirst : 	
			segmenter = new BreadthFirstSegmentation(bp, Neighborhood); break;
		case DepthFirst : 		
			segmenter = new DepthFirstSegmentation(bp, Neighborhood); break;
		case Sequential : 		
			segmenter = new SequentialSegmentation(bp, Neighborhood); break;
		case RegionAndContours : 
			segmenter = new RegionContourSegmentation(bp, Neighborhood); break;
		case Recursive : 
			segmenter = new RecursiveSegmentation(bp, Neighborhood); break;
		}

		// Retrieve the list of detected regions:
		List<BinaryRegion> regions = segmenter.getRegions(true);	// regions are sorted by size
		
		if (regions == null || regions.isEmpty()) {
			IJ.showMessage("No regions detected!");
			return;
		}
		
		IJ.log("Detected regions: " + regions.size());
		
		if (ListRegions) {
			IJ.log("Regions sorted by size: ");
			for (BinaryRegion r: regions) {
				IJ.log(r.toString());
			}
		}
		
		// Show the resulting labeling as a random color image
		ImageProcessor labelIp = Display.makeLabelImage(segmenter, ColorRegions);
		(new ImagePlus(Method.name(), labelIp)).show();
		
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
		gd.addEnumChoice("Segmentation method", Method);
		gd.addEnumChoice("Neighborhood type", Neighborhood);
		gd.addCheckbox("Color result", ColorRegions);
		gd.addCheckbox("List regions", ListRegions);
		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}
		Method = gd.getNextEnumChoice(LabelingMethod.class);
		Neighborhood = gd.getNextEnumChoice(NeighborhoodType.class);
		ColorRegions = gd.getNextBoolean();
		ListRegions = gd.getNextBoolean();
		return true;
	}
    
    
}



