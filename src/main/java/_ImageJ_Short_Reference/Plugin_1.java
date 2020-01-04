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

public class Plugin_1 implements PlugInFilter {
	ImagePlus im;
	public static final String HistKey = "HISTOGRAM"; 
	
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_ALL + NO_CHANGES;}
		
	public void run(ImageProcessor ip) {
		int[] hist = ip.getHistogram();
		// add histogram to image properties:
		im.setProperty(HistKey, hist); 
	}
}
