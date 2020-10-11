package _XXX;

import ij.IJ;
import ij.plugin.PlugIn;
import imagingbook.lib.ij.GenericDialogPlus;

public class GenericDialogPlus_Example implements PlugIn {
	
	private enum MyEnum {
		Alpha, Beta, Gamma;
	}

	@Override
	public void run(String arg) {
		
		GenericDialogPlus gd = new GenericDialogPlus("Testing enums");
		gd.addEnumChoice("Choose from here", MyEnum.Beta);
		gd.showDialog();
		if (gd.wasCanceled())
			return;
		MyEnum m = gd.getNextEnumChoice(MyEnum.class);
		IJ.log("Your choice was " + m);
	}

}
