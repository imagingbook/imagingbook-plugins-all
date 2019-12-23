package Spectral_2.dft;

/**
 * Interface specifying all one-dimensional DFT/FFT implementations.
 * @author WB
 *
 */
public interface DiscreteFourierTransform1D {
	
	public interface Float extends DiscreteFourierTransform1D {
		public float[] getRe();
		public float[] getIm();
		public float[] getRe(float[] a);
		public float[] getIm(float[] a);
		public float[] getMag();
		public float[] getMag(float[] a);
		public void forward(float[] gRe);
		public void forward(float[] gRe, float[] gIm);
		public void inverse(float[] GRe, float[] GIm);
	}
	
	public interface Double extends DiscreteFourierTransform1D {
		public double[] getRe();
		public double[] getIm();
		public double[] getRe(double[] a);
		public double[] getIm(double[] a);
		public double[] getMag();
		public double[] getMag(double[] a);
		public void forward(double[] gRe);
		public void forward(double[] gRe, double[] gIm);
		public void inverse(double[] GRe, double[] GIm);
	}
	
}
