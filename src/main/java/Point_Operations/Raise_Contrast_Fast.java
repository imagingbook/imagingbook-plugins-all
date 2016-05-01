/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/
package Point_Operations;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * Does the same as {@link Raise_Contrast} but uses the one-dimensional
 * pixel array to read and writes pixel values. Should be fast.
 * @author WB
 */
public class Raise_Contrast_Fast implements PlugInFilter {

	public int setup(String arg, ImagePlus img) {
		return DOES_8G;
	}
	
	public void run(ImageProcessor ip) {
		// ip is assumed to be of type ByteProcessor
		byte[] pixels = (byte[]) ip.getPixels();
		for (int i = 0; i < pixels.length; i++) {
			int a = 0xFF & pixels[i];
			int b = (int) (a * 1.5 + 0.5);
			if (b > 255)
				b = 255;
			pixels[i] = (byte) (0xFF & b);
		}
	}
}
