package Tools;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;


public class Unlock_Image implements PlugIn {

	public void run(String arg0) {
		ImagePlus img = IJ.getImage();
		if (img.isLocked()) {
			img.unlock();
			IJ.log("unlocked image");
		}
	}

}
