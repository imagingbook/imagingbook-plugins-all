package Spectral_2.dft;

/**
 * Interface specifying all one-dimensional DFT/FFT implementations.
 * @author WB
 *
 */
public interface DiscreteFourierTransform2D {
	
	public interface Float extends DiscreteFourierTransform2D {
		public void forward(float[][] gRe, float[][] gIm);
		public void inverse(float[][] GRe, float[][] GIm);
	}
	
	public interface Double extends DiscreteFourierTransform2D {
		public void forward(double[][] gRe, double[][] gIm);
		public void inverse(double[][] GRe, double[][] GIm);
	}
	
}
