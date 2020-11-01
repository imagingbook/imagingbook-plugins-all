package _XXX;

import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.FloatProcessor;
import imagingbook.lib.image.Filter;
import imagingbook.lib.math.Matrix;

public class HighOrderDerivatives_Test implements PlugIn {

	@Override
	public void run(String arg) {
		
		FloatProcessor fp = new FloatProcessor(21, 21);
		fp.setf(10, 10, 1.0f);
		
		float[] hx = {1, -8, 0, 8, -1};
		
		Filter.convolveX(fp, Matrix.multiply(1f/12, hx));
		Filter.convolveY(fp, Matrix.multiply(1f/12, hx));
		
		new ImagePlus("fp", fp).show();
		
	}

}
