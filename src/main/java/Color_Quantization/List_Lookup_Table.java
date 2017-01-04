package Color_Quantization;

import java.awt.image.IndexColorModel;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.text.TextWindow;

/**
 * https://imagej.nih.gov/ij/plugins/download/Lut_Lister.java
 * @author Wayne Rasband
 *
 */
public class List_Lookup_Table implements PlugInFilter {

	public int setup(String arg, ImagePlus imp) {
		return DOES_ALL - DOES_RGB + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		listLut(ip);
	}

	void listLut(ImageProcessor ip) {
		IndexColorModel icm = (IndexColorModel) ip.getColorModel();
		int size = icm.getMapSize();
		byte[] r = new byte[size];
		byte[] g = new byte[size];
		byte[] b = new byte[size];
		icm.getReds(r);
		icm.getGreens(g);
		icm.getBlues(b);
		StringBuffer sb = new StringBuffer();
		String headings = "Index\tRed\tGreen\tBlue";
		for (int i = 0; i < size; i++)
			sb.append(i + "\t" + (r[i] & 255) + "\t" + (g[i] & 255) + "\t" + (b[i] & 255) + "\n");
		new TextWindow("LUT", headings, sb.toString(), 250, 400);
	}

}
