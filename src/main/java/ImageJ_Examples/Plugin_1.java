/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/
package ImageJ_Examples;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * This ImageJ plugin shows how data can be communicated from one
 * plugin to another. In this example, THIS plugin ({@link Plugin_1})
 * calculates a histogram that is subsequently retrieved by ANOTHER
 * plugin {@link Plugin_2}. Data are stored as a property of the associated
 * image (of type {@link ImagePlus}).
 * Note that the stored data should contain no instances of self-defined
 * classes, since these may be re-loaded when performing compile-and-run.
 * 
 * @author W. Burger
 *
 */
public class Plugin_1 implements PlugInFilter {
	
	public static final String HistKey = Plugin_1.class.getCanonicalName() +"histogram";
	ImagePlus im;
	
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_ALL + NO_CHANGES;}
		
	public void run(ImageProcessor ip) {
		int[] hist = ip.getHistogram();
		// add histogram to image properties:
		im.setProperty(HistKey, hist); 
	}
}
