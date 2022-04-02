/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Point_Operations;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.pub.histogram.HistogramMatcher;
import imagingbook.pub.histogram.HistogramPlot;
import imagingbook.pub.histogram.Util;

/**
 * Adapts image intensities to match a Gaussian histogram.
 * 
 * @author WB
 * 
 * @see HistogramMatcher
 * @see HistogramPlot
 * @see Util#makeGaussianHistogram(double, double)
 *
 */
public class Match_To_Gaussian_Histogram implements PlugInFilter {
	
	private static double GaussMean = 128;
	private static double GaussVariance = 128;
	
	@Override
	public int setup(String arg0, ImagePlus im) {
		return DOES_8G;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		
		// get histograms
		int[] hi = ip.getHistogram();
		int[] hG = Util.makeGaussianHistogram(GaussMean, GaussVariance);
				
		(new HistogramPlot(hi, "Image Histogram")).show();
		(new HistogramPlot(hG, "Gaussian Histogram")).show();
		
		double[] nhG = Util.normalizeHistogram(hG);
		(new HistogramPlot(nhG, "Gaussian Hist. normalized")).show();
		
		double[] chG = Util.Cdf(hG);
    	(new HistogramPlot(chG, "Gaussian Hist. cumulative")).show();
		
		HistogramMatcher m = new HistogramMatcher();
		int[] F = m.matchHistograms(hi, hG);
		
//		for (int i = 0; i < F.length; i++) {
//			IJ.log(i + " -> " + F[i]);
//		}
		
		ip.applyTable(F);
		int[] hAm = ip.getHistogram();
		(new HistogramPlot(hAm, "Histogram A (mod)")).show();
		(new HistogramPlot(Util.Cdf(hAm), "Cumulative Histogram A (mod)")).show();
	}

}

