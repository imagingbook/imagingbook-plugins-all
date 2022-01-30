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
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.geometry.basic.NeighborhoodType2D;
import imagingbook.pub.morphology.BinaryOutline;

/**
 * This ImageJ plugin demonstrates morphological outline calculation
 * on binary images. Pixels with value 0 are considered
 * background, values &gt; 0 are foreground. The plugin 
 * modifies the supplied image.
 * 
 * @author W. Burger
 * @version 2022/01/24
 *
 */
public class Outline_Demo implements PlugInFilter {
	
	private static NeighborhoodType2D nh = NeighborhoodType2D.N4;
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}

	public void run(ImageProcessor ip) {
		
		if (!showDialog()) {
			return;
		}
		
		BinaryOutline outline = new BinaryOutline(nh);
		outline.applyTo((ByteProcessor) ip);
	}
	
	private boolean showDialog() {
		GenericDialog gd = new GenericDialog(getClass().getSimpleName());
		gd.addEnumChoice("Neighborhood type", nh);

		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		
		nh = gd.getNextEnumChoice(NeighborhoodType2D.class);
		return true;
	}
	
}
