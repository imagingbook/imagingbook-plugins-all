/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Thresholding.Global;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.ij.IjUtils;

/**
 * Demo plugin making available a selection of global thresholders.
 * 
 * @author WB
 * @version 2022/04/02
 */
public class Threshold_Global_All implements PlugInFilter {
	
	enum Algorithm {
		IsoData(Threshold_Isodata.class), 
		MaxEntropy(Threshold_MaxEntropy.class),
		Mean(Threshold_Mean.class),
		Median(Threshold_Median.class),
		MinError(Threshold_MinError.class),
		MinMax(Threshold_MinMax.class),
		Otsu(Threshold_Otsu.class);
		
		final Class<? extends PlugInFilter> pluginClass;
		
		Algorithm(Class<? extends PlugInFilter> cls) {
			this.pluginClass = cls;
		}
	}
	
	private static Algorithm algo = Algorithm.IsoData;
	private ImagePlus imp = null;
	
	@Override
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_8G;
	}

	@Override
	public void run(ImageProcessor ip) {
		GenericDialog gd = new GenericDialog(this.getClass().getSimpleName());
		gd.addEnumChoice("Algorithm", algo);
		
		gd.showDialog();
		if (gd.wasCanceled())
			return;
		
		algo = gd.getNextEnumChoice(Algorithm.class);
		imp.unlock();
		
		IjUtils.runPlugInFilter(algo.pluginClass);
//		IJ.runPlugIn(imp, algo.pluginClass.getCanonicalName(), null);
	}

}
