package view;

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import model.SequenceImpl;

import org.jdesktop.swingx.JXButton;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.BusinessBlackSteelSkin;

import util.Alignment;
import util.Constants;
import util.NCBIBlastPDB;
import util.PDBAtomReader;
import util.PDBFile;
import util.PdbFileOpener;
import util.ScreenConfig;
import util.SequenceManager;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
/**
 * This class is the loading frame for the JMolViewer.
 * The User can choose the PDB file information and load the viewer.
 * @author Thilo Muth
 *
 */
public class LoadStructureFrame extends JDialog{
	
	private static final long serialVersionUID = 1L;
	private JTextField pdbFileTtf;
	private LoadStructureFrame frame;
	private String filename;
	private JButton browspdbFileBtn;
	private JButton loadBtn;
	private JButton cancelBtn;
	private JLabel chooseFileLbl;
	private JTextField pdbCodeTtf;
	private JButton loadpdbonlineBtn;
	private JLabel pdbCodeLbl;
	private PdbFileOpener pdbfileOpener;
	private JTextArea downloadInfoArea;
	private String pdbCode;
	private JLabel statusLbl;
	private JPanel centerPnl;
	private JPanel statusPnl;	
	private JLabel chainLbl;
	private JTextField chainTtf;
	private AlignViewProps viewprops;
	private JComboBox comboBox;
	//private JLabel idLbl;
	private JButton blastPdbOnlineBtn;	
	private JPanel pdbFilePnl;
	private SequencePainter seqPainter;
	private NamePainter namePainter;
	private AlignPanel alignPanel;
	private AlignFrame parent;
	
	
	/**
	 * Constructor of the LoadJMolFrame
	 */
	public LoadStructureFrame(AlignViewProps viewprops, SequencePainter seqPainter, NamePainter namePainter, AlignPanel alignPanel,AlignFrame parent){		
		//super();
		super(parent,"Load XDet",true);
		this.viewprops = viewprops;
		this.seqPainter = seqPainter;
		this.namePainter = namePainter;
		this.alignPanel = alignPanel;
		this.parent = parent;
		this.setTitle("JMol File Selection");
		this.setModal(true);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
            	cancelTriggered();
            }
        });
		constructScreen();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	/**
	 * Does the main screen construction
	 */
	private void constructScreen(){		
		JPanel northPnl = null;
		JPanel southPnl = null;
		
		// Initialize frame
		frame = this;
		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		frame.setSize(Constants.LOADFRAME_WIDTH, Constants.LOADFRAME_HEIGHT);
		
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		// Build CellConstraints object
		CellConstraints cc = new CellConstraints();
		

		/*
		 * two horizontal main panels
		 */
		northPnl = new JPanel(new FormLayout("10dlu, p:grow, 10dlu", "5dlu, p, 5dlu"));
		centerPnl = new JPanel(new FormLayout("10dlu, p:grow, 10dlu", "5dlu, p, 5dlu"));
		statusPnl = new JPanel(new FormLayout("10dlu, p, 3dlu, p:grow, 10dlu", "5dlu, p, 5dlu"));
		southPnl = new JPanel(new FormLayout("10dlu, right:p:grow, 10dlu", "p, 5dlu"));
		
		
		// Adding settings panel
	    JPanel settingsPanel = new JPanel(new FormLayout("5dlu, p, 5dlu, p, 5dlu", "3dlu, p, 3dlu"));
	    
	    // Titled border
	    settingsPanel.setBorder(BorderFactory.createTitledBorder("Alignment sequence"));
		
		// Load from Server button
		blastPdbOnlineBtn = new JButton("Look for suitable structure for");
		blastPdbOnlineBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String sequenceToBlast = SequenceManager.getSequence(comboBox.getSelectedItem().toString(),parent.getAlObj()).getSequenceAsString();
				sequenceToBlast = sequenceToBlast.replace("-", "");
				downloadInfoArea.setText("Blasting....");
				statusPnl.paintImmediately(statusPnl.getVisibleRect());
				System.out.println(sequenceToBlast);
				String blastResult = NCBIBlastPDB.blastThePDB(sequenceToBlast, 25.0);
				if(blastResult != null){
					if (NCBIBlastPDB.evaluateBestHit(blastResult)){
						String[] splitResults = blastResult.split("\\|");
						pdbCodeTtf.setText(splitResults[0]);
						chainTtf.setText(splitResults[1]);
						downloadInfoArea.setText("Structure found: "+splitResults[0]+" - Identity: "+splitResults[3]+"% - e-Value: "+splitResults[2]);
					}
					else{
						downloadInfoArea.setText("No matching structure found");
					}
				}
				else{
					downloadInfoArea.setText("Connection failed.");
				}
			}
		});		
		
		settingsPanel.add(blastPdbOnlineBtn, cc.xy(2, 2));
	    
	/*	// Protein ID Label
		idLbl = new JLabel("ID:");
		settingsPanel.add(idLbl, cc.xy(4, 2));*/
		
		// ProteinID Comboboxes
		int count = viewprops.getAlObj().getProteinNumber();
		String[] names = new String[count];
		for (int i = 0; i < count; i++){
			names[i] = viewprops.getAlObj().getSequenceAt(i).getName();
		}
		
		// Option comboboxes for axes
		comboBox = new JComboBox(names);
		if(SequenceManager.getLastSelected()!= null){
			comboBox.setSelectedItem(SequenceManager.getLastSelected());
		}
		else{		
			comboBox.setSelectedIndex(0);
		}
		settingsPanel.add(comboBox, cc.xy(4, 2));
		
		
		// PDB file Panel
		pdbFilePnl = new JPanel(new FormLayout("3dlu, p, 5dlu, p, 5dlu, p, 5dlu", "p, 5dlu, p, 5dlu, p, 3dlu, p"));
		
		// Titled border
		pdbFilePnl.setBorder(BorderFactory.createTitledBorder("PDB file"));

		// File Chooser Label		
		chooseFileLbl = new JLabel("Enter PDB file:");
		pdbFilePnl.add(chooseFileLbl, cc.xy(2, 1));
		
		// Textfield
		pdbFileTtf = new JTextField(10);
		pdbFileTtf.setEditable(false);
		pdbFileTtf.setEnabled(false);
		pdbFilePnl.add(pdbFileTtf, cc.xy(4, 1));
		
		// Browse local PDB file
		browspdbFileBtn = new JButton(openFileAction());
		pdbFilePnl.add(browspdbFileBtn, cc.xy(6, 1));
		
		// PDB Code Label
		pdbCodeLbl = new JLabel("or Enter 4-letter PDB-Code:");
		pdbFilePnl.add(pdbCodeLbl, cc.xy(2, 3));
		
		// PDB Code Textfield
		pdbCodeTtf = new JTextField(4);
		pdbCodeTtf.setEditable(true);
		pdbCodeTtf.setEnabled(true);
		pdbFilePnl.add(pdbCodeTtf, cc.xy(4, 3));
		
		// Load from Server button
		loadpdbonlineBtn = new JButton("Load from Server");
		loadpdbonlineBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				pdbCode = pdbCodeTtf.getText().toLowerCase();
				openPDBfromServer();
			}
		});		
		
		pdbFilePnl.add(loadpdbonlineBtn, cc.xy(6, 3));
		

		
	    // PDB Code Label
		chainLbl = new JLabel("Chain:");
		pdbFilePnl.add(chainLbl, cc.xy(2, 5));
		
		// PDB Code Textfield
		chainTtf = new JTextField(4);
		chainTtf.setEditable(true);
		chainTtf.setEnabled(true);
		// Default Chain A
		chainTtf.setText("A");
		chainTtf.setHorizontalAlignment(JTextField.CENTER);
		pdbFilePnl.add(chainTtf, cc.xy(4, 5));
		
		
		// Status Label
		statusLbl = new JLabel("Status:");
		statusPnl.add(statusLbl, cc.xy(2, 2));
	
		// Download Infoarea
		downloadInfoArea = new JTextArea ();
		downloadInfoArea.setOpaque(false);
		statusPnl.add(downloadInfoArea, cc.xy(4, 2));		
		
		// ProgressBar
		//progressBar = new JProgressBar();
		//pdbFilePnl.add(progressBar, cc.xy(6, 5));
		

		// Adding load panel
	    JPanel loadPanel = new JPanel(new FormLayout("p:grow", "3dlu, p ,3dlu"));
	    loadPanel.add(pdbFilePnl, cc.xy(1, 2));
	    
	    // Action panel
	    JPanel actionPanel = new JPanel(new FormLayout("150dlu, p, 5dlu, right:p", "5dlu, p, 5dlu"));
	    
	    loadBtn = new JXButton("Load");
	    loadBtn.setEnabled(false);
	    loadBtn.addActionListener(new ActionListener() {
			
			private Alignment alignment;

			public void actionPerformed(ActionEvent e) {
				if(pdbFileTtf.getText().length() > 0 ){
					try {
						// Load from the text field
						SequenceImpl alignSeq = viewprops.getAlObj().getSequenceAt(comboBox.getSelectedIndex());
						// Read extract amino acid sequence from PDB file
						String pdbSequence = new PDBAtomReader(pdbFileTtf.getText(), chainTtf.getText()).getPdbSequence();
						// Read aminoacid reference sequence from PDB file 
						// String pdbSequence = new PDBFile(pdbFileTtf.getText(), chainTtf.getText()).getPdbSequence();
						if(pdbSequence.length()==0){
							JOptionPane.showMessageDialog(new JFrame(), "Error! Retrieved sequence is empty, check selected chain.");
						}
						else{
							alignment = new Alignment(alignSeq.getSequenceAsString(), pdbSequence);
							frame.setVisible(false);
							alignment.showAlignment();
							EventQueue.invokeLater(new Runnable() {
					    	      public void run() {
					    	    	  try {
					    	    		  SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin()); 
					    	    	  } catch (Exception e){
					    	    		  e.printStackTrace();
					    	    	  }    	    	
					    	    	  seqPainter.closeRegisteredWindows("Structures");
					    	    	  SequenceManager.setMarkedSequenceName(comboBox.getSelectedItem().toString());
						    	      namePainter.repaint();					    	      
					    	    	  new Structure3DFrame(pdbFileTtf.getText(), seqPainter, chainTtf.getText(), alignment, namePainter, alignPanel);
					    	      }
					    	    });
							
							cancelTriggered();
						}
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null,
								"The PDB file couldn't be loaded! \n"
										+ "Please specify a proper format/location of the file.",
								"File load error", JOptionPane.ERROR_MESSAGE);
					}
										
					
				} else if (pdbfileOpener.getPdbFile() != null){
					// Load download file.
					try {
						SequenceImpl alignSeq = viewprops.getAlObj().getSequenceAt(comboBox.getSelectedIndex());
						String pdbSequence = new PDBFile(pdbfileOpener.getPdbFile(), chainTtf.getText()).getPdbSequence();
						if(pdbSequence.length()==0){
							JOptionPane.showMessageDialog(new JFrame(), "Error! Retrieved sequence is empty, check selected chain.");
						}
						else{
							Alignment alignment = new Alignment(alignSeq.getSequenceAsString(), pdbSequence);
							seqPainter.closeRegisteredWindows("Structures");
							SequenceManager.setMarkedSequenceName(comboBox.getSelectedItem().toString());						
							namePainter.repaint();	
							frame.setVisible(false);
							alignment.showAlignment();
							new Structure3DFrame(pdbfileOpener.getPdbFile(), seqPainter, chainTtf.getText(), alignment, namePainter, alignPanel);
							cancelTriggered();
						}
					} catch (Exception ex) {
					JOptionPane.showMessageDialog(null,
							"The PDB file couldn't be loaded! \n"
									+ "Please specify a proper location of the file.",
							"File load error", JOptionPane.ERROR_MESSAGE);
					}					
				}		
			}
		});
	    
	    cancelBtn = new JXButton("Cancel");
	    cancelBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				 frame.dispose();				
			}
		});
	    
	    actionPanel.add(cancelBtn, cc.xy(2, 2));
	    actionPanel.add(loadBtn, cc.xy(4, 2));
	    
	    centerPnl.add(loadPanel, cc.xy(2, 2));
	    northPnl.add(settingsPanel, cc.xy(2, 2));
	    southPnl.add(actionPanel, cc.xy(2, 1));
	    contentPane.add(northPnl);
	    contentPane.add(centerPnl);
	    contentPane.add(statusPnl);
		contentPane.add(southPnl);
		frame.setResizable(false);
		frame.pack();

		// Center in parent window
		ScreenConfig.centerInComponent(this, parent);
		
		frame.setVisible(true);
	}
	
	/**
     * This method is called when the user presses cancel.
     */
    private void cancelTriggered() {       
        frame.dispose();        
    }
    
	/**
	 * Opens a PDB file from the PDB server.
	 * @param pdbCode String
	 */
	private void openPDBfromServer() {
		downloadInfoArea.setText("Downloading...");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				pdbfileOpener = new PdbFileOpener(pdbCode,downloadInfoArea);
				//String dlInfo = "kB downloaded...";
				//progressBar.setIndeterminate(true);
				try {
					pdbfileOpener.getThread().join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//progressBar.setIndeterminate(false);
				//int prog = pdbfileOpener.getDownloaded() / 1024;
				//downloadInfoArea.setText(prog + dlInfo);
				loadBtn.setEnabled(true);
			}
		});
	}

	/**
	 * Defines the openFileAction-method
	 * @return openAction 
	 */
	private Action openFileAction(){
			filename = System.getProperty("user.dir") + File.separator;	
		    JFileChooser fc = new JFileChooser(new File(filename));
		    JDialog loadFrame = new JDialog();
		    loadFrame.setModal(true);
		    Action openAction = new OpenFileAction(loadFrame, fc);	    
		    return openAction;	    
	    }
	
	/**
	 * Inner action class that creates and show an open-file dialog.
	 * 
	 */
    public class OpenFileAction extends AbstractAction {
    	private static final long serialVersionUID = 1L;
        private JDialog frame;
        private JFileChooser chooser;
        private InnerPdbFileFilter filter = new InnerPdbFileFilter();
        private File file = null;
        private String name = null;
        
        OpenFileAction(JDialog frame, JFileChooser chooser) {
            super("Browse...");
            this.chooser = chooser;
            this.frame = frame;
        }
        
        public void setSourceName(String aName){
        	name = aName;
        }
        
        public String getSourceName(){    	
        	return name;
        }

        public void actionPerformed(ActionEvent evt) {
            chooser.setFileFilter(filter);
            chooser.showOpenDialog(frame);
            // Get the selected file
            file = chooser.getSelectedFile();
            // Only get the path, if a file was selected
            if (file != null) { 
            	setSourceName(file.getAbsolutePath());            	
            	pdbFileTtf.setText(getSourceName());                
            	pdbFileTtf.setEnabled(true);     
            	loadBtn.setEnabled(true);
            }
        }        
    }   
    /**
     * Static inner class that serves as a filter: only filenames ending with .pdb are allowed.
     *
     */
    private static class InnerPdbFileFilter extends javax.swing.filechooser.FileFilter {
        @Override
		public boolean accept(File f) {
            return f.isDirectory() || f.getName().toLowerCase().endsWith(".pdb") || f.getName().toLowerCase().endsWith(".ent");
        }

        @Override
		public String getDescription() {        	
            return "*.pdb, *.ent";
        }
    }
    
    /**
     * Getter-method for the filename.
     * @return filename
     */
	public String getFilename() {
		return filename;
	}
	
	/**
	 * Setter method for the filename.
	 * @param filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
}
