/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package ImageJ_Short_Reference;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Display_Demo implements PlugInFilter {
	ImagePlus im = null;

	public int setup(String arg, ImagePlus imp) {
		this.im = imp;		// keep reference to associated ImagePlus
		return DOES_ALL; 	// this plugin accepts any image
	}

	public void run(ImageProcessor ip) {
		for (int i = 0; i < 10; i++) {
			// modify this image:
			ip.smooth();
			ip.rotate(30);
			// redisplay this image:
			im.updateAndDraw();
			// sleep so user can watch this:
			IJ.wait(100);
		}
	}
}
