package Spectral_2.dft;

/**
 * See https://reference.wolfram.com/language/tutorial/FourierTransforms.html
 * @author WB
 *
 */
public enum ScalingMode {
	Default {
		@Override
		public double getScale(int M, boolean forward) {
			return 1.0 / Math.sqrt(M);
		}
	}, 
	DataAnalysis {
		@Override
		public double getScale(int M, boolean forward) {
			return forward ? 1.0 / M : 1.0;
		}
	}, 
	SignalProcessing {
		@Override
		public double getScale(int M, boolean forward) {
			return forward ? 1.0 : 1.0 / M;
		}
	};
	
	public abstract double getScale(int M, boolean forward);
}
