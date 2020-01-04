/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package _ImageJ_Short_Reference;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Plugin_2 implements PlugInFilter {
	ImagePlus im;
	
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_ALL;}

	public void run(ImageProcessor ip) {
		String key = Plugin_1.HistKey;	
		int[] hist = (int[]) im.getProperty(key); 
		if (hist == null){
			IJ.error("This image has no histogram");
		}
		else {
			// process histogram ...
		}
	}
}
