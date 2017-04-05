package util;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.AlignObject;
import model.Methods;

import org.jdesktop.swingx.JXButton;

import parser.MfaParser;
import view.AlignFrame;
import view.AlignViewProps;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class FilterAlignment  extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AlignFrame parent;
	private AlignViewProps viewProps;
	private String method;
	private FilterAlignment filterDialog;
	private JTextField paramMttf;
	private JTextField paramRttf;
	private JTextField paramFttf;
	private JCheckBox paramGttf;
	private JComboBox<String> presetsCombo;
	private HashMap<String, PresetItem> presets;
	
	public FilterAlignment(){}
	
	/**
	 * LoadFrame with a given parent frame.
	 * @param parent
	 */
	public FilterAlignment(AlignFrame parent, AlignViewProps viewProps) {
		
		// Initialize frame.
		super(parent,"Filter Alignment",true);
		this.parent = parent;		
		this.viewProps = viewProps;		
		this.filterDialog = this;
		this.setModal(true);
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
		JPanel panelNorth = null;
		JPanel panelCenter = null;
		JPanel panelSouth = null;
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
		panelNorth = new JPanel(new FormLayout("10dlu, p, 10dlu", "p"));
		panelCenter = new JPanel(new FormLayout("p, 10dlu", "p, 5dlu"));
		panelSouth = new JPanel(new FormLayout("10dlu, p, 3dlu, p, 10dlu", "p, 5dlu"));

		// Parameters Panel
		JPanel paramsPanel = new JPanel(new FormLayout("15dlu, p, 5dlu, p, 15dlu", "p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));		
		
		// Titled borders
		paramsPanel.setBorder(BorderFactory.createTitledBorder("Parameters"));
		// Textfields
		JLabel labelMttf = new JLabel("Remove sequences with %ID with master lower than");
		paramsPanel.add(labelMttf, cc.xy(2, 1));		
		paramMttf = new JTextField(2);
		paramsPanel.add(paramMttf, cc.xy(4, 1));

		JLabel labelRttf = new JLabel("Remove redundancy at %ID");		
		paramsPanel.add(labelRttf, cc.xy(2, 3));		
		paramRttf = new JTextField(2);
		paramsPanel.add(paramRttf, cc.xy(4, 3));
		
		JLabel labelFttf = new JLabel("Remove fragments, %coverage with master lower than");
		paramsPanel.add(labelFttf, cc.xy(2, 5));		
		paramFttf = new JTextField(2);
		paramsPanel.add(paramFttf, cc.xy(4, 5));
		
		JLabel labelGttf = new JLabel("Remove positions with a gap in master sequence");
		paramsPanel.add(labelGttf, cc.xy(2, 7));		
		paramGttf = new JCheckBox();
		paramsPanel.add(paramGttf, cc.xy(4, 7));

		// Presets Panel
		JPanel presetsPanel = new JPanel(new FormLayout("15dlu, p, 15dlu, p", "p, 5dlu"));		

		// Titled borders
		//presetsPanel.setBorder(BorderFactory.createTitledBorder("Presets"));

		// Textfields
		JLabel presetsLabel = new JLabel("Load default values for");
		presetsCombo = new JComboBox<String>();
		presets = new HashMap<String, PresetItem>();
		loadPresets(presets, presetsCombo);
		presetsCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<String> cb = (JComboBox<String>)e.getSource();
				String selectedPreset = cb.getSelectedItem().toString();
				loadPresetValues(selectedPreset);
			}
		});	
		loadPresetValues(presetsCombo.getSelectedItem().toString());
		
		presetsPanel.add(presetsLabel, cc.xy(2, 1));
		presetsPanel.add(presetsCombo, cc.xy(4, 1));		
	/*	JXButton loadPreset = new JXButton("Load Preset");
		loadPreset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadPresetValues(e);
			}
		});		
		presetsPanel.add(loadPreset, cc.xy(2, 5));*/
		panelCenter.add(presetsPanel, cc.xy(1, 1));
		
		// Adding options panel
	    JPanel optionsPanel = new JPanel(new FormLayout("p", "3dlu, p, 3dlu"));
	    optionsPanel.add(paramsPanel, cc.xy(1, 2));
	    
	    // Action panel
	    JPanel actionPanel = new JPanel(new FormLayout("100dlu, p, 5dlu, right:p", "5dlu, p, 5dlu"));
	    
	    runBtn = new JXButton("Filter");
	    //runBtn.setEnabled(false);
	    runBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
						if(paramMttf.getText().isEmpty() || paramRttf.getText().isEmpty() || paramFttf.getText().isEmpty()){
							JOptionPane.showMessageDialog(parent, "Parameters can't be empty.", "Parameters error", JOptionPane.INFORMATION_MESSAGE);
							return;
						}
						else{
							try{
								Double.parseDouble(paramMttf.getText());
								Double.parseDouble(paramRttf.getText());
								Double.parseDouble(paramFttf.getText());
							}
							catch(NumberFormatException nfe){
								JOptionPane.showMessageDialog(parent, "Parameter values must be numeric.", "Parameters error", JOptionPane.INFORMATION_MESSAGE);
								return;								
							}
							filterDialog.filterAlignment(parent, viewProps, e, null, paramMttf.getText(),paramRttf.getText(),paramFttf.getText(),paramGttf.isSelected());
							filterDialog.dispose();
						}
				} catch (Exception ex) {
				}
			}
		});
	    closeBtn = new JXButton("Close");
	    closeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filterDialog.dispose();
			}
		});
	    
	    actionPanel.add(closeBtn, cc.xy(2, 2));
	    actionPanel.add(runBtn, cc.xy(4, 2));
	    
	    panelNorth.add(optionsPanel, cc.xy(2, 1));
	    panelSouth.add(actionPanel, cc.xy(4, 1));
	    
		tContentPane.add(panelNorth, BorderLayout.NORTH);
		tContentPane.add(panelCenter, BorderLayout.CENTER);
		tContentPane.add(panelSouth, BorderLayout.SOUTH);
		filterDialog.setResizable(false);
		filterDialog.pack();
		
		// Center in the parent window
		ScreenConfig.centerInComponent(this,parent);
		
		filterDialog.setVisible(true);
	}
	
	private void loadPresetValues(String selectedPreset){
		PresetItem preset = presets.get(selectedPreset); 
		this.paramMttf.setText(preset.getmValue()); 
		this.paramRttf.setText(preset.getrValue());
		this.paramFttf.setText(preset.getfValue()); 
		this.paramGttf.setSelected(preset.getgValue()); 
	}
	
	public void loadPresets(HashMap<String, PresetItem> presets, JComboBox<String> newPresetsCombo){
		try{
			BufferedReader reader = new BufferedReader(new FileReader(Constants.FILTER_PRESETS_FILE));
			String nextLine = reader.readLine();
			while (nextLine != null){
				nextLine = nextLine.trim();
				if(nextLine.charAt(0) != '#' && nextLine.length()>0){
				    StringTokenizer tokenizer = new StringTokenizer(nextLine,"\t");			    
				    List<String> tokenList = new ArrayList<String>();
				    
				    // Iterate over all the tokens
				    while (tokenizer.hasMoreTokens()) {
				        tokenList.add(tokenizer.nextToken());
				    }
				    if(tokenList.size()<5){
				    	reader.close();
				    	throw new IOException("Bad format in "+Constants.FILTER_PRESETS_FILE);
				    }
				    // Add the tokens to the Presets 
				    presets.put(tokenList.get(0), new PresetItem(tokenList.get(0), tokenList.get(1),tokenList.get(2),tokenList.get(3),(tokenList.get(4).equals("0")) ? false: true));
				    newPresetsCombo.addItem(tokenList.get(0));
				}
			    nextLine = reader.readLine();				
			}
		    reader.close();
		}catch(IOException ex){
			JOptionPane.showMessageDialog(null,	"Error applying filter! \n"	+ "Details:\n       "+(ex.getMessage().indexOf(":")!= -1 ? ex.getMessage().substring(ex.getMessage().indexOf(":")+1): ex.getMessage()),"File load error", JOptionPane.ERROR_MESSAGE);
			
		}
	}
	
	/**
     * This method is called when the user presses cancel.
     */
    private void cancelTriggered() {       
        this.dispose();        
    }
    
	public void filterAlignment(AlignFrame alignFrame, AlignViewProps viewProps, ActionEvent evt, String outFileName, String paramM, String paramR, String paramF, boolean paramG){
		
		//Define directory seaparator
		String dS = (System.getProperty("os.name").toLowerCase().indexOf("win") >=0) ? "\\" :"/";

		// Check if tmd dir exist, if not try to create it 
		File file=new File(System.getProperty("user.dir")+dS+Constants.TMP_DIR);
		if(!file.exists()){
			boolean success = (new File(Constants.TMP_DIR)).mkdir();
			if (!success) {
				JOptionPane.showMessageDialog(null, "Temp directory cannot be created.", "Tmp directory create error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
		// Create temp copy of current alignment
		String tmpFileName = System.getProperty("user.dir")+dS+Constants.TMP_DIR+dS+"tmp.mfa";
		File tmpFile = new File(tmpFileName);
		
		tmpFileName = "."+dS+Constants.TMP_DIR+dS+"tmp.mfa";
		
		try{
            AlignmentExporter.saveFasta(viewProps,tmpFile, false);
		}
		catch(IOException e){
			JOptionPane.showMessageDialog(alignFrame, "Temporary file cannot be created.", "File create error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Check output file name
		if(outFileName==null){ 
			outFileName= System.getProperty("user.dir")+dS+Constants.TMP_DIR+dS+"tmp.filtered.mfa";
		}


		String methodsDirName = "."+dS+Constants.METHODS_DIR+dS; 
		String command = methodsDirName;
		
		if(System.getProperty("os.name").toLowerCase().indexOf("nix") >=0 || System.getProperty("os.name").toLowerCase().indexOf("linux") >=0){
 			command += "filter_aln_linux32";	
 		}
 		else if(System.getProperty("os.name").toLowerCase().indexOf("win") >=0){
 			command += "filter_aln.exe";
 		}
 		else if(System.getProperty("os.name").toLowerCase().indexOf("mac") >=0){
 			command += "filter_aln.osx";
 		}
		
		try {
			String matrixRoute = Constants.CONFIG_DIR+"Maxhom_McLachlan.metric";
			String params = matrixRoute+" -M "+paramM+" -R "+paramR+" -F "+paramF+" "+(paramG ? "-G" : "")+" -O=F" ;
			String completeCommand = command+" "+tmpFileName+" "+params;
			// System.out.println(completeCommand);
			Process proc = Runtime.getRuntime().exec(completeCommand);
			StreamGobbler s1 = new StreamGobbler ("stdin", proc.getInputStream (), outFileName);
			StreamGobbler s2 = new StreamGobbler ("stderr", proc.getErrorStream (), null);
			s1.start ();
			s2.start ();
			proc.waitFor();
			

			// AlignFrame
			AlignFrame newAlignFrame = null;
			
			// Alignment Parser
			MfaParser parser = new MfaParser();

			AlignObject alignObject = null;
			try {
				alignObject = parser.read(outFileName);	
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null,
						"The alignment file couldn't be loaded! \n"
								+ "Please specify a proper location/format of the file.\nDetails:\n       "+(ex.getMessage().indexOf(":")!= -1 ? ex.getMessage().substring(ex.getMessage().indexOf(":")+1): ex.getMessage()),
						"File load error", JOptionPane.ERROR_MESSAGE);
						return;
			}

			// Init Methods
			Methods methods = new Methods();

			newAlignFrame = new AlignFrame(alignObject, methods, Constants.DEFAULT_WIDTH, Constants.DEFAULT_HEIGHT, true);
			newAlignFrame.pack();
			// Close parent if given
			if (alignFrame != null) {
				((AlignFrame) alignFrame).closeOtherWindows();							
				alignFrame.dispose();
			}
			newAlignFrame.setVisible(true);
			
			JOptionPane.showMessageDialog(newAlignFrame, "Filtered alignment succesfully loaded.", "Alignment filtered", JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
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
					/*else{
						System.out.println(line);
					}*/
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
	
	private class PresetItem{
	    String name;
	    String mValue;
	    String rValue;
	    String fValue;
	    boolean gValue;

		public PresetItem (String name, String mValue, String rValue, String fValue, boolean  gValue) {
			this.name = name;
			this.mValue = mValue;
			this.rValue = rValue;
			this.fValue = fValue;
			this.gValue = gValue;
		}

		/**
		 * @return the mValue
		 */
		@SuppressWarnings("unused")
		public String getName() {
			return name;
		}

		/**
		 * @return the mValue
		 */
		public String getmValue() {
			return mValue;
		}


		/**
		 * @return the rValue
		 */
		public String getrValue() {
			return rValue;
		}


		/**
		 * @return the fValue
		 */
		public String getfValue() {
			return fValue;
		}


		/**
		 * @return the gValue
		 */
		public boolean  getgValue() {
			return gValue;
		}
	
	}
}
