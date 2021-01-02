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
import imagingbook.lib.math.VectorNorm.NormType;
import imagingbook.pub.edgepreservingfilters.BilateralFilterVector;
import imagingbook.pub.edgepreservingfilters.BilateralFilterVector.Parameters;
import imagingbook.pub.edgepreservingfilters.BilateralFilterVectorSeparable;


/**
 * This plugin demonstrates the use of the (full) BilateralFilter class.
 * This plugin works for all types of images.
 * When used on a color image, the filter is applied separately to
 * each color component.
 * 
 * @author WB
 * @version 2021/01/02
 */
public class Bilateral_Filter_Vector implements PlugInFilter {
	
	private Parameters params = new Parameters();
	private static boolean UseSeparableFilter = false;
	
	public int setup(String arg0, ImagePlus imp) {
		return DOES_RGB;	// only works on color images!
	}
	
	public void run(ImageProcessor ip) {
		if (!getParameters())
			return;

		if (UseSeparableFilter) {
			new BilateralFilterVectorSeparable((ColorProcessor) ip, params).apply();
		}
		else {
			new BilateralFilterVector((ColorProcessor) ip, params).apply();
		}
	}

	private boolean getParameters() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addNumericField("Sigma domain", params.sigmaD, 1);
		gd.addNumericField("Sigma range", params.sigmaR, 1);
		gd.addEnumChoice("Color norm", params.colorNormType);
		gd.addCheckbox("Use X/Y-separable filter", UseSeparableFilter);
		gd.showDialog();
		if (gd.wasCanceled()) return false;
		params.sigmaD = Math.max(gd.getNextNumber(), 0.5);
		params.sigmaR = Math.max(gd.getNextNumber(), 1);
		params.colorNormType = gd.getNextEnumChoice(NormType.class);
		UseSeparableFilter = gd.getNextBoolean();
		return true;
    }
}


