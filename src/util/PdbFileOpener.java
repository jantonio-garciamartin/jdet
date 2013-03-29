package util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.zip.GZIPInputStream;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * This class is for loading an compressed PDB file from the FTP Server and uncompressing it.
 * The user gives the PDB 4-letter code as parameter.
 * @author Thilo Muth
 *
 */
public class PdbFileOpener implements Runnable {	
	
	/**
	 * Holds the string with the pdbCode.
	 */
	private String pdbCode;
	
	/**
	 * Holds the string with the GZIP PDB file.
	 */
	private String gzipFile;
	
	/**
	 * Holds the string of the (uncompressed) PDBfile;
	 */
	private String pdbFile;
	
	/**
	 * Holds the URL to the PDB file.
	 */
	private URL url;
	
	/**
	 * Holds the thread for downloading and uncompressing PDB file.
	 */
	private Thread thread;
	
	/**
	 * Holds the downloaded amount of data.
	 */
    private int downloaded; 

	/**
	 * Text area for displaying download status.
	 */
    private JTextArea progressInfo = null; 
    
	/**
	 * Constructor of the PdbFileOpener. Gets the PDB code.
	 * @param pdbCode
	 */
	public PdbFileOpener(String pdbCode){
		this.pdbCode = pdbCode;
		this.gzipFile = "pdb" +pdbCode +".ent.gz";	
		getURL();
		start();
	}

	/**
	 * Constructor of the PdbFileOpener. Gets the PDB code.
	 * @param pdbCode
	 */
	public PdbFileOpener(String pdbCode, JTextArea progressInfo){
		this.pdbCode = pdbCode;
		this.gzipFile = "pdb" +pdbCode +".ent.gz";
		this.progressInfo = progressInfo;
		getURL();
		start();
	}
	
	/**
	 * Start the thread.
	 */
	private void start() {
		thread = new Thread(this);
		thread.start();
	}
	
	/**
	 * Get this download's progress.
	 * 
	 * @return int
	 */
	public int getDownloaded() {
		return downloaded;
	}
	  
	/**
	 * Retrieves the URL with the FTP location.
	 */
	private void getURL(){
		try {
			url = new URL(Constants.FTPSERVER + gzipFile);
		} catch (MalformedURLException e) {
			JOptionPane.showMessageDialog(null,
					"Couldn't get the URL of the FTP...\n"
							+ "Use a valid filename for the file...",
					e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}
	/**
	 * This method downloads the compressed PDBfile from the Server.
	 * @param url
	 */
	private void downloadFile(){		
		try {
			URLConnection con = url.openConnection();
			BufferedInputStream in = new BufferedInputStream(con.getInputStream());
			FileOutputStream out = new FileOutputStream("pdb" +pdbCode +".ent.gz");			
			downloaded = 0;
			int i = 0;
			byte[] buffer = new byte[1024];
			while ((i = in.read(buffer)) >= 0) {
				// Write buffer to file
				out.write(buffer, 0, i);
				downloaded += i;
				if(progressInfo!= null){
					progressInfo.setText(downloaded + "kB downloaded...");
				}				
			}
			out.close();
			in.close();
		} catch (UnknownHostException e) {
			if(progressInfo!= null){
				progressInfo.setText("Download error.");
			}
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(null,
							"The PDB file couldn't be retrieved from the server! \n"
									+ "Please check your connection to server...",
							"Unknown host error", JOptionPane.ERROR_MESSAGE);
				}
			});
			return;
		} catch (Exception ex){
			if(progressInfo!= null){
				progressInfo.setText("Download error.");
			}
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(null,
							"The PDB file couldn't be downloaded.! \n"
									+ "Please use a valid 4-digit-code for an existing PDB file.",
							"Download file error", JOptionPane.ERROR_MESSAGE);
				}
			});
			return;
		}
		if(progressInfo!= null){
			progressInfo.setText("Download complete.");
		}				
		
	}
	
	/**
	 * This method uncompresses the GZIP file.
	 */
	private void uncompressFile(){
		 try {	
		        // GZIP Inputstream
		        GZIPInputStream in = new GZIPInputStream(new FileInputStream(gzipFile));
		    
		        // Outputstream
		        pdbFile = pdbCode + ".pdb";
		        OutputStream out = new FileOutputStream(pdbFile);
		    
		        // Transfer bytes from the compressed file to the output file
		        byte[] buffer = new byte[1024];
		        int len;
		        while ((len = in.read(buffer)) > 0) {
		            out.write(buffer, 0, len);
		        }
		        in.close();
		        out.close();
		    } catch (IOException e) {
		    }
	}
	
	/**
	 *  Runs the thread: Downloading + Uncompressing the PDB file.
	 */
	public void run(){		
		downloadFile();
		uncompressFile();
	}
	
	/**
	 * Returns the string of the PDB file.
	 * @return pdbFile String
	 */
	public String getPdbFile() {
		return pdbFile;
	}
	
	/**
	 * Returns the thread.
	 * @return thread Thread
	 */
	public Thread getThread() {
		return thread;
	}
}
