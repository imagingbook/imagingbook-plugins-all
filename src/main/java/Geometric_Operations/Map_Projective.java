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
import imagingbook.lib.interpolation.InterpolationMethod;
import imagingbook.pub.geometry.basic.Pnt2d;
import imagingbook.pub.geometry.mappings.Mapping2D;
import imagingbook.pub.geometry.mappings.linear.ProjectiveMapping2D;

/**
 * This plugin demonstrates the use of geometric mappings, as implemented
 * in the imagingbook library.
 * A {@link ProjectiveMapping2D} (transformation) is specified by 4
 * corresponding point pairs, given by point sequences P and Q.
 * The inverse mapping is required for target-to-source mapping.
 * The actual pixel transformation is performed by an {@link ImageMapper} object.
 * Try on a suitable test image and check if the image corners (P) are
 * mapped to the points specified in Q.
 * This plugin works for all image types.
 * 
 * @author WB
 * @version 2021/10/03
 *
 */
public class Map_Projective implements PlugInFilter {
	
	static Pnt2d[] P = {
			Pnt2d.from(0, 0),
			Pnt2d.from(400, 0),
			Pnt2d.from(400, 400),
			Pnt2d.from(0, 400)};

	static Pnt2d[] Q = {
			Pnt2d.from(0, 60),
			Pnt2d.from(400, 20),
			Pnt2d.from(300, 400),
			Pnt2d.from(30, 200)};

	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL + NO_CHANGES;
	}

	public void run(ImageProcessor source) {
		int W = source.getWidth();
		int H = source.getHeight();
		// create the target image:
		ImageProcessor target = source.createProcessor(W, H); 
		
		// create the target-to source mapping, i.e. Q -> P. 
		// there are 2 alternatives:
		Mapping2D mi = ProjectiveMapping2D.fromPoints(P, Q).getInverse();		// P -> Q, then invert
		//Mapping2D mi = ProjectiveMapping2D.fromPoints(Q, P);		// Q -> P = inverse mapping

		// create a mapper instance:
		ImageMapper mapper = new ImageMapper(mi, InterpolationMethod.Bicubic);
		
		// apply the mapper:
		mapper.map(source, target);
		
		// display the target image:
		new ImagePlus("Target", target).show();
	}
}