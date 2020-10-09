package Tools;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import imagingbook.lib.ij.GuiTools;

/**
 * Resizes the window of the given image to fit an arbitrary, user-specified
 * magnification factor.
 * The resulting window size is limited by the current screen size.
 * The window size is reduced if too large but the given magnification factor
 * remains always unchanged.
 * 
 * @version 2020/10/08
 */
public class Zoom_Exact implements PlugIn {
	
	public void run(String arg) {
		ImagePlus im = WindowManager.getCurrentImage();
		if (im == null) {
			IJ.showMessage("No image open");
			return;
		}
		
		GenericDialog gd = new GenericDialog("Zoom Exact");
		gd.addNumericField("Magnification (%): ", GuiTools.getMagnification(im) * 100, 0);
		gd.showDialog();
		if (gd.wasCanceled())
			return;
		double mag = gd.getNextNumber() / 100.0;
		
		if (mag < 0.001) {
			IJ.showMessage(String.format("Out of range magnification:\n%.3f", mag));
			return;
		}
		
		if (GuiTools.zoomExact(im, mag)) {
			IJ.log(String.format("new magnification: %.3f",  GuiTools.getMagnification(im)));
		}
		else {
			IJ.showMessage(Zoom_Exact.class.getSimpleName() + " failed");
		}
	}
}