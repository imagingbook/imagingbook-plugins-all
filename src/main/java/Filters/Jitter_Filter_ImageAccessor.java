package Filters;
import java.util.Random;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import imagingbook.lib.image.access.ImageAccessor;
import imagingbook.lib.image.access.OutOfBoundsStrategy;

/**
 * Jitter filter implemented with 'ImageAccessor' for transparent
 * image access.
 * Works for all image types, using nearest-border-pixel strategy.
 * The input image is destructively modified.
 * 
 * @author wilbur
 * @version 2016/11/01
 */
public class Jitter_Filter_ImageAccessor implements PlugInFilter {
	
	final int rad = 3;	// the radius (should be user-specified)
	
	public int setup(String arg, ImagePlus im) {
		return DOES_ALL;
	}

	public void run(ImageProcessor ip1) {
		final int w = ip1.getWidth();
		final int h = ip1.getHeight();
		final int d = 2 * rad + 1;	// width/height of the "kernel"
		
		ImageProcessor ip2 = ip1.duplicate();
		ImageAccessor ia1 = ImageAccessor.create(ip1);
		ImageAccessor ia2 = ImageAccessor.create(ip2, OutOfBoundsStrategy.NEAREST_BORDER, null);

		Random rnd = new Random();
		
		for (int u = 0; u < w; u++) {
			for (int v = 0; v < h; v++) {
				int rx = rnd.nextInt(d) - rad;
				int ry = rnd.nextInt(d) - rad;
				// pick a random position inside the current support region
				float[] p = ia2.getPix(u + rx, v + ry);
				// replace the current center pixel
				ia1.setPix(u, v, p);
			}
		}
		
		ia2 = null;
		ip2 = null;
	}
	
}
