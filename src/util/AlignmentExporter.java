package util;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import model.SequenceImpl;
import view.AlignFrame;
import view.AlignViewProps;

public class AlignmentExporter {
	
	private static String lastSelectedFolder = "user.home";
	
	
	/**
	 * This method manages the Fasta File export of current selection.
	 * @param evt
	 */
	public static void exportFasta(AlignFrame alignFrame, AlignViewProps viewProps, ActionEvent evt, boolean saveSelection){
		JFileChooser chooser = new JFileChooser(lastSelectedFolder);
        chooser.setFileFilter(new FastaFileFilter());
        chooser.setMultiSelectionEnabled(false);
        chooser.setDialogTitle("Export MFA File Details");


        File selectedFile;
        chooser.requestFocusInWindow();
        int returnVal = chooser.showSaveDialog(alignFrame);

        if (returnVal == JFileChooser.APPROVE_OPTION) {

            selectedFile = chooser.getSelectedFile();

            if (!selectedFile.getName().toLowerCase().endsWith(".mfa")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".mfa");
            }

            while (selectedFile.exists()) {
                int option = JOptionPane.showConfirmDialog(alignFrame,
                        "The  file " + chooser.getSelectedFile().getName() +
                        " already exists. Replace file?",
                        "Replace File?", JOptionPane.YES_NO_CANCEL_OPTION);

                if (option == JOptionPane.NO_OPTION) {
                    chooser = new JFileChooser(lastSelectedFolder);
                    chooser.setFileFilter(new PngFileFilter());
                    chooser.setMultiSelectionEnabled(false);
                    chooser.setDialogTitle("Export FASTA File Details");

                    returnVal = chooser.showSaveDialog(alignFrame);

                    if (returnVal == JFileChooser.CANCEL_OPTION) {
                        return;
                    } else {
                        selectedFile = chooser.getSelectedFile();

                        if (!selectedFile.getName().toLowerCase().endsWith(".mfa")) {
                            selectedFile = new File(selectedFile.getAbsolutePath() + ".mfa");
                        }
                    }
                } else { // YES option
                    break;
                }
            }

            alignFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));

            try {
                selectedFile = chooser.getSelectedFile();

                if (!selectedFile.getName().toLowerCase().endsWith(".mfa")) {
                    selectedFile = new File(selectedFile.getAbsolutePath() + ".mfa");
                }

                if (selectedFile.exists()) {
                    selectedFile.delete();
                }

                selectedFile.createNewFile();
                // Here comes the actual FASTA file export! 
                AlignmentExporter.saveFasta(viewProps, selectedFile, saveSelection);
                
                lastSelectedFolder = selectedFile.getPath();

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(alignFrame,
                        "An error occured when exporting the PNG file.",
                        "Error Exporting PNG File",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }

            alignFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
	}	
	
	public static void saveFasta(AlignViewProps viewProps, File selectedFile, boolean saveSelection) throws IOException{
        Vector<Integer> currentSelection =  SelectionManager.getFullSelection();
        if(currentSelection.size()== 0 || !saveSelection){
        	currentSelection.clear();
			int sequenceLength =  viewProps.getAlObj().getSequenceAt(0).getLength();
			for(int i = 0; i< sequenceLength;i++){
				currentSelection.add(i+1);
			}
        }
        Writer out = new OutputStreamWriter(new FileOutputStream(selectedFile));
        if(SequenceManager.getSelectedSequences().size()==0 || !saveSelection){
            for(int i=0; i<viewProps.getAlObj().getHeight();i++){
            	out.write(">"+viewProps.getAlObj().getSequenceAt(i).getName()+"\n");
            	SequenceImpl currentSeq = viewProps.getAlObj().getSequenceAt(i); 
            	for(int j=0; j<currentSelection.size();j++){
            		out.write(currentSeq.getSequence()[currentSelection.get(j)-1]);
                	if((j+1)%Constants.AA_PER_LINE==0){
                    	out.write("\n");
                	}
            	}
            	out.write("\n");                	
            }
        }
        else{
            for(int i=0; i<SequenceManager.getSelectedSequences().size();i++){
            	out.write(">"+SequenceManager.getSelectedSequences().get(i)+"_Fragment\n");
            	SequenceImpl currentSeq = SequenceManager.getSequence(SequenceManager.getSelectedSequences().get(i), viewProps.getAlObj()); 
            	for(int j=0; j<currentSelection.size();j++){
                	out.write(currentSeq.getSequence()[currentSelection.get(j)-1]);
                	if((j+1)%Constants.AA_PER_LINE==0){
                    	out.write("\n");
                	}
            	}
            	out.write("\n");                	
            }
        }
        out.close();
	}
	
	
	/**
	 * This method manages the PNG File export.
	 * @param evt
	 */
	public static void exportPNGFile(AlignFrame alignFrame, ActionEvent evt) {
	        JFileChooser chooser = new JFileChooser(lastSelectedFolder);
	        chooser.setFileFilter(new PngFileFilter());
	        chooser.setMultiSelectionEnabled(false);
	        chooser.setDialogTitle("Export PNG File Details");
	        chooser.requestFocusInWindow();

	        File selectedFile;
	        chooser.requestFocusInWindow();
	        int returnVal = chooser.showSaveDialog(alignFrame);

	        if (returnVal == JFileChooser.APPROVE_OPTION) {

	            selectedFile = chooser.getSelectedFile();

	            if (!selectedFile.getName().toLowerCase().endsWith(".png")) {
	                selectedFile = new File(selectedFile.getAbsolutePath() + ".png");
	            }

	            while (selectedFile.exists()) {
	                int option = JOptionPane.showConfirmDialog(alignFrame,
	                        "The  file " + chooser.getSelectedFile().getName() +
	                        " already exists. Replace file?",
	                        "Replace File?", JOptionPane.YES_NO_CANCEL_OPTION);

	                if (option == JOptionPane.NO_OPTION) {
	                    chooser = new JFileChooser(lastSelectedFolder);
	                    chooser.setFileFilter(new PngFileFilter());
	                    chooser.setMultiSelectionEnabled(false);
	                    chooser.setDialogTitle("Export PNG File Details");

	                    returnVal = chooser.showSaveDialog(alignFrame);

	                    if (returnVal == JFileChooser.CANCEL_OPTION) {
	                        return;
	                    } else {
	                        selectedFile = chooser.getSelectedFile();

	                        if (!selectedFile.getName().toLowerCase().endsWith(".png")) {
	                            selectedFile = new File(selectedFile.getAbsolutePath() + ".png");
	                        }
	                    }
	                } else { // YES option
	                    break;
	                }
	            }

	            alignFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));

	            try {
	                selectedFile = chooser.getSelectedFile();

	                if (!selectedFile.getName().toLowerCase().endsWith(".png")) {
	                    selectedFile = new File(selectedFile.getAbsolutePath() + ".png");
	                }

	                if (selectedFile.exists()) {
	                    selectedFile.delete();
	                }

	                selectedFile.createNewFile();
	                // Here comes the actual PNG file export! 
	                alignFrame.getAlignPanel().exportPNGFile(selectedFile);
	                lastSelectedFolder = selectedFile.getPath();

	            } catch (IOException ex) {
	                JOptionPane.showMessageDialog(alignFrame,
	                        "An error occured when exporting the PNG file.",
	                        "Error Exporting PNG File",
	                        JOptionPane.ERROR_MESSAGE);
	                ex.printStackTrace();
	            }

	            alignFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	        }
	    }
	
	/**
	 * Create a copy of the original input file on a new location 
	 */
	public static void exportMethodFile(AlignFrame alignFrame, ActionEvent evt, String method) {
        JFileChooser chooser = new JFileChooser(lastSelectedFolder);
        if(method.equals("S3Det")){
        	chooser.setFileFilter(new InnerS3DetFileFilter());
        }
        else if(method.equals("xdet")){
        	chooser.setFileFilter(new InnerXDetFileFilter());
        }
        chooser.setMultiSelectionEnabled(false);
        chooser.setDialogTitle("Export "+method+" File Details");

        File selectedFile;
        chooser.requestFocusInWindow();
        int returnVal = chooser.showSaveDialog(alignFrame);

        if (returnVal == JFileChooser.APPROVE_OPTION) {

            selectedFile = chooser.getSelectedFile();

            if (!selectedFile.getName().toLowerCase().endsWith(method)) {
                selectedFile = new File(selectedFile.getAbsolutePath() + "."+method);
            }

            while (selectedFile.exists()) {
                int option = JOptionPane.showConfirmDialog(alignFrame,
                        "The  file " + chooser.getSelectedFile().getName() +
                        " already exists. Replace file?",
                        "Replace File?", JOptionPane.YES_NO_CANCEL_OPTION);

                if (option == JOptionPane.NO_OPTION) {
                    chooser = new JFileChooser(lastSelectedFolder);
                    if(method.equals("S3Det")){
                    	chooser.setFileFilter(new InnerS3DetFileFilter());
                    }
                    else if(method.equals("xdet")){
                    	chooser.setFileFilter(new InnerXDetFileFilter());
                    }
                    chooser.setMultiSelectionEnabled(false);
                    chooser.setDialogTitle("Export "+method+" File Details");

                    returnVal = chooser.showSaveDialog(alignFrame);

                    if (returnVal == JFileChooser.CANCEL_OPTION) {
                        return;
                    } else {
                        selectedFile = chooser.getSelectedFile();

                        if (!selectedFile.getName().toLowerCase().endsWith("."+method)) {
                            selectedFile = new File(selectedFile.getAbsolutePath() + "."+method);
                        }
                    }
                } else { // YES option
                    break;
                }
            }

            alignFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));

            try {
                selectedFile = chooser.getSelectedFile();

                if (!selectedFile.getName().toLowerCase().endsWith("."+method)) {
                    selectedFile = new File(selectedFile.getAbsolutePath() + "."+method);
                }

                if (selectedFile.exists()) {
                    selectedFile.delete();
                }
        		try{
	                selectedFile.createNewFile();
	                // Here comes the actual method file export! 
	                if(method.equals("S3Det")){
	                	AlignmentExporter.saveFile(alignFrame.getMethods().getS3DetObject().getFilename(),selectedFile);
	                }
	                else if(method.equals("xdet")){
	                	AlignmentExporter.saveFile(alignFrame.getMethods().getxDetObject().getFilename(),selectedFile);	                
	                }
        		}
        		catch(FileNotFoundException ex){
        			JOptionPane.showMessageDialog(alignFrame,ex.getMessage() + " in the specified directory.","Error Exporting "+method+" File",JOptionPane.ERROR_MESSAGE);
        			return;
        		}
                lastSelectedFolder = selectedFile.getPath();

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(alignFrame,
                        "An error occured when exporting "+method+" file.",
                        "Error Exporting "+method+" File",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }

            alignFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }
	
	
	/**
	 * Sets the name-to-clusterIndex HashMap.
	 * @param name2ClusterIndex HashMap
	 */
	public static void saveFile(String inputFile, File outFile) throws FileNotFoundException,IOException {
			File s3detInput = new File(inputFile);
			InputStream in = new FileInputStream(s3detInput);
			OutputStream out = new FileOutputStream(outFile);
	
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0){
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
	}
	
	private static class InnerS3DetFileFilter extends FileFilter {
        @Override
		public boolean accept(File f) {
            return f.isDirectory() || f.getName().toLowerCase().endsWith(".s3Det") || f.getName().endsWith(".S3det");
        }

        @Override
		public String getDescription() {
            return "*.S3det";
        }
    }	
	 private static class InnerXDetFileFilter extends FileFilter {
	        @Override
			public boolean accept(File f) {
	            return f.isDirectory() || f.getName().toLowerCase().endsWith(".xdet") || f.getName().endsWith(".Xdet");
	    }
	
	    @Override
		public String getDescription() {
	        return "*.xdet";
	    }
	}      
}
