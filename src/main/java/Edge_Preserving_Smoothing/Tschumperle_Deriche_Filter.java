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
import ij.io.LogStream;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.filter.GenericFilter;
import imagingbook.lib.settings.PrintPrecision;
import imagingbook.lib.util.progress.ProgressMonitor;
import imagingbook.lib.util.progress.ij.ProgressBarMonitor;
import imagingbook.pub.edgepreservingfilters.TschumperleDericheF.Parameters;
import imagingbook.pub.edgepreservingfilters.TschumperleDericheFilter;

/**
 * This ImageJ plugin demonstrates the use of the Anisotropic Diffusion filter proposed 
 * by David Tschumperle in D. Tschumperle and R. Deriche, Rachid, "Diffusion PDEs on 
 * vector-valued images}", IEEE Signal Processing Magazine, vol. 19, no. 5, pp. 16-25 
 * (Sep. 2002). This plugin works for all types of images and stacks.
 * @author W. Burger
 * @version 2021/01/06
 */

public class Tschumperle_Deriche_Filter implements PlugInFilter {
	
	static {
		LogStream.redirectSystem();
		PrintPrecision.set(6);
	}
	
	private static Parameters params = new Parameters();
	
//	private boolean isColor;

	public int setup(String arg0, ImagePlus imp) {
		return DOES_ALL + DOES_STACKS;
	}
	
	public void run(ImageProcessor ip) {
//		isColor = (ip instanceof ColorProcessor);
		if (!getParameters())
			return;
		
		GenericFilter filter = new TschumperleDericheFilter(params);
		
		try (ProgressMonitor m = new ProgressBarMonitor(filter)) {
			filter.applyTo(ip);
		}
	}

	private boolean getParameters() {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addNumericField("Number of iterations", params.iterations, 0);
		gd.addNumericField("dt (Time step)", params.dt, 1);
		gd.addNumericField("Gradient smoothing (sigma_g)", params.sigmaD, 2);
		gd.addNumericField("Structure tensor smoothing (sigma_s)", params.sigmaM, 2);
		gd.addNumericField("a1 (Diffusion limiter along minimal variations)", params.a0, 2);
		gd.addNumericField("a2 (Diffusion limiter along maximal variations)", params.a1, 2);
//		if (isColor) {
//			gd.addCheckbox("Use linear RGB", params.useLinearRgb);
//		}
		gd.addMessage("Incorrect values are replaced by defaults.");
		
		gd.showDialog();
		if (gd.wasCanceled()) return false;

		params.iterations = Math.max(1, (int)gd.getNextNumber());
		params.dt = (double) gd.getNextNumber();
		params.sigmaD = gd.getNextNumber();
		params.sigmaM = gd.getNextNumber();
		params.a0 = (float) gd.getNextNumber();
		params.a1 = (float) gd.getNextNumber();
//		if (isColor) {
//			params.useLinearRgb = gd.getNextBoolean();
//		}
		return true;
	}

//	@Override
//	public void updateProgress(double progress) {
//		IJ.showProgress(progress);
//	}
    
}



