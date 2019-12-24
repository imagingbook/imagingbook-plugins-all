package Spectral_2.dft;

import imagingbook.lib.math.Matrix;


/**
 * Naive (slow) implementation of the DFT.
 * TODO: fix the scale setting (check Mathematica again, https://reference.wolfram.com/language/tutorial/FourierTransforms.html)
 * @author WB
 *
 */
public abstract class Dft1DImpl {
	
	final int M;			// size (length) of the signal or spectrum
	final ScalingMode sm;	
	final double[] cosTable;
	final double[] sinTable;
	
	private Dft1DImpl(int M, ScalingMode sm) {
		this.M = M;
		this.sm = sm;
		this.cosTable = makeCosTable();
		this.sinTable = makeSinTable();
	}
	
	private double[] makeCosTable() {
		double[] cosTable = new double[M];
		double theta = 2 * Math.PI / M;
		for (int i = 0; i < M; i++) {
			cosTable[i] = Math.cos(theta * i);
		}
		return cosTable;
	}

	private double[] makeSinTable() {
		double[] sinTable = new double[M];
		double theta = 2 * Math.PI / M;
		for (int i = 0; i < M; i++) {
			sinTable[i] = Math.sin(theta * i);
		}
		return sinTable;
	}
	
	// ----------------------------------------------------------------------
	
	public static class Float extends Dft1DImpl implements Dft1D.Float {
		
		private final float[] outRe;
		private final float[] outIm;
		
		public Float(int M, ScalingMode sm) {
			super(M, sm);
			this.outRe = new float[M];
			this.outIm = new float[M];
		}
		
		public Float(int M) {
			this(M, ScalingMode.Default);
		}
		
		@Override
		public void forward(float[] gRe, float[] gIm) {
			transform(gRe, gIm, true);
		}
		
		@Override
		public void inverse(float[] GRe, float[] GIm) {
			transform(GRe, GIm, false);
		}
		
		/**
		 * Forward DFT applied to a complex-valued input signal (forward = true)
		 * or inverse DFT applied to a complex-valued spectrum (forward = false).
		 * Works in-place, the input arrays contain the transformed data.
		 * 
		 * @param inRe	real part of the signal	or spectrum (must be of length M)
		 * @param inIm	imaginary part of the signal or spectrum (must be of length M or null)
		 * @param forward set true for forward transform, false for inverse transform
		 * @param scale a custom factor for scaling the transform values
		 */
		public void transform(float[] inRe, float[] inIm, boolean forward) {
			if (M != inRe.length || M != inIm.length) {
				throw new IllegalArgumentException(String.format("Dft1d: length of input signal g (%d) must be %d", inRe.length, M));
			}
			final double scale = sm.getScale(M, forward);
			for (int u = 0; u < M; u++) {
				double sumRe = 0;
				double sumIm = 0;
				for (int m = 0; m < M; m++) {
					final double re = inRe[m];
					final double im = (inIm == null) ? 0 : inIm[m];
					final int k = (u * m) % M;
					final double cosPhi = cosTable[k];
					final double sinPhi = (forward) ? -sinTable[k] : sinTable[k];
					// complex multiplication: (gRe + i gIm) * (cosPhi + i sinPhi)
					sumRe += re * cosPhi - im * sinPhi;
					sumIm += re * sinPhi + im * cosPhi;
				}
				outRe[u] = (float) (scale * sumRe);	
				outIm[u] = (float) (scale * sumIm);
			}
			System.arraycopy(outRe, 0, inRe, 0, M);
			System.arraycopy(outIm, 0, inIm, 0, M);
		}
	}

	// ----------------------------------------------------------------------
	
	public static class Double extends Dft1DImpl implements Dft1D.Double {
		
		private final double[] outRe;
		private final double[] outIm;
		
		public Double(int M, ScalingMode sm) {
			super(M, sm);
			this.outRe = new double[M];
			this.outIm = new double[M];
		}
		
		public Double(int M) {
			this(M, ScalingMode.Default);
		}
		
		@Override
		public void forward(double[] gRe, double[] gIm) {
			transform(gRe, gIm, true);
		}
		
		@Override
		public void inverse(double[] GRe, double[] GIm) {
			transform(GRe, GIm, false);
		}
		
		/**
		 * Forward DFT applied to a complex-valued input signal (forward = true)
		 * or inverse DFT applied to a complex-valued spectrum (forward = false).
		 * Works in-place, the input arrays contain the transformed data.
		 * 
		 * @param inRe	real part of the signal	or spectrum (must be of length M)
		 * @param inIm	imaginary part of the signal or spectrum (must be of length M or null)
		 * @param forward set true for forward transform, false for inverse transform
		 * @param scale a custom factor for scaling the transform values
		 */
		public void transform(double[] inRe, double[] inIm, boolean forward) {
			if (M != inRe.length || M != inIm.length) {
				throw new IllegalArgumentException(String.format("Dft1d: length of input signal g (%d) must be %d", inRe.length, M));
			}
			final double scale = sm.getScale(M, forward);
			for (int u = 0; u < M; u++) {
				double sumRe = 0;
				double sumIm = 0;
				for (int m = 0; m < M; m++) {
					final double re = inRe[m];
					final double im = (inIm == null) ? 0 : inIm[m];
					final int k = (u * m) % M;
					final double cosPhi = cosTable[k];
					final double sinPhi = (forward) ? -sinTable[k] : sinTable[k];
					// complex multiplication: (gRe + i gIm) * (cosPhi + i sinPhi)
					sumRe += re * cosPhi - im * sinPhi;
					sumIm += re * sinPhi + im * cosPhi;
				}
				outRe[u] = scale * sumRe;	
				outIm[u] = scale * sumIm;
			}
			System.arraycopy(outRe, 0, inRe, 0, M);
			System.arraycopy(outIm, 0, inIm, 0, M);
		}

//		@Override
//		public double[] getMag(double[] a) {
//			if (a.length != M) {
//				throw new IllegalArgumentException(String.format("Dft1d: length of supplied array (%d) must be %d", a.length, M));
//			}
//			for (int u = 0; u < M; u++) {
//				double re = outRe[u];
//				double im = outIm[u];
//				a[u] = Math.sqrt(re*re + im*im);
//			}
//			return a;
//		}
		
	}

	// ----------------------------------------------------------------------

	/*
	 * Direct implementation of the one-dimensional DFT for arbitrary signal lengths.
	 * This DFT uses the same definition as Mathematica. Example:
	 * Fourier[{1, 2, 3, 4, 5, 6, 7, 8}, FourierParameters -> {0, -1}]:
		{12.7279 + 0. i, 
		-1.41421 + 3.41421 i, 
		-1.41421 + 1.41421 i, 
		-1.41421 + 0.585786 i, 
		-1.41421 + 0. i, 
		-1.41421 - 0.585786 i, 
		-1.41421 - 1.41421 i, 
		-1.41421 - 3.41421 i}
	 */

	//test example
	public static void main(String[] args) {

		System.out.println("******************** Float test (DFT) ********************");
		{
			float[] re = { 1, 2, 3, 4, 5, 6, 7, 8 };
			float[] im = new float[re.length];

			System.out.println("original signal:");
			System.out.println("gRe = " + Matrix.toString(re));
			System.out.println("gIm = " + Matrix.toString(im));

			Dft1D.Float dft = new Dft1DImpl.Float(re.length);
			dft.forward(re, im);
//			float[] GRe = dft.getRe();
//			float[] GIm = dft.getIm();

			System.out.println("DFT spectrum:");
			System.out.println("GRe = " + Matrix.toString(re));
			System.out.println("GIm = " + Matrix.toString(im));

			dft.inverse(re, im);

			System.out.println("reconstructed signal:");
			System.out.println("gRe' = " + Matrix.toString(re));
			System.out.println("gIm' = " + Matrix.toString(im));
		}
		
		System.out.println();
		System.out.println("******************** Double test (DFT) ********************");

		{
			double[] re = { 1, 2, 3, 4, 5, 6, 7, 8 };
			double[] im = new double[re.length];

			System.out.println("original signal:");
			System.out.println("gRe = " + Matrix.toString(re));
			System.out.println("gIm = " + Matrix.toString(im));

			Dft1D.Double dft = new Dft1DImpl.Double(re.length);
			dft.forward(re, im);

			System.out.println("DFT spectrum:");
			System.out.println("GRe = " + Matrix.toString(re));
			System.out.println("GIm = " + Matrix.toString(im));

			dft.inverse(re, im);

			System.out.println("reconstructed signal:");
			System.out.println("gRe' = " + Matrix.toString(re));
			System.out.println("gIm' = " + Matrix.toString(im));
		}



	}

}
