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
import imagingbook.pub.geometry.basic.Point;
import imagingbook.pub.geometry.mappings.nonlinear.BilinearMapping;

public class Transform_Bilinear implements PlugInFilter {

    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    public void run(ImageProcessor ip) {
//		Point p1 = Point.create(0, 0);
//		Point p2 = Point.create(400, 0);
//		Point p3 = Point.create(400, 400);
//		Point p4 = Point.create(0, 400);
//
//		Point q1 = Point.create(0, 60);
//		Point q2 = Point.create(400, 20);
//		Point q3 = Point.create(300, 400);
//		Point q4 = Point.create(30, 200);
		
	   	Point[] P = {
				Point.create(0, 0),
				Point.create(400, 0),
				Point.create(400, 400),
				Point.create(0, 400)
	    	};

	    	Point[] Q = {
				Point.create(0, 60),
				Point.create(400, 20),
				 Point.create(300, 400),
				 Point.create(30, 200)
	    	};
		
		// we want the inverse mapping (Q -> P, so we swap P/Q):
		BilinearMapping imap = BilinearMapping.fromPoints(Q, P);
		ImageMapper mapper = new ImageMapper(imap, InterpolationMethod.Bicubic);
		mapper.map(ip);
    }
}
