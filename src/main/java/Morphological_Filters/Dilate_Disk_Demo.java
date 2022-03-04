/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Morphological_Filters;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.morphology.BinaryDilation;
import imagingbook.pub.morphology.BinaryMorphologyFilter;
import imagingbook.pub.morphology.BinaryMorphologyOperator;


/**
 * This plugin implements a binary dilation using a disk-shaped
 * structuring element whose radius can be specified.
 */
public class Dilate_Disk_Demo implements PlugInFilter {

	double radius = 6.5;
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}

	public void run(ImageProcessor ip) {
		byte[][] H = BinaryMorphologyFilter.makeDiskKernel(radius);
//		BinaryMorphologyFilter filter = new BinaryDilation(H);
		BinaryMorphologyOperator filter = new BinaryDilation(H);
		
		filter.applyTo((ByteProcessor) ip);
	}	
}
