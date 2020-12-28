/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package _Demos;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.image.access.ScalarAccessor;

/**
 * 
 * 
 * @author WB
 *
 */
public class ImageAccessor_Demo_Scalar implements PlugInFilter {

	public int setup(String arg, ImagePlus img) {
		return DOES_8G + DOES_16 + DOES_32;
	}

	public void run(ImageProcessor ip) {
		final int width = ip.getWidth();
		final int height = ip.getHeight();
		
		ScalarAccessor ia = ScalarAccessor.create(ip, null, null);
		
		for (int u = 0; u < width; u++) {
			for (int v = 0; v < height; v++) {
				float val = ia.getVal(u, v);
				ia.setVal(u, v, val  + 20);
			}
		}
	}

}
