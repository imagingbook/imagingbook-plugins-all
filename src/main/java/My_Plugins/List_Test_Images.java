package My_Plugins;

import java.net.URI;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import imagingbook.core.resource.ImageResource;
import imagingbook.testimages.RansacTestImage;

public class List_Test_Images implements PlugIn {

	@Override
	public void run(String arg) {
		for (ImageResource res : RansacTestImage.values()) {
			String relPath = res.getRelativePath();
			IJ.log(relPath);

			URI uri = res.getURI();
			IJ.log("uri = " + uri);
			IJ.log("inside JAR = " + res.isInsideJar());
			
//			ImagePlus im = new Opener().openImage(uri.toString());
//			ImagePlus im = IJ.openImage(uri.toString());
			ImagePlus im = res.getImage();
			im.show();
		}

	}
	
	

}
