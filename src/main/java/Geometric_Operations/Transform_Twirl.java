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
import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.geometry.mappings.Mapping2D;

public class Transform_Twirl implements PlugInFilter {
	
	static double alpha = Math.toRadians(43.0); 	// angle (43 degrees)

	@Override
	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL;
	}

	@Override
	public void run(ImageProcessor ip) {
		final double xc = 0.5 * ip.getWidth();
		final double yc = 0.5 * ip.getHeight();
		final double rmax = Math.sqrt(xc * xc + yc * yc);
		
		Mapping2D imap = new Mapping2D() {	// inverse mapping (target to source)
			@Override
			public Pnt2d applyTo(Pnt2d uv) {
				double dx = uv.getX() - xc;
				double dy = uv.getY() - yc;
				double r = Math.sqrt(dx * dx + dy * dy);
				if (r < rmax) {
					double beta = Math.atan2(dy, dx) + alpha * (rmax - r) / rmax;
					double x = xc + r * Math.cos(beta);
					double y = yc + r * Math.sin(beta);
					return Pnt2d.from(x, y);
				}
				else {
					return uv;	// return the original point
				}
			}
		};
		
		new ImageMapper(imap).map(ip);
	}
	
}
