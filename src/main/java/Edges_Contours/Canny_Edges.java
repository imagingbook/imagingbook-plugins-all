/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Edges_Contours;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.color.edge.CannyEdgeDetector;

/**
 * This ImageJ shows the use of the Canny edge detector in
 * its simplest form. Works on all image types.
 * 
 * See also {@link CannyEdgeDetector} and {@link Color_Edges.Color_Edges_Canny}.
 * 
 * @author WB
 *
 */
public class Canny_Edges implements PlugInFilter {
	
	public int setup(String arg0, ImagePlus imp) {
		return DOES_ALL + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		CannyEdgeDetector.Parameters params = new CannyEdgeDetector.Parameters(); 			
		params.gSigma = 3.0;	// sigma of Gaussian
		params.hiThr  = 20.0;	// 20% of max. edge magnitude
		params.loThr  = 5.0;	// 5% of max. edge magnitude
		
		CannyEdgeDetector detector = new CannyEdgeDetector(ip, params);
		
//		FloatProcessor eMag = detector.getEdgeMagnitude();
//		FloatProcessor eOrt = detector.getEdgeOrientation();
//		List<List<Point>> edgeTraces = detector.getEdgeTraces();
		
		ByteProcessor edge = detector.getEdgeBinary();
		(new ImagePlus("Canny Edges", edge)).show();
	}
}
