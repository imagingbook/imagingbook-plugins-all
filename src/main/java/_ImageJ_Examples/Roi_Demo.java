/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package _ImageJ_Examples;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import java.awt.Rectangle;

/**
 * This ImageJ plugin shows how an image operation can be confined
 * to the currently selected region of interest (ROI). Note that
 * the ROI attached to an ImageProcessor object is a rectangle,
 * which is used to limit the range of visited pixels. In addition,
 * if the image has a mask attached, it is used to check if the
 * current pixel is inside an ROI of arbitrary shape.
 * Note that masks have their own coordinate system whose origin
 * is generally not the same as in the associated image.
 * 
 * @author WB
 * @version 2015/03/23
 */
public class Roi_Demo implements PlugInFilter {
	boolean showMask = true;

	public int setup(String arg, ImagePlus imp) {
		return DOES_RGB + ROI_REQUIRED;
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
