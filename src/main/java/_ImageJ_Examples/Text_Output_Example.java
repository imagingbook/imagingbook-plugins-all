/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package _ImageJ_Examples;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * This is a minimal ImageJ plugin (PlugInFilter) that inverts an
 * 8-bit grayscale (byte) image.
 * @author WB
 */
public class Text_Output_Example implements PlugInFilter {
	
	ImagePlus im;

	public int setup(String args, ImagePlus im) {
		this.im = im;
		return DOES_8G; // this plugin accepts 8-bit grayscale images 
	}

	public void run(ImageProcessor ip) {
		int M = ip.getWidth();
		int N = ip.getHeight();
		
		String title = im.getShortTitle();
		IJ.log("Image " + title + " has " + (M * N) + " elements.");
	}

}
