/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Edge_Preserving_Smoothing;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.edgepreservingfilters.KuwaharaF.Parameters;
import imagingbook.pub.edgepreservingfilters.KuwaharaFilterScalar;
import imagingbook.pub.edgepreservingfilters.KuwaharaFilterVector;

/**
 * Scalar version. Applied to color images, each color component is filtered separately.
 * This plugin demonstrates the use of the Kuwahara filter, similar to the filter suggested in 
 * Tomita and Tsuji (1977). It structures the filter region into  five overlapping, 
 * square subregions of size (r+1) x (r+1). Unlike the original Kuwahara filter,
 * it includes a centered subregion. This plugin works for all types of images and stacks.
 * 
 * @author WB
 * @version 2021/01/02
 */
public class Kuwahara_Filter implements PlugInFilter {

	private static Parameters params = new Parameters();
	private static boolean UseVectorFilter = false;
	
	private boolean isColor;

	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL + DOES_STACKS;
	}

	public void run(ImageProcessor ip) {
		isColor = (ip instanceof ColorProcessor);
		if (!getParameters())
			return;
		if (isColor && UseVectorFilter) {
			new KuwaharaFilterVector(params).applyTo((ColorProcessor)ip);
		}
		else {
			new KuwaharaFilterScalar(params).applyTo(ip);
		}
	}

	private boolean getParameters() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addNumericField("Radius (>1)", params.radius, 0);
		gd.addNumericField("Variance threshold", params.tsigma, 0);
		if (isColor)
			gd.addCheckbox("Use vector filter", UseVectorFilter);
		gd.showDialog();
		if(gd.wasCanceled()) 
			return false;
		params.radius = (int) Math.max(gd.getNextNumber(), 1);
		params.tsigma = Math.max(gd.getNextNumber(), 0);
		if (isColor)
			UseVectorFilter = gd.getNextBoolean();
		return true;
	}
}

