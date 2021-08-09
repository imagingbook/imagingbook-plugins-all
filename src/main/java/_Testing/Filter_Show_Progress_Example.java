/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2020 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package _Testing;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.filter.GenericFilter;
import imagingbook.lib.filter.GenericFilterScalar;
import imagingbook.lib.filter.GenericFilterScalarSeparable;
import imagingbook.lib.filter.GenericFilterVector;
import imagingbook.lib.filter.GenericFilterVectorSeparable;
import imagingbook.lib.filter.linear.Kernel2D;
import imagingbook.lib.filter.linear.LinearFilter;
import imagingbook.lib.image.data.PixelPack;
import imagingbook.lib.image.data.PixelPack.PixelSlice;
import imagingbook.lib.util.progress.ProgressMonitor;
import imagingbook.lib.util.progress.ij.ProgressBarMonitor;

/**
 * This ImageJ plugin shows how to construct a generic linear filter
 * using the classes {@link LinearFilter} and {@link Kernel2D}.
 * This plugin works for all types of images.
 * 
 * @author WB
 *
 */
public class Filter_Show_Progress_Example implements PlugInFilter {
	
    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    public void run(ImageProcessor ip) {
    	//GenericFilter filter = new MyFilterScalar(); //new FilterShowProgressExample();
    	//GenericFilter filter = new MyFilterVector(); 
    	//GenericFilter filter = new MyFilterVectorSeparable();
    	GenericFilter filter = new MyFilterScalarSeparable();
    	
    	IJ.log("RUNNING " + filter.getClass().getSimpleName());
    	try (ProgressMonitor m = new ProgressBarMonitor(filter)) {
			filter.applyTo(ip);
			IJ.log(String.format("elapsed time: %.3fs", m.getElapsedTime()));
		}
		
    }
    
    // ----------------------------------------------------------------
    // ----------------------------------------------------------------
    
    static class MyFilterScalar extends GenericFilterScalar {
    	// variables for progress reporting
    	

    	@Override
    	protected float doPixel(PixelSlice plane, int u, int v) {
    		dummyWork();
    		return plane.getVal(u, v);
    	}
    	
    	@Override
    	protected int passesRequired() {
    		return 2;
    	}

    }
    
    // ------------------------------------------------------------------
    
    static class MyFilterVector extends GenericFilterVector {
    	// variables for progress reporting

		@Override
		protected float[] doPixel(PixelPack source, int u, int v) {
			dummyWork();
			return source.getVec(u, v);
		}
    	
    	@Override
    	protected int passesRequired() {
    		return 2;
    	}
    }
    
    // ------------------------------------------------------------------
    
    static class MyFilterVectorSeparable extends GenericFilterVectorSeparable {
    	// variables for progress reporting


		@Override
		protected float[] doPixelX(PixelPack source, int u, int v) {
			dummyWork();
			return source.getVec(u, v);
		}

		@Override
		protected float[] doPixelY(PixelPack pack, int u, int v) {
			dummyWork();
			return pack.getVec(u, v);
		}
		
		@Override
		protected final int passesRequired() {
			return 1;	// do exactly 2 passes
		}
    }
    
 // ------------------------------------------------------------------
    
    static class MyFilterScalarSeparable extends GenericFilterScalarSeparable {
    	// variables for progress reporting


		@Override
		protected float doPixelX(PixelSlice source, int u, int v) {
			dummyWork();
			return source.getVal(u, v);
		}

		@Override
		protected float doPixelY(PixelSlice pack, int u, int v) {
			dummyWork();
			return pack.getVal(u, v);
		}
		
		@Override
		protected final int passesRequired() {
			return 2;	// do exactly 2 passes
		}
    }
    
 // ------------------------------------------------------------------
    
    
	private static float dummyWork() {
		int maxCnt = 5000;
    	@SuppressWarnings("unused")
		int cnt = 0;
		float sum = 0;
		for (int i = 0; i < maxCnt; i++) {
			cnt = i;
			sum += (float) Math.sqrt(i); // some dummy work
		}
		cnt = 0;
		return sum;
	}
 

}
