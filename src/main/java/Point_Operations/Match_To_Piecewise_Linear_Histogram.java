/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2015 Wilhelm Burger, Mark J. Burge. 
 * All rights reserved. Visit http://www.imagingbook.com for additional details.
 *  
 *******************************************************************************/
package Point_Operations;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.pub.histogram.HistogramMatcher;
import imagingbook.pub.histogram.HistogramPlot;
import imagingbook.pub.histogram.PiecewiseLinearCdf;
import imagingbook.pub.histogram.Util;

public class Match_To_Piecewise_Linear_Histogram implements PlugInFilter { 
	
	public int setup(String arg0, ImagePlus im) {
		return DOES_8G;
	}
	
	public void run(ImageProcessor ipA) {
		// get histogram of original image
		int[] hA = ipA.getHistogram();
		
		(new HistogramPlot(hA, "Histogram A")).show();
		(new HistogramPlot(Util.Cdf(hA), "Cumulative Histogram A")).show();
		
		// -------------------------
		int[] ik = {28, 75, 150, 210};
		double[] Pk = {.05, .25, .75, .95};
		PiecewiseLinearCdf pLCdf = new PiecewiseLinearCdf(256, ik, Pk);
		// -------------------------
		
		double[] nhB = pLCdf.getPdf();
		nhB = Util.normalizeHistogram(nhB);
		(new HistogramPlot(nhB, "Piecewise Linear")).show();
		(new HistogramPlot(pLCdf, "Piecewise Linear Cumulative")).show();
		
		HistogramMatcher m = new HistogramMatcher();
		int[] F = m.matchHistograms(hA, pLCdf);
		
//		for (int i = 0; i < F.length; i++) {
//			IJ.log(i + " -> " + F[i]);
//		}
		
		ipA.applyTable(F);
		int[] hAm = ipA.getHistogram();
		(new HistogramPlot(hAm, "Histogram A (mod)")).show();
		(new HistogramPlot(Util.Cdf(hAm), "Cumulative Histogram A (mod)")).show();
	}

}

