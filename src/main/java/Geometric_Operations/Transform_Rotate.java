/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Geometric_Operations;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.image.ImageMapper;
import imagingbook.pub.geometry.mappings.Mapping2D;
import imagingbook.pub.geometry.mappings.linear.Rotation2D;


public class Transform_Rotate implements PlugInFilter {
	
	static double alpha = Math.toRadians(15.0); 	// angle (15 degrees)

	@Override
    public int setup(String arg, ImagePlus im) {
        return DOES_ALL;	// works for all image types
    }

	@Override
    public void run(ImageProcessor ip) {
		Mapping2D mi = new Rotation2D(alpha).getInverse(); // inverse mapping (target to source)
		new ImageMapper(mi).map(ip);
    }
}
