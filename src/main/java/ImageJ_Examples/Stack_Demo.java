package ImageJ_Examples;

import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * This ImageJ plugin demonstrates how to iterate over the frames (slices)
 * of an image stack. In this case each frame is simply inverted. No new
 * frames are added to the stack. The result is automatically re-displayed
 * (unless {@literal NO_CHANGES} is specified).
 * @author W. Burger
 *
 */
public class Stack_Demo implements PlugInFilter {
	
	ImagePlus im = null;	// keep a reference to the associated ImagePlus object
	
	public int setup(String args, ImagePlus im) {
		this.im = im;
		return DOES_8G + STACK_REQUIRED;
	}

	public void run(ImageProcessor ignored) {
		ImageStack stack = im.getImageStack();
		int K = stack.getSize();
		for (int k = 1; k <= K; k++) {	// NOTE: slices are numbered from 1,...,K !!
			ImageProcessor ip = stack.getProcessor(k);
			ip.invert();
		}
	}
}
