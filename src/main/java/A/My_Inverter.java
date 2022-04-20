package A;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class My_Inverter implements PlugInFilter {

	public int setup(String args, ImagePlus im) {
		return DOES_8G; // this plugin accepts 8-bit grayscale images 
	}

	public void run(ImageProcessor ip) {
		int M = ip.getWidth();
		int N = ip.getHeight();

		// iterate over all image coordinates
		for (int u = 0; u < M; u++) {
			for (int v = 0; v < N; v++) {
				int p = ip.getPixel(u, v);
				ip.putPixel(u, v, 255 - p);
			}
		}
	}

}