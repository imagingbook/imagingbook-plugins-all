package Corner_Detection;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.lang.reflect.Field;

import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;

/**
 * Resizes the window of the given image to fit an arbitrary, user-specified
 * magnification factor.
 * The resulting window size is limited by the current screen size.
 * The window size is reduced if too large but the given magnification factor
 * remains always unchanged.
 * 
 * Adapted from https://albert.rierol.net/plugins/Zoom_Exact.java
 * Copyright Albert Cardona @ 2006
 * General Public License applies.
 * Use at your own risk.
 * 
 * @version 2020/10/08
 */
public class Zoom_Exact implements PlugIn {
	
	private static final int W_MARGIN = 10;	// width margin (wrt. screen size)
	private static final int H_MARGIN = 30; // height margin (wrt. screen size)

	public void run(String arg) {
		ImagePlus im = WindowManager.getCurrentImage();
		if (null == im)
			return;
		ImageWindow win = im.getWindow();
		if (null == win)
			return;
		ImageCanvas ic = win.getCanvas();
		if (null == ic)
			return;
		
		GenericDialog gd = new GenericDialog("Exact Zoom");
		gd.addNumericField("Zoom (%): ", ic.getMagnification() * 100, 0);
		gd.showDialog();
		if (gd.wasCanceled())
			return;
		
		double mag = gd.getNextNumber() / 100.0;
		
		if (mag <= 0.0)
			mag = 1.0;
		
		ic.setMagnification(mag);

		// see if it fits to the screen
		double w = im.getWidth() * mag;
		double h = im.getHeight() * mag;
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	
		w = Math.min(h, screen.width - W_MARGIN);
		h = Math.min(h, screen.height - H_MARGIN);
				
		Rectangle sourceRect = new Rectangle(0, 0, (int) (w / mag), (int) (h / mag));
		try {
			// by reflection:
			Field f_srcRect = ic.getClass().getDeclaredField("srcRect");
			f_srcRect.setAccessible(true);
			f_srcRect.set(ic, sourceRect);
			ic.setSize((int) w, (int) h);
			win.pack();
			im.repaintWindow();	//c.repaint();
		} catch (Exception  e) { e.printStackTrace(); };
		
	}
}