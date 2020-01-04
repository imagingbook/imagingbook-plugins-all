/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package _ImageJ_Short_Reference;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import java.awt.Rectangle;

public class Roi_Demo implements PlugInFilter {
	boolean showMask = true;

	public int setup(String arg, ImagePlus imp) {
		return DOES_RGB;
	}

	public void run(ImageProcessor ip) {
		Rectangle roi = ip.getRoi();
		ImageProcessor mask = ip.getMask();
		boolean hasMask = (mask != null);
		if (hasMask && showMask) {
			(new ImagePlus("The Mask", mask)).show();
		}

		// ROI corner coordinates: 
		int rLeft = roi.x;
		int rTop = roi.y;
		int rRight = rLeft + roi.width;
		int rBottom = rTop + roi.height;

		// process all pixels inside the ROI
		for (int v = rTop; v < rBottom; v++) {
			for (int u = rLeft; u < rRight; u++) {
				if (!hasMask || mask.getPixel(u - rLeft, v - rTop) > 0) {
					int p = ip.getPixel(u, v);
					ip.putPixel(u, v, ~p);
				}

			}
		}
	}
}
