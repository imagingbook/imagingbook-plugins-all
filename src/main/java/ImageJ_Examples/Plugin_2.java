/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package ImageJ_Examples;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * This ImageJ plugin shows how data can be communicated from one
 * plugin to another. In this example, ANOTHER plugin ({@link Plugin_1})
 * calculates a histogram that is subsequently retrieved by THIS
 * plugin {@link Plugin_2}. Data are stored as a property of the associated
 * image (of type {@link ImagePlus}).
 * Note that the stored data should contain no instances of self-defined
 * classes, since these may be re-loaded when performing compile-and-run.
 * 
 * @author W. Burger
 *
 */
public class Plugin_2 implements PlugInFilter {
	ImagePlus im;
	
	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_ALL;}

	public void run(ImageProcessor ip) {
		String key = Plugin_1.HistKey;	
		int[] hist = (int[]) im.getProperty(key); 
		if (hist == null){
			IJ.error("found no histogram for image " + im.getTitle());
		}
		else {
			IJ.log("found histogram attached to image " + im.getTitle());
			// process the histogram ...
		}
		
		// delete the stored data if not needed any longer:
		// im.setProperty(key, null);
		
	}
}
