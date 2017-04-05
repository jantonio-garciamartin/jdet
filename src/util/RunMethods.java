package util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import model.S3DetObject;
import model.XDetObject;

import org.jdesktop.swingx.JXButton;

import parser.S3DetParser;
import parser.XDetParser;
import view.AlignFrame;
import view.AlignViewProps;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class RunMethods  extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AlignFrame parent;
	private AlignViewProps viewProps;
	private String method;
	private RunMethods runDialog;
	private JTextField paramsTtf;
	private JLabel warningsLabel;
	
	public RunMethods(){}
	
	/**
	 * LoadFrame with a given parent frame.
	 * @param parent
	 */
	public RunMethods(AlignFrame parent, AlignViewProps viewProps, String method) {
		
		// Initialize frame.
		super(parent,"Run "+method,true);
		this.parent = parent;		
		this.viewProps = viewProps;		
		this.method = method;
		this.runDialog = this;
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				cancelTriggered();
			}
		});
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		// GUI construction
		constructScreen(method);
	}
	
	
	private void constructScreen(String methodName) {
		JPanel panelNorth;
		JPanel panelCenter;
		JPanel panelSouth;

		JXButton runBtn;
		JXButton closeBtn;	
		this.setSize(Constants.LOADFRAME_WIDTH, Constants.LOADFRAME_HEIGHT);
		
		Container tContentPane = this.getContentPane();
		tContentPane.setLayout(new BorderLayout());

		// Build CellConstraints object
		CellConstraints cc = new CellConstraints();

		/*
		 * Two horizontal main panels
		 */
		panelNorth = new JPanel(new FormLayout("10dlu, p, 10dlu", "p, 5dlu"));
		panelCenter = new JPanel(new FormLayout("10dlu, p, 10dlu", "p, 5dlu"));		
		panelSouth = new JPanel(new FormLayout("10dlu, p, 3dlu, p, 10dlu", "p, 5dlu"));

		// Parameters Panel
		JPanel paramsPanel = new JPanel(new FormLayout("3dlu, p, 3dlu", "p"));		
		warningsLabel = new JLabel(); 
		// Titled borders
		paramsPanel.setBorder(BorderFactory.createTitledBorder("Command line options"));

		
		// Textfields
		paramsTtf = new JTextField(26);
		if(method.equals("Xdet")){
			paramsTtf.setText(Constants.METHOD_METRICS_FILE);
			if(this.parent.getAlObj().getHeight()>80){
				warningsLabel.setText("<html><p><b>Warning: </b></p>" +
						              "<p>Your alignment contains a large number of sequences. </p><p></p> " +
						              "<p>Xdet running time grows quadratically with the number</p>" +
						              "<p>of sequences  and hence it could take a long time to  </p>" +
						              "<p>run for this alignment. </p><p></p>" +
						              "<p>It is recommended to run Xdet (outside Jdet) in a HPC </p>" +
						              "<p>machine or similar and then load the results file. </p>" +
						              "<p>Another possibility is, in case the alignment contains </p>" +
						              "<p>very similar (redundant) sequences, is to filter it in </p>" +
						              "<p>order to remove this redundancy (File->Filter alignment)</p><html>");
			}
		}
		else if(method.equals("S3Det")){
			paramsTtf.setText("-v");
		}
		paramsPanel.add(paramsTtf, cc.xy(2, 1));
		
		
		// Adding options panel
	    JPanel optionsPanel = new JPanel(new FormLayout("p", "3dlu, p, 3dlu"));
	    optionsPanel.add(paramsPanel, cc.xy(1, 2));
	    
	    // Action panel
	    JPanel actionPanel = new JPanel(new FormLayout("100dlu, p, 5dlu, right:p", "5dlu, p, 5dlu"));
	    
	    runBtn = new JXButton("Run");
	    //runBtn.setEnabled(false);
	    runBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
						runDialog.runMethod(parent, viewProps, e, method, null, paramsTtf.getText() );
						runDialog.dispose();
				} catch (Exception ex) {
				}
			}
		});
	    closeBtn = new JXButton("Close");
	    closeBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				runDialog.dispose();
			}
		});
	    
	    actionPanel.add(closeBtn, cc.xy(2, 2));
	    actionPanel.add(runBtn, cc.xy(4, 2));
	    
	    panelNorth.add(warningsLabel, cc.xy(2, 1));
	    panelCenter.add(optionsPanel, cc.xy(2, 1));
	    panelSouth.add(actionPanel, cc.xy(4, 1));
		
	    tContentPane.add(panelNorth, BorderLayout.NORTH);
		tContentPane.add(panelCenter, BorderLayout.CENTER);
		tContentPane.add(panelSouth, BorderLayout.SOUTH);
		runDialog.setResizable(false);
		runDialog.pack();
		
		// Center in the parent window
		ScreenConfig.centerInComponent(this,parent);
		
		runDialog.setVisible(true);
	}
	
	/**
     * This method is called when the user presses cancel.
     */
    private void cancelTriggered() {       
        this.dispose();        
    }
    
    public void writeProgress(String text){
    	System.out.println(text);
    }
    
	public void runMethod(AlignFrame alignFrame, AlignViewProps viewProps, ActionEvent evt, String method, String outFileName, String params){
		params= params.trim();
		
		// Check if tmd dir exist, if not try to create it 
		File file=new File(System.getProperty("user.dir")+Constants.dS+Constants.TMP_DIR);
		if(!file.exists()){
			boolean success = (new File(Constants.TMP_DIR)).mkdir();
			if (!success) {
				JOptionPane.showMessageDialog(null, "Temp directory cannot be created.", "Tmp directory create error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
		// Create temp copy of current alignment
		String tmpFileName = Constants.TMP_DIR+"tmp.mfa";
		File tmpFile = new File(tmpFileName);
		
		
		try{
            AlignmentExporter.saveFasta(viewProps,tmpFile, false);
		}
		catch(IOException e){
			JOptionPane.showMessageDialog(alignFrame, "Temporary file cannot be created.", "File create error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		
 		if(method.equals("Xdet")){
 			// Check output file name
 			if(outFileName==null){ 
 				outFileName= Constants.TMP_DIR+"tmp.Xdet";
 				try{
 					File tmpDelete = new File(outFileName);
 					tmpDelete.delete();
 				}
 				catch (Exception e){} 				
 			}

 			String command = Constants.METHODS_DIR;
 			
			if(System.getProperty("os.name").toLowerCase().indexOf("nix") >=0 || System.getProperty("os.name").toLowerCase().indexOf("linux") >=0){
				if(System.getProperty("os.arch").indexOf("amd64")>=0){
					command += "xdet_linux64";
				}
				else{
					command += "xdet_linux32";
				}
	 		}
	 		else if(System.getProperty("os.name").toLowerCase().indexOf("win") >=0){
	 			command += "xdet.exe";
	 		}
	 		else if(System.getProperty("os.name").toLowerCase().indexOf("mac") >=0){
	 			command += "xdet_osx";
	 		}
			
			try {
				
				String completeCommand = command+" "+tmpFileName+" "+params;
				Process proc = Runtime.getRuntime().exec(completeCommand);
				StreamGobbler s1 = new StreamGobbler ("stdin", proc.getInputStream (), outFileName);
				s1.start ();
				
				// Show progress
				try{
					JDialog progress = new JDialog(this, Dialog.ModalityType.MODELESS);
					progress.setTitle("Running Xdet...");
					JLabel pb = new JLabel("");
					pb.setBackground(Color.WHITE);
					pb.setForeground(Color.BLACK);
					pb.setSize(250, 40);
					JPanel panelNorth = new JPanel();
					panelNorth.add(pb);
					progress.setLayout(null);
					progress.setContentPane(panelNorth);
					progress.getRootPane().add(panelNorth);
					progress.validate();
					progress.pack();
					ScreenConfig.centerInComponent(progress,this);
					progress.setVisible(true);
					progress.toFront();
					

					InputStreamReader isr = new InputStreamReader (proc.getErrorStream ());
					BufferedReader br = new BufferedReader (isr);
					
					while (true) {
						String line = br.readLine ();
						if (line == null) break;
						//writeProgress(line);
						pb.setText(line.trim());
						progress.validate();						
						progress.pack();
						progress.repaint();
						panelNorth.paintImmediately(panelNorth.getBounds());
					}
					proc.getErrorStream ().close ();
				} catch (Exception ex) {
					System.out.println ("Problem reading stream " + ex);
					ex.printStackTrace ();
				}				
				
				
				
				// Wait for the thread termination signal
				proc.waitFor();
				
				
				//Load Xdet method 
				
				AlignFrame newAlignFrame = null;
			
				// XDetParser
				XDetParser xdetparser = new XDetParser();
				XDetObject xdetObject = null;
				try {
					xdetObject = xdetparser.read(outFileName,alignFrame.getAlObj());
					alignFrame.getMethods().setxDetUsed(true);
					alignFrame.getMethods().setxDetObject(xdetObject);
					alignFrame.getMethods().setxDetCutoff(new Double(Constants.XDETCUTOFF_DEFAULT));
					alignFrame.getMethods().setEntropyUsed(true);
					alignFrame.getMethods().setEntropyCutoff(new Double(Constants.ENTROPYCUTOFF_DEFAULT));
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null,
							"The XDet file couldn't be loaded! \n"
									+ "Please specify a proper format/location of the file.\nDetails:\n    "+(ex.getMessage().indexOf(":")!= -1 ? ex.getMessage().substring(ex.getMessage().indexOf(":")+1): ex.getMessage()),
							"File load error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				newAlignFrame = new AlignFrame(alignFrame.getAlObj(), alignFrame.getMethods(), Constants.DEFAULT_WIDTH, Constants.DEFAULT_HEIGHT, false);
				newAlignFrame.pack();
				
				// Close parent if given
				if (alignFrame != null) {
					((AlignFrame) alignFrame).closeOtherWindows();							
					alignFrame.dispose();
				}
				newAlignFrame.setVisible(true);

			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,
						"XDet method cannot be executed! \n"
								+ "There is no version of XDet available for current SO.\nDetails:\n    "+(e.getMessage().indexOf(":")!= -1 ? e.getMessage().substring(e.getMessage().indexOf(":")+1): e.getMessage()),
						"File load error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				return;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 		}
 		else if(method.equals("S3Det")){
 			// Check output file name
 			if(outFileName==null){ 
 				outFileName= Constants.TMP_DIR+"tmp.S3det";
 				try{
 					File tmpDelete = new File(outFileName);
 					tmpDelete.delete();
 				}
 				catch (Exception e){}
 			}
 			
 			String command = Constants.METHODS_DIR;
 			
			if(System.getProperty("os.name").toLowerCase().indexOf("nix") >=0 || System.getProperty("os.name").toLowerCase().indexOf("linux") >=0){
				if(System.getProperty("os.arch").indexOf("amd64")>=0){
					command = "./s3det_linux64";
				}
				else{
					command = "./s3det_linux32";
				}
	 		}
	 		else if(System.getProperty("os.name").toLowerCase().indexOf("win") >=0){
	 			command = "s3det.exe";
	 		}
	 		else if(System.getProperty("os.name").toLowerCase().indexOf("mac") >=0){
	 			command = "./s3det_osx";
	 		}
			
			try {
				String completeCommand = command+" -i .."+Constants.dS+tmpFileName+" -o .."+Constants.dS+outFileName+" "+params;
				ProcessBuilder procB = new ProcessBuilder(System.getProperty("user.dir")+Constants.dS+Constants.METHODS_DIR+command,"-i","../"+tmpFileName,"-o","../"+outFileName,params);
				procB.directory(new File(System.getProperty("user.dir")+Constants.dS+Constants.METHODS_DIR));

				Process proc  = procB.start();
				StreamGobbler s2 = new StreamGobbler ("stderr", proc.getInputStream(), null);
				s2.start ();
				

				// Show progress	
				JDialog progress = new JDialog(this, Dialog.ModalityType.MODELESS);
				progress.setTitle("Running S3det...");
				JProgressBar pb = new JProgressBar();
				pb.setSize(200, 40);
				pb.setIndeterminate(true);
				JPanel panelNorth = new JPanel();
				panelNorth.add(pb);
				progress.setLayout(null);
				progress.setContentPane(panelNorth);
				progress.getRootPane().add(panelNorth);
				progress.validate();
				progress.pack();
				ScreenConfig.centerInComponent(progress,this);
				progress.setVisible(true);
				panelNorth.paintImmediately(panelNorth.getBounds());
				progress.toFront();
				
				
				// Wait process to finalize
				proc.waitFor();
				
				/* CAMBIO TEMPORAL PARA WINDOWS - HASTA ARREGLAR LA COMPILACION DE S3DET */  
				if(System.getProperty("os.name").toLowerCase().indexOf("win") >=0){
					try{
						String s3detConfFile = Constants.METHODS_DIR+"conf.h";
						BufferedReader reader = new BufferedReader(new FileReader(s3detConfFile));
						String commandR = "";
						String exec_directory = "";
						String tmp_directory = "";
						String nextLine;
						Pattern patCmd=Pattern.compile("^(\\s*)string(\\s+)order(\\s*)=(\\s*)\"(.+)\"(\\s*);(\\s*)$");
						Pattern patTmp=Pattern.compile("^(\\s*)string(\\s+)tmp_directory(\\s*)=(\\s*)\"(.+)\"(\\s*);(\\s*)$");
						Pattern patExec=Pattern.compile("^(\\s*)string(\\s+)exec_directory(\\s*)=(\\s*)\"(.+)\"(\\s*);(\\s*)$");

						Matcher mat = null;
						// Iterate over all the lines of the file.
						while ((nextLine = reader.readLine()) != null) {
							mat = patCmd.matcher(nextLine);
							if(mat.find()){
								commandR = nextLine.substring(nextLine.indexOf('"')+1, nextLine.lastIndexOf('"'));
								commandR= commandR.trim();
							}
							mat = patTmp.matcher(nextLine);
							if(mat.find()){
								tmp_directory = nextLine.substring(nextLine.indexOf('"')+1, nextLine.lastIndexOf('"'));
								tmp_directory= tmp_directory.trim();
							}
							mat = patExec.matcher(nextLine);
							if(mat.find()){
								exec_directory = nextLine.substring(nextLine.indexOf('"')+1, nextLine.lastIndexOf('"'));
								exec_directory= exec_directory.trim();
							}
						}						
						String completeCommandR =commandR+" "+exec_directory+"S3det_Wilcoxon_test.R "+tmp_directory;
						procB = new ProcessBuilder(commandR,System.getProperty("user.dir")+Constants.dS+Constants.METHODS_DIR+"S3det_Wilcoxon_test.R",tmp_directory);
						procB.directory(new File(System.getProperty("user.dir")+Constants.dS+Constants.METHODS_DIR));

						proc  = procB.start();
						System.out.println(completeCommandR);
						proc.waitFor();
						if(proc.exitValue()!= 0){
							JOptionPane.showMessageDialog(null,"Error running S3Det! \n"+ "Rscript was not found on specified path.\nPlease check if R is installed and the correct path of Rscript is included in programs/conf.h file.");
						    reader.close();
							return;
						}
						
						
						procB = new ProcessBuilder(System.getProperty("user.dir")+Constants.dS+Constants.METHODS_DIR+command,"-i","../"+tmpFileName,"-o","../"+outFileName,params);
						procB.directory(new File(System.getProperty("user.dir")+Constants.dS+Constants.METHODS_DIR));
						proc  = procB.start();
						System.out.println(completeCommand);
						s2 = new StreamGobbler ("stderr", proc.getInputStream(), null);
						s2.start ();

						proc.waitFor();
					    reader.close();

					} catch (FileNotFoundException e) {
						System.out.println("File not found error: "+e.getMessage());
						JOptionPane.showMessageDialog(null,"Error running S3Det! \n"+ "Rscript was not found on specified path.\nPlease check if R is installed and the correct path of Rscript is included in programs/conf.h file.");
						return;
					} catch (IOException e) {
						System.out.println("IO error: "+e.getMessage());
						JOptionPane.showMessageDialog(null,"Error running S3Det! \n"+ "Rscript was not found on specified path.\nPlease check if R is installed and the correct path of Rscript is included in programs/conf.h file.");
						return;
					} catch (NumberFormatException e) {
						System.out.println("Number format error:"+e.getMessage());
						JOptionPane.showMessageDialog(null,"Error running S3Det! \n"+ "Rscript was not found on specified path.\nPlease check if R is installed and the correct path of Rscript is included in programs/conf.h file.");
						return;
					} catch (Exception e) {
						System.out.println("Other error:"+e.getMessage());
						JOptionPane.showMessageDialog(null,"Error running S3Det! \n"+ "Rscript was not found on specified path.\nPlease check if R is installed and the correct path of Rscript is included in programs/conf.h file.");
						return;
					}

			 	}
				else{
					if(proc.exitValue()!= 0){
							JOptionPane.showMessageDialog(null,"Error running S3Det! \n"+ "R was not found on specified path, cluster data cannot be obtained.\nPlease check configuration on programs/conf.h file.");
							return;
					}
				}
				/*******************************************************************************/				
				
				//Load S3Det method
				
				// AlignFrame
				AlignFrame newAlignFrame = null;
			
				// S3DetParser
				S3DetParser s3detparser = new S3DetParser();
				S3DetObject s3DetObject = null;
				try {
						s3DetObject = s3detparser.read(outFileName, viewProps.getAlObj());
						/*if(s3DetObject.getSeqCoords().length != parent.getAlObj().getHeight()){
							throw new Exception("Incorrect number of sequences. Alignment->"+parent.getAlObj().getHeight()+"  S3File->"+s3DetObject.getSeqCoords().length);
						}*/
						alignFrame.getMethods().setS3DetUsed(true);
						alignFrame.getMethods().setS3DetObject(s3DetObject);
						alignFrame.getMethods().setS3DetCutoff(Constants.S3DETCUTOFF_DEFAULT);
				} catch (Exception ex) {			
					JOptionPane.showMessageDialog(null,"The S3Det file couldn't be loaded! \n"+ "Please specify a proper format/location of the file.\nDetails:\n    "+(ex.getMessage().indexOf(":")!= -1 ? ex.getMessage().substring(ex.getMessage().indexOf(":")+1): ex.getMessage()),"File load error", JOptionPane.ERROR_MESSAGE);
					return;
				}	
				newAlignFrame = new AlignFrame(viewProps.getAlObj(), alignFrame.getMethods(), Constants.DEFAULT_WIDTH, Constants.DEFAULT_HEIGHT, false);
				newAlignFrame.pack();
				// Close parent if given
				if (alignFrame != null) {
					((AlignFrame) alignFrame).closeOtherWindows();							
					alignFrame.dispose();
				}
				newAlignFrame.setVisible(true);

			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,
						"S3Det method cannot be executed! \n"
								+ "There is no version of S3Det available for current SO.\nDetails:\n    "+(e.getMessage().indexOf(":")!= -1 ? e.getMessage().substring(e.getMessage().indexOf(":")+1): e.getMessage()),
						"File load error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 		}
	}
	
	
	public class StreamGobbler implements Runnable {
		String name;
		InputStream is;
		String outFileName;
		Thread thread;
		
		
		public StreamGobbler (String name, InputStream is,String outFileName) {
			this.name = name;
			this.is = is;
			this.outFileName = outFileName;
		}


		public void start () {
			thread = new Thread (this);
			thread.start ();
		}

		public void run () {
			try {
				InputStreamReader isr = new InputStreamReader (is);
				BufferedReader br = new BufferedReader (isr);
				FileWriter outWriter = null;
				if(outFileName != null){
					outWriter = new FileWriter(outFileName);
				}
				
				while (true) {
					String line = br.readLine ();
					if (line == null) break;
					if(outFileName != null){
						outWriter.write(line+"\n");
					}
					else{	
						System.out.println(line);				
					}
				}
				is.close ();
				if(outWriter!= null){
					outWriter.close();	
				}
			} catch (Exception ex) {
				System.out.println ("Problem reading stream " + name + "... :" + ex);
				ex.printStackTrace ();
			}
		}
	}	
}
