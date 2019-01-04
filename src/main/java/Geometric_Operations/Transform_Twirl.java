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
import imagingbook.pub.geometry.mappings.nonlinear.TwirlMapping;

public class Transform_Twirl implements PlugInFilter {
	
	static double angle = 0.75; // radians

	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL;
	}

	public void run(ImageProcessor ip) {
		int w = ip.getWidth();
		int h = ip.getHeight();
		double xc = 0.5 * w;
		double yc = 0.5 * h;
		double radius = Math.sqrt(xc * xc + yc * yc);
		
		TwirlMapping imap = new TwirlMapping(xc, yc, angle, radius);	// inverse mapping (target to source)
		ImageMapper mapper = new ImageMapper(imap, InterpolationMethod.Bicubic);
		mapper.map(ip);
//		imap.applyTo(ip, InterpolationMethod.Bicubic);
	}

}
