/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package _ImageJ_Examples;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import java.awt.Rectangle;

/**
 * This ImageJ plugin shows how a subimage is extracted from
 * a given image using the bounding box of the currently selected 
 * region of interest (ROI). Note that the resulting image is
 * of the same type as the original.
 * 
 * @author WB
 * @version 2015/03/23
 */
public class Roi_Extract_Subimage_Demo implements PlugInFilter {
	boolean showMask = true;

	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL + ROI_REQUIRED;
	}

	public void run(ImageProcessor ip) {
		Rectangle roi = ip.getRoi();
		if (roi == null) {
			IJ.error("selection required!"); // this should not happen ever
			return;
		}

		ImageProcessor ip2 = ip.crop();
		new ImagePlus("Extracted image", ip2).show();
	}
}
