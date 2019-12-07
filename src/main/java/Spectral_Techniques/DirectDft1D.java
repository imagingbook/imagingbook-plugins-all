/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 *  image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause 
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause). 
 * Copyright (c) 2006-2016 Wilhelm Burger, Mark J. Burge. All rights reserved. 
 * Visit http://imagingbook.com for additional details.
 *******************************************************************************/
package Spectral_Techniques;

import java.util.Locale;

import imagingbook.lib.math.Complex;

/** 
 * Direct implementation of the one-dimensional DFT for arbitrary signal lengths.
 * This DFT uses the same definition as Mathematica. Example:
 * Fourier[{1, 2, 3, 4, 5, 6, 7, 8}, FourierParameters = {0, -1}]:
	{12.7279 + 0. i, 
	-1.41421 + 3.41421 i, 
	-1.41421 + 1.41421 i, 
	-1.41421 + 0.585786 i, 
	-1.41421 + 0. i, 
	-1.41421 - 0.585786 i, 
	-1.41421 - 1.41421 i, 
	-1.41421 - 3.41421 i}
	@deprecated
*/
public abstract class DirectDft1D {

	//direct DFT implementation 
	static Complex[] DFT(Complex[] g, boolean forward) {
		int M = g.length;
		double s = 1 / Math.sqrt(M); //common scale factor
		Complex[] G = new Complex[M];
		for (int m = 0; m < M; m++) {
			double sumRe = 0;
			double sumIm = 0;
			double phim = 2 * Math.PI * m / M;
			for (int u = 0; u < M; u++) {
				double gRe = g[u].re;
				double gIm = g[u].im;
				double cosw = Math.cos(phim * u);
				double sinw = Math.sin(phim * u);
				if (!forward) // inverse transform
					sinw = -sinw;
				//complex mult: (gRe + i gIm) * (cosw + i sinw)
				sumRe += gRe * cosw + gIm * sinw;
				sumIm += gIm * cosw - gRe * sinw;
			}
			G[m] = new Complex(s * sumRe, s * sumIm);
		}
		return G;
	}
	
	public static Complex[] makeComplexVector(double[] signal) {
		int M = signal.length;
		Complex[] g = new Complex[M];
		for (int i = 0; i < M; i++) {
			g[i] = new Complex(signal[i], 0);
		}
		return g;
	}
	
	public static void printComplexVector(Complex[] g, String title) {
    	System.out.println("Listing of " + title);
		for (int i = 0; i < g.length; i++) {
			if (g[i] == null)
				System.out.println(String.format("%d: ********", i));
			else {
				double gr = g[i].re;
				double gi = g[i].im;
				if (gi >= 0) {
					System.out.println(String.format(Locale.US, "%d: %6.2f + %6.2fi", i, gr, Math.abs(gi)));
				}
				else {
					System.out.println(String.format(Locale.US, "%d: %6.2f - %6.2fi", i, gr, Math.abs(gi)));
				}
			}
		}
	}
	
	//test example
	public static void main(String[] args) {
		double[] signal = { 1, 2, 3, 4, 5, 6, 7, 8 };
		Complex[] g = makeComplexVector(signal);

		printComplexVector(g, "Signal");
		
		//compute forward DFT
		Complex[] G = DFT(g, true);
		printComplexVector(G, "Spectrum");

		//compute inverse DFT
		Complex[] iG = DFT(G, false);
		printComplexVector(iG, "Reconstructed signal");
	}
}

