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
import imagingbook.pub.edgepreservingfilters.PeronaMalikF.ColorMode;
import imagingbook.pub.edgepreservingfilters.PeronaMalikF.ConductanceFunction;
import imagingbook.pub.edgepreservingfilters.PeronaMalikF.Parameters;
import imagingbook.lib.filter.FilterProgressListener;
import imagingbook.pub.edgepreservingfilters.PeronaMalikFilterScalar;
import imagingbook.pub.edgepreservingfilters.PeronaMalikFilterVector;

/**
 * This plugin demonstrates the use of the PeronaMalikFilter class.
 * This plugin works for all types of images and stacks.
 * 
 * @author W. Burger
 * @version 2021/01/05
 */
public class Perona_Malik_Filter implements PlugInFilter, FilterProgressListener {

	private static Parameters params = new Parameters();
	
	private boolean isColor;

	public int setup(String arg0, ImagePlus imp) {
		return DOES_ALL;
	}

	public void run(ImageProcessor ip) {
		isColor = (ip instanceof ColorProcessor);
		if (!getParameters())
			return;
		
		if (isColor) {
			ColorProcessor cp = (ColorProcessor) ip;
			if (params.colorMode == ColorMode.SeparateChannels) {
				new PeronaMalikFilterScalar(params).applyTo(cp);
			}
			else {
				new PeronaMalikFilterVector(params).applyTo(cp);
			}
		}
		else {
			new PeronaMalikFilterScalar(params).applyTo(ip);
		}
	}
	
	private boolean getParameters() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addNumericField("Number of iterations", params.iterations, 0);
		gd.addNumericField("Alpha (0,..,0.25)", params.alpha, 2);
		gd.addNumericField("K", params.kappa, 0);
		gd.addEnumChoice("Conductance function", params.conductanceFunType);
		if (isColor) {
			gd.addEnumChoice("Color method", params.colorMode);
			//gd.addCheckbox("Use linear RGB", params.useLinearRgb);
		}
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		params.iterations = (int) Math.max(gd.getNextNumber(), 1);
		params.alpha = (float) gd.getNextNumber();
		params.kappa = (float) gd.getNextNumber();
		params.conductanceFunType = gd.getNextEnumChoice(ConductanceFunction.Type.class);
		if (isColor) {
			params.colorMode = gd.getNextEnumChoice(ColorMode.class);
			//params.useLinearRgb = gd.getNextBoolean();
		}
		return true;
	}
}



