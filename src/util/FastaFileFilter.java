package util;


import java.io.File;
import javax.swing.filechooser.*;

/**
 * File filter for *.mfa files.
 *
 * @author Thilo Muth
 */
public class FastaFileFilter extends FileFilter {

    private final static String multifasta = "mfa";
    private final static String MULTIFASTA = "MFA";

    /**
     * Accept all directories, *.fasta files.
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
            if (extension.equals(FastaFileFilter.multifasta) ||
                    extension.equals(FastaFileFilter.MULTIFASTA)) {
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
        return "Alignment (FASTA Format) (*.mfa)";
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
