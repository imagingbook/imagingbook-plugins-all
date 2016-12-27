

package Color_Quantization;


import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import Color_Quantization.lib.apache.OctreeQuantizer;


/**
 * Works, but bad results!
 * @author WB
 *
 */
public class Octree_Quantization_Apache implements PlugInFilter {
	
	static int NCOLORS = 16;
	
	public int setup(String arg, ImagePlus imp) {
		return DOES_RGB + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		ColorProcessor cp = (ColorProcessor) ip;
		IJ.log("creating octree quantizer");
		
		OctreeQuantizer oq = new OctreeQuantizer(cp, NCOLORS);
//		OctreeQuantizer oq = new OctreeQuantizer((int[]) cp.getPixels(), NCOLORS);
		IJ.log("quantizer is done");
		
		ColorProcessor cp2 = (ColorProcessor) cp.duplicate();
		int[] pixels2 = (int[]) cp2.getPixels();
		
		for (int i = 0; i < pixels2.length; i++) {
			pixels2[i] = oq.mapColor(pixels2[i]);
		}
		
		(new ImagePlus("Quantized", cp2)).show();
	}


}
