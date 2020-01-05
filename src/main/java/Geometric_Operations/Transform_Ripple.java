/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Geometric_Operations;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.image.ImageMapper;
import imagingbook.lib.interpolation.InterpolationMethod;
import imagingbook.pub.geometry.mappings.nonlinear.RippleMapping;

public class Transform_Ripple implements PlugInFilter {

	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL;
	}

	public void run(ImageProcessor ip) {
		double xW = 120 / (2 * Math.PI);
		double xAmpl = 10;
		double yW = 250 / (2 * Math.PI);
		double yAmpl = 10;

		RippleMapping imap = new RippleMapping(xW, xAmpl, yW, yAmpl);	// inverse (target to source)
		ImageMapper mapper = new ImageMapper(imap, InterpolationMethod.Bicubic);
		mapper.map(ip);
	}

}
