package util;


import java.io.File;
import javax.swing.filechooser.*;

/**
 * File filter for *.png files.
 *
 * @author Thilo Muth
 */
public class PngFileFilter extends FileFilter {

    private final static String png = "png";
    private final static String PNG = "PNG";

    /**
     * Accept all directories, *.png files.
     *
     * @param f
     * @return boolean
     */
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = getExtension(f);
        if (extension != null) {
            if (extension.equals(PngFileFilter.png) ||
                    extension.equals(PngFileFilter.PNG)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * The description of this filter
     *
     * @return String
     */
    public java.lang.String getDescription() {
        return "Image (PNG Format) (*.png)";
    }

    /**
     * Get the extension of a file.
     *
     * @param f
     * @return String - the extension of the file f
     */
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }
}
