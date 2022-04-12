package My_Plugins;

import java.net.URI;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import imagingbook.testimages.MyImages;

public class List_Test_Images implements PlugIn {

	@Override
	public void run(String arg) {
		for (MyImages mi : MyImages.values()) {
			String relPath = mi.getRelativePath();
			IJ.log(relPath);
//			URI uri = MyImages.getResourceUri(relPath);
			URI uri = mi.getURI();
			IJ.log("uri = " + uri);
			IJ.log("inside JAR = " + mi.isInsideJar());
			
//			ImagePlus im = new Opener().openImage(uri.toString());
			ImagePlus im = IJ.openImage(uri.toString());
			im.show();
		}
		
		MyImages res = MyImages.noisyCircles;
		res.getURI();
		URI uir = MyImages.noisyCircles.getURI();
		
	}

}
