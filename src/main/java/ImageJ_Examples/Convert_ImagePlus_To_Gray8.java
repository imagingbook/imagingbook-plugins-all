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
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

public class Convert_ImagePlus_To_Gray8 implements PlugInFilter {
	ImagePlus imp = null;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_ALL; 	// this plugin accepts any image
	}

	public void run(ImageProcessor ip) {
		ImageConverter iConv = new ImageConverter(imp);
		iConv.convertToGray8();
		ip = imp.getProcessor();	// ip is now of type ByteProcessor
		// process grayscale image ...
		ip.sharpen();
	}
}
