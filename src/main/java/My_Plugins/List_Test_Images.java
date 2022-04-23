package My_Plugins;

import java.net.URL;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import imagingbook.core.resource.ImageResource;
import imagingbook.sampleimages.RansacTestImage;

public class List_Test_Images implements PlugIn {

	@Override
	public void run(String arg) {
		for (ImageResource res : RansacTestImage.values()) {
			String relPath = res.getRelativePath();
			IJ.log(relPath);

			URL url = res.getURL();
			IJ.log("URL = " + url);
			IJ.log("inside JAR = " + res.isInsideJar());
			
//			ImagePlus im = new Opener().openImage(url.toString());
//			ImagePlus im = IJ.openImage(url.toString());
			ImagePlus im = res.getImage();
			im.show();
		}

	}
	
	

}
