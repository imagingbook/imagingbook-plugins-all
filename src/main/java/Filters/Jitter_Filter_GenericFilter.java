package Filters;
import java.util.Random;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.filter.GenericFilter;
import imagingbook.lib.filter.GenericFilterVector;
import imagingbook.lib.image.data.PixelPack;

/**
 * Implementation of the "Jitter filter" based on the {@link GenericFilter}
 * class. Works for all image types, using nearest-border-pixel strategy.
 * The input image is destructively modified.
 * See the inner class {@link JitterFilter} for the actual implementation
 * of the filter.
 * 
 * @author wilbur
 * @version 2021/01/12
 */
public class Jitter_Filter_GenericFilter implements PlugInFilter {
	
	private final int rad = 3;	// the radius (should be user-specified)
		
	public int setup(String arg, ImagePlus im) {
		return DOES_ALL;
	}

	public void run(ImageProcessor ip) {
		GenericFilter gf = new JitterFilter();
		gf.applyTo(ip);
	}
	
	// --------------------------------------------------------------
	
	/**
	 * This inner class actually implements the Jitter filter,
	 * based on the functionality provided by {@link GenericFilter}.
	 */
	public class JitterFilter extends GenericFilterVector {
		private final int d = 2 * rad + 1;	// width/height of the "kernel"
		private final Random rnd = new Random();
		
		@Override
		protected float[] doPixel(PixelPack source, int u, int v) {
			int rx = rnd.nextInt(d) - rad;
			int ry = rnd.nextInt(d) - rad;
			return source.getVec(rx, ry);
		}
	}
	
}
