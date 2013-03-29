package util;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

import view.AlignPanel;

/**
 * With this class the alignment panel can be exported to a PNG file.
 * @author Thilo Muth
 *
 */
public class ImageExporter {

	private BufferedImage image;
	private Graphics g;
	private FileOutputStream out;
	/**
	 * Constructor of the image exporter.
	 * @param alignPanel
	 * @param title
	 * @param width
	 * @param height
	 * @param file
	 */
	public ImageExporter(AlignPanel alignPanel, String title, int width, int height, File file){
	    if (file != null) {
	      try {
	        out = new FileOutputStream(file);
	        initPNGFile(width, height);
	      } catch (Exception ex) {
	        System.out.println("Error exporting PNG file.");
	      }
	    }
	}
	
	/**
	 * Sets up the PNG file settings.
	 * @param width
	 * @param height
	 */
	private void initPNGFile(int width, int height) {
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		g = image.getGraphics();		
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}
	
	/**
	 * Write the image to a PNG file.
	 */
	public void write() {
		try {
			ImageIO.write(image, "png", out);
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	 public Graphics getGraphics()
	  {
	    return g;
	  }

}
