package Spectral_2.dft;

/**
 * Interface specifying all one-dimensional DFT/FFT implementations.
 * @author WB
 *
 */
public interface Dft2D {
	
	public interface Float extends Dft2D {
		public void forward(float[][] gRe, float[][] gIm);
		public void inverse(float[][] GRe, float[][] GIm);
	}
	
	public interface Double extends Dft2D {
		public void forward(double[][] gRe, double[][] gIm);
		public void inverse(double[][] GRe, double[][] GIm);
	}
	
}
