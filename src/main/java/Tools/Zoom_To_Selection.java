package Tools;

import java.awt.Rectangle;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.Roi;
import ij.plugin.PlugIn;
import imagingbook.lib.ij.GuiTools;


/**
 * Zooms the current image, such that the given selection (ROI) fits
 * the image window.
 * The top-left anchor corner of the new view is set to
 * the top-left corner of the current ROI (which may be of any type).
 * The magnification factor is determined from the width/height of the
 * ROI's bounding rectangle, such that the selected box always fits entirely
 * into the image window.
 * If the resulting view has no complete coverage by the source image,
 * the plugin does nothing.
 * The size of the existing image window is never changed.
 * <br>
 * The functionality is similar to ImageJ's
 * "Image &rarr; Zoom &rarr; To Selection" operator which, however, only
 * supports a limited number of predefined magnification values.
 * 
 * 
 * @author WB
 * @version 2020/10/08
 */
public class Zoom_To_Selection implements PlugIn {
	
	@Override
	public void run(String arg) {
		ImagePlus im = WindowManager.getCurrentImage();
		if (im == null) {
			IJ.showMessage("No image open");
			return;
		}
		Roi roi = im.getRoi();
		if (roi == null) {
			IJ.showMessage("Selection required");
			return;
		}
		
		Rectangle bounds = roi.getBounds();
		if (bounds.width == 0 || bounds.height == 0) {
			IJ.showMessage("Selected width and height must not be zero");
			return;
		}
		
		double xmag = (double) im.getWidth() / bounds.width;
		double ymag = (double) im.getHeight() / bounds.height;	
		double mag =  Math.min(xmag, ymag);
		
		Rectangle srcRect = GuiTools.setImageView(im, mag, bounds.x, bounds.y);
		
		if (srcRect == null) {
			IJ.showMessage("Failed to set view");
		}
	}
	
}
