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
import imagingbook.lib.interpolation.InterpolationMethod;
import imagingbook.pub.geometry.mappings.Mapping;
import imagingbook.pub.geometry.mappings.linear.ProjectiveMapping;

import java.awt.Point;
import java.awt.geom.Point2D;


public class Transform_Projective implements PlugInFilter {

    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    public void run(ImageProcessor ip) {
		Point2D p1 = new Point(0, 0);
		Point2D p2 = new Point(400, 0);
		Point2D p3 = new Point(400, 400);
		Point2D p4 = new Point(0, 400);

		Point2D q1 = new Point(0, 60);
		Point2D q2 = new Point(400, 20);
		Point2D q3 = new Point(300, 400);
		Point2D q4 = new Point(30, 200);

		Mapping imap = ProjectiveMapping.from4Points(p1, p2, p3, p4, q1, q2, q3, q4).getInverse();
		imap.applyTo(ip, InterpolationMethod.Bilinear);
	}
}
