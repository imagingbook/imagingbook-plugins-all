package Color_Quantization;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Show_Lookup_Table implements  PlugInFilter {

	@Override
	public int setup(String arg0, ImagePlus im) {
		return DOES_8G + DOES_8C;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		ImageProcessor ip2 = ip.createProcessor(256, 256); //ip.convertToByteProcessor();
		int k = 0;
		for (int row = 0; row < 16; row++) {
			int v = row * 16;
			for (int col = 0; col < 16; col++) {
				int u = col * 16;
				// fill the patch with value k
				for (int j = 0; j < 16; j++) {
					for (int i = 0; i < 16; i++) {
						ip2.set(u + i, v + j, k);
					}
				}
				k = k + 1;
			}
			
		}
		
		new ImagePlus("ColorTable", ip2).show();
	}

}
