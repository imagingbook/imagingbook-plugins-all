/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package ImageJ_Examples;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * Updating the image.
 * @author WB
 */
public class XY_Plugin implements PlugInFilter {
	
	ImagePlus im;

	public int setup(String args, ImagePlus im) {
		this.im = im; 	// keep a reference to the ImagePlus object
		return DOES_8G;
	}

	public void run(ImageProcessor ip) {
		// ...  modify ip
		im.updateAndDraw(); 	// redraw the image
		// ...
	}

}
