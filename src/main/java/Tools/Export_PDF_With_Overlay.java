package Tools;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.io.SaveDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;


/**
 * This ImageJ plugin exports the current image and its attached
 * vector graphic overlay (if existent) as a PDF file.
 * This plugin requires iText to be in the Java library path:
 * place itextpdf-5.5.8.jar (or newer) in {@code ImageJ/jars/}.
 * iText releases can be downloaded from here:
 * <a href="https://github.com/itext/itextpdf/releases">
 * https://github.com/itext/itextpdf/releases</a>.
 * 
 * @author W. Burger
 * @version 2016/01/09
 */
public class Export_PDF_With_Overlay implements PlugInFilter {

	/**
	 * Used to draw graphic strokes with zero stroke width.
	 */
	public static double DefaultStrokeWidth = 0.01;		// substitute for zero-width strokes
	
	/**
	 * Set true to have a default ICC profile (sRGB) added
	 * to the PDF file. Solves problems with viewing
	 * files in Acrobat X+.
	 */
	public static boolean AddIccProfile = true;
	
	public static String DefaultFileExtension = ".pdf";
	
	private static String CurrentOutputDirectory = IJ.getDirectory("home");
	private ImagePlus img;

	public int setup(String arg0, ImagePlus img) {
		this.img = img;
		return DOES_ALL + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		if (!verifyIText()) {
			IJ.error("This plugin requires iText to be installed!\n" +
					"Download itextpdf-5.5.8.jar (or newer) from\n" +
					"https://github.com/itext/itextpdf/releases\n" +
					"and place in ImageJ/jars/");
			return;
		}
		
		if (img.getStackSize() != 1) {
			IJ.error("Can only export single images (no stacks)!");
			return;
		}
		
		String dir = IJ.getDirectory("image");
		if (dir == null) 
			dir = CurrentOutputDirectory;
		
		String name = stripFileExtension(img.getTitle());
			
		String path = askForFilePath(dir, name, "Save as PDF");
		if (path == null) {
			return;
		}
		
		String finalPath = createPdf(img, path);
		if (finalPath == null) 
			IJ.log("PDF export failed");
		else
			IJ.log("PDF exported to " + finalPath);
	}
	
	// ----------------------------------------------------------------------
	
	/** Saves a PDF of the supplied image.
	 * 
	 * @param im the image (of type {@link ij.ImagePlus}), possibly with attached vector overlay
	 * @param path the path to save to
	 * @return the complete path to the created PDF file or {@code null} 
	 * if the file could not be saved
	 */
	public String createPdf(ImagePlus im, String path) {
		final int width  = im.getWidth();
		final int height = im.getHeight();
		
		// step 1: create the PDF document
		Document document = new Document(new Rectangle(width, height));
		try {
			// step 2: create a PDF writer
			PdfWriter writer = PdfWriter.getInstance(document,	new FileOutputStream(path));
			// step 3: open the document
			document.open();
			// step 4: create a template and the associated Graphics2D context
			PdfContentByte cb = writer.getDirectContent();
			
			// optional: set sRGB default viewing profile
			if (AddIccProfile) {
				byte[] iccdata = java.awt.color.ICC_Profile.getInstance(ColorSpace.CS_sRGB).getData();	
				com.itextpdf.text.pdf.ICC_Profile icc = com.itextpdf.text.pdf.ICC_Profile.getInstance(iccdata);
				writer.setOutputIntents("Custom", null, "http://www.color.org", "sRGB IEC61966-2.1", icc);
			}
						
			// insert the image
			com.itextpdf.text.Image pdfImg = com.itextpdf.text.Image.getInstance(im.getImage(), null);
			pdfImg.setAbsolutePosition(0, 0);
			pdfImg.scaleToFit(width, height); 		
			cb.addImage(pdfImg);
			
			// optional: draw the vector overlay
			Overlay overlay = im.getOverlay();
			if (overlay != null) {
				Graphics2D g2 = new PdfGraphics2D(cb, width, height);
				Roi[] roiArr = overlay.toArray();
				for (Roi roi : roiArr) {
					float sw = roi.getStrokeWidth();
					if (sw < 0.001f) {	// sometimes stroke width is simply not set (= 0)
						roi.setStrokeWidth(DefaultStrokeWidth);	// temporarily change stroke width
					}
					ImagePlus tmpIm = roi.getImage();
					roi.setImage(null); 	// trick learned from Wayne to ensure magnification is 1
					roi.drawOverlay(g2);	// replacement (recomm. by Wayne)
					roi.setImage(tmpIm);
					roi.setStrokeWidth(sw); // restore original stroke width
				}
				g2.dispose();
			}
			
		} catch (DocumentException de) {
			IJ.log(de.getMessage());
		} catch (IOException ioe) {
			IJ.log(ioe.getMessage());
		}
 
		// step 5: close the document
		document.close();
		return path;
	}
	
	// ----------------------------------------------------------------------
	
    private String askForFilePath(String dir, String name, String title) {
    	String extension = DefaultFileExtension;
    	SaveDialog od = new SaveDialog(title, dir, name, extension);
    	dir  = od.getDirectory();
    	name = od.getFileName();
    	if(name != null) {
    		CurrentOutputDirectory = dir;
    		return new File(dir, name).getAbsolutePath();
    	}
    	else
    		return null;
    }
	
	private String stripFileExtension(String fileName) {
		int dotInd = fileName.lastIndexOf('.');
		// if dot is in the first position,
		// we are dealing with a hidden file rather than an DefaultFileExtension
		return (dotInd > 0) ? fileName.substring(0, dotInd) : fileName;
	}
	
	
	private boolean verifyIText() {
		try {
			if (Class.forName("com.itextpdf.text.Document") != null) {
				return true;
			}
		} catch (ClassNotFoundException e) { }
		return false;
	}
}

