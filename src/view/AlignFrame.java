package view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import model.AlignObject;
import model.Methods;
import model.S3DetObject;
import model.SequenceImpl;
import model.UDMObject;
import model.XDetObject;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.BusinessBlackSteelSkin;
import org.pushingpixels.substance.internal.contrib.jgoodies.looks.Options;

import parser.MfaParser;
import parser.S3DetParser;
import parser.XDetParser;
import util.AlignmentExporter;
import util.ColorScheme;
import util.Constants;
import util.FileDrop;
import util.FilterAlignment;
import util.PluginLoader;
import util.ResidueSelectionManager;
import util.RunMethods;
import util.ScreenConfig;
import util.SelectionManager;
import util.SequenceLogo;
import util.SequenceManager;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.HeaderStyle;

/**
 * This class represents the main user interface containing the different panels,
 * such as the alignment panel.
 * @author Thilo Muth
 *
 */
public class AlignFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private AlignViewProps viewProps;
	private AlignPanel alignPanel;
	private Methods methods;
	private JMenuItem loadAlignmentItem;
	private JMenuItem exitMenuItem;
	private AlignFrame alignFrame;
	private JMenuItem exportMenuItem;
	private JMenuItem saveAlignmentItem;
	private AlignObject alObj;
	private JMenuItem loadMethodItem;	
	private JMenuItem loadS3DetItem;
	private JMenuItem loadStructureItem;
	private JMenuItem loadClusterItem;
	private JMenuItem loadXDetItem;
	private JMenu loadMethodMenu;
	private JMenu saveMethodMenu;
	private JMenuItem saveS3DetItem;
	private JMenuItem saveXDetItem;
	private SequencePainter seqPainter;
	private NamePainter namePainter;
	private StatusPanel statusPnl;
	private JMenu selectMethodItem;	
	private JMenuItem selectEntropyItem;
	private JMenuItem selectXDetItem;
	private JMenuItem selectS3Item;
	private JMenuItem filterAlignment;
	private JMenu runMethodMenu;	
	private JMenuItem runXDetItem;
	private JMenuItem runS3DetItem;
	private JMenu selectUdm;
	private JMenuItem[] selectUdmItems;
	private JMenuItem unselectResiduesItem;
	private JMenuItem unselectProteinsItem;
	private JMenuItem exportFastaItem;
	private JMenuItem showSelectionItem;
	private JMenuItem sequenceLogoItem;
	private JMenuItem dualLogoItem;	
	private JMenuItem clusterLogoItem;
	private JMenuItem manualMenuItem;
	private JMenuItem tutorialMenuItem;
	private JMenuItem aboutMenuItem;	
	private JCheckBoxMenuItem defaultColorItem;
	private JCheckBoxMenuItem mwColorItem;
	private JCheckBoxMenuItem polgColorItem;	
	private JCheckBoxMenuItem hdfColorItem;
	private JCheckBoxMenuItem userDefColorItem;
	private JCheckBoxMenuItem paletteRgbItem;
	private JCheckBoxMenuItem paletteRbItem;
	private Vector<JCheckBoxMenuItem> colorSchemeItems;	
	private Vector<JCheckBoxMenuItem> colorPaletteItems;
	private JPanel buttonBar;	
	public static boolean jarMode = true;
	JButton addGapButton; 
	JButton removeGapButton; 
	int gapId = 0;

	
	public AlignFrame(){
		
		// Application title
		super(Constants.APPTITLE);
		this.setSize(new Dimension(Constants.DEFAULT_WIDTH, Constants.DEFAULT_HEIGHT));
		alignFrame = this;		
		alignPanel = new AlignPanel(this);
		statusPnl = new StatusPanel(); 
		SelectionManager.setUpperSelectionOn(false);
		// Get the content pane
		Container cp = this.getContentPane();	
		try{
			getImageIcon("img/add.png");
		}
		catch(Exception e){
			jarMode = false;
		}
		constructButtonBar();		
		cp.add(alignPanel, BorderLayout.CENTER);
		cp.add(statusPnl, BorderLayout.SOUTH);
		// Construct the menu
		constructMenu();		
		this.addWindowListener(new WindowAdapter()
        {
            @Override
			public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });
		
		this.pack();
		
        new FileDrop( System.out, alignPanel,/*dragBorder,*/ new FileDrop.Listener()
        {   public void filesDropped( java.io.File[] files )
            {   for( int i = 0; i < files.length; i++ )
                {   try{
                		processDroppedFile(files[i].getCanonicalPath());                	
                    }   // end try
                    catch( java.io.IOException e ) {}
                }   // end for: through each dropped file
            }   // end filesDropped
        }); // end FileDrop.Listener
		
        // Clear selections when loading a new alignment 
        
		SequenceManager.emptySelectedSequences();
		SelectionManager.emptyCurrentSelection();
		ResidueSelectionManager.emptyCurrentSelection();

        
		// Center in the screen
		ScreenConfig.centerInScreen(this);		
		this.setVisible(true);
	}
	/**
	 * Constructor for a given AlignObject.
	 * @param alObj
	 * @param methods
	 * @param width
	 * @param height
	 */
	public AlignFrame(AlignObject alObj, Methods methods, int width, int height, boolean clearSelection) {
		
		// Application title
		super(Constants.APPTITLE);
		this.setSize(new Dimension(width, height));
		alignFrame = this;
		this.methods = methods;
		if(methods.getMethodNumber() > 0) {
			SelectionManager.setUpperSelectionOn(true);
		}
		this.alObj = alObj;
		viewProps = new AlignViewProps(alObj);		
		alignPanel = new AlignPanel(this, viewProps);
		seqPainter = alignPanel.getSeqPanel().getSeqPainter();
		namePainter = alignPanel.getNamePanel().getNameRenderer();
		// Get the content pane
		statusPnl = new StatusPanel(viewProps); 
		Container cp = this.getContentPane();		
		try{
			getImageIcon("img/add.png");
		}
		catch(Exception e){
			jarMode = false;
		}
		constructButtonBar();
		cp.add(alignPanel, BorderLayout.CENTER);	 
		cp.add(statusPnl, BorderLayout.SOUTH);
		// Construct the menu
		constructMenu();

		
		this.addWindowListener(new WindowAdapter()
        {
            @Override
			public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });
		
		this.pack();

        new FileDrop( System.out, alignPanel,/*dragBorder,*/ new FileDrop.Listener()
        {   public void filesDropped( java.io.File[] files )
            {   for( int i = 0; i < files.length; i++ )
                {   try{
                	processDroppedFile(files[i].getCanonicalPath());
                	System.out.println( files[i].getCanonicalPath() + "\n" );
                    }   // end try
                    catch( java.io.IOException e ) {}
                }   // end for: through each dropped file
            }   // end filesDropped
        }); // end FileDrop.Listener
		
        // Clear selections when loading a new alignment 
        if(clearSelection){
	        SequenceManager.emptySelectedSequences();
			SelectionManager.emptyCurrentSelection();
			ResidueSelectionManager.emptyCurrentSelection();
        }

        
		// Center in the screen
		ScreenConfig.centerInScreen(this);		
		this.setVisible(true);
	}

	/**
	 * This method is called when the frame is closed. It shuts down the JVM.
	 */
	public void close() {
		System.exit(0);
	}

	/**
	 * Construct the menu.
	 */
	private void constructMenu() {

	        JMenuBar menuBar = new JMenuBar();
	        menuBar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.SINGLE);
	       
	        // Defining the menus
	        JMenu fileMenu = new JMenu("File");
	        fileMenu.setMnemonic('F');
	        
	        JMenu editMenu = new JMenu("Selection");
	        editMenu.setMnemonic('L');

	        JMenu colorMenu = new JMenu("Color");
	        colorMenu.setMnemonic('O');	        
	        
	        JMenu helpMenu = new JMenu("Help");
	        helpMenu.setMnemonic('H');
	        menuBar.add(fileMenu);
	        menuBar.add(editMenu);        
	        menuBar.add(colorMenu);	        
	        menuBar.add(helpMenu);
	 

	        // The menu items	 
	        loadAlignmentItem = new JMenuItem();	  
	        filterAlignment = new JMenuItem();
	        loadS3DetItem = new JMenuItem();
	        loadXDetItem = new JMenuItem();
	        loadMethodItem = new JMenuItem();
	        runS3DetItem = new JMenuItem();
	        runXDetItem = new JMenuItem();
	        saveXDetItem = new JMenuItem();
	        saveS3DetItem = new JMenuItem();
	        loadStructureItem = new JMenuItem();
	        loadClusterItem = new JMenuItem();
	        exitMenuItem = new JMenuItem();
	        exportMenuItem = new JMenuItem();
	        saveAlignmentItem = new JMenuItem();

	        //File Items
	        loadAlignmentItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.ALT_MASK));
	        loadAlignmentItem.setMnemonic('N');
	        loadAlignmentItem.setText("Load Alignment");
	        loadAlignmentItem.setToolTipText("Loads a new alignment");
	        loadAlignmentItem.addActionListener(new ActionListener() {

	            public void actionPerformed(ActionEvent evt) {
	            	new LoadAlignFrame(alignFrame);
	            }
	        });
	        fileMenu.add(loadAlignmentItem);
	        
	        
	        filterAlignment.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.ALT_MASK));
	        filterAlignment.setMnemonic('T');
	        filterAlignment.setText("Filter Alignment");
	        filterAlignment.setToolTipText("Filter Alignment");
	        filterAlignment.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent evt) {
	            	new FilterAlignment(alignFrame, viewProps);
	            }
	        });
	        fileMenu.add(filterAlignment);
	        
	        
	        saveAlignmentItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK));
	        saveAlignmentItem.setMnemonic('S');
	        saveAlignmentItem.setText("Save Alignment");
	        saveAlignmentItem.setToolTipText("Export the alignment as FASTA File");
	        saveAlignmentItem.addActionListener(new ActionListener() {

	            public void actionPerformed(ActionEvent evt) {
	            	AlignmentExporter.exportFasta(alignFrame, viewProps, evt, false);
	            }
	        });
	        

	        fileMenu.add(saveAlignmentItem);
	        
	        fileMenu.addSeparator();
	        loadMethodMenu = new JMenu("Load Method Result");	       
	        loadMethodMenu.setToolTipText("Load Method Result");
	        fileMenu.add(loadMethodMenu);
	        
	        loadXDetItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.ALT_MASK));
	        loadXDetItem.setMnemonic('D');
	        loadXDetItem.setText("XDet");
	        loadXDetItem.setToolTipText("Load XDet Method");
	        loadXDetItem.addActionListener(new ActionListener() {

	            public void actionPerformed(ActionEvent evt) {
	            	new LoadXDetFrame(alignFrame);
	            }
	        });
	        loadMethodMenu.add(loadXDetItem);
	        
	        loadS3DetItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.ALT_MASK));
	        loadS3DetItem.setMnemonic('3');
	        loadS3DetItem.setText("S3Det");
	        loadS3DetItem.setToolTipText("Load S3Det Method");
	        loadS3DetItem.addActionListener(new ActionListener() {

	            public void actionPerformed(ActionEvent evt) {
	            	new LoadS3DetFrame(alignFrame);
	            }
	        });
	        loadMethodMenu.add(loadS3DetItem);
	        
	        loadMethodItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.ALT_MASK));
	        loadMethodItem.setMnemonic('U');
	        loadMethodItem.setText("User Defined");
	        loadMethodItem.setToolTipText("Load User Defined Method");
	        loadMethodItem.addActionListener(new ActionListener() {

	            public void actionPerformed(ActionEvent evt) {
	            	new LoadMethodFrame(alignFrame);
	            }
	        });
	        loadMethodMenu.add(loadMethodItem);

	        
	        if(PluginLoader.getPlugins(Constants.PLUGIN_TYPE_METHOD).size()!=0){
	        	JMenu conversionPluginsMenu = new JMenu("Plugins");	        	
	        	for(String pluginName: PluginLoader.getPlugins(Constants.PLUGIN_TYPE_CONVERSION)){
	        		JMenuItem loadPluginItem = new JMenuItem(pluginName);
	        		loadPluginItem.addActionListener(new ActionListener() {
	    	            public void actionPerformed(ActionEvent evt) {
	    	            	PluginLoader.loadConversionPlugin(((JMenuItem) evt.getSource()).getText(),alObj,alignFrame);
	    	            }
	    	        });
	        		conversionPluginsMenu.add(loadPluginItem);
	        		
	        			
	        	}
	        	loadMethodMenu.add(conversionPluginsMenu);
		        if(alObj == null) {
		        	conversionPluginsMenu.setEnabled(false);
		        }
	        }
	        
	        
	        
	        runMethodMenu = new JMenu("Run Method");
	        runMethodMenu.setToolTipText("Run Method");
	        fileMenu.add(runMethodMenu);
	        
	        //runXDetItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.ALT_MASK));
	        //runXDetItem.setMnemonic('D');
	        runXDetItem.setText("XDet");
	        runXDetItem.setToolTipText("Run XDet Method");
	        runXDetItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	new RunMethods(alignFrame, viewProps, "Xdet");
	            }
	        });
	        runMethodMenu.add(runXDetItem);
	        
	        //runS3DetItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK));
	        //runS3DetItem.setMnemonic('S');
	        runS3DetItem.setText("S3Det");
	        runS3DetItem.setToolTipText("Run S3Det Method");
	        runS3DetItem.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent evt) {
	            	new RunMethods(alignFrame, viewProps, "S3Det");	            	
	            }
	        });
	        runMethodMenu.add(runS3DetItem);
	        
	        if(PluginLoader.getPlugins(Constants.PLUGIN_TYPE_METHOD).size()!=0){
	        	JMenu pluginsMenu = new JMenu("Plugins");	        	
	        	for(String pluginName: PluginLoader.getPlugins(Constants.PLUGIN_TYPE_METHOD)){
	        		JMenuItem runPluginItem = new JMenuItem(pluginName);
	        		runPluginItem.addActionListener(new ActionListener() {
	    	            public void actionPerformed(ActionEvent evt) {
	    	            	PluginLoader.loadMethodPlugin(((JMenuItem) evt.getSource()).getText(),alObj,alignFrame);
	    	            }
	    	        });
	        		pluginsMenu.add(runPluginItem);
	        		
	        			
	        	}
	        	runMethodMenu.add(pluginsMenu);
		        if(alObj == null) {
		        	pluginsMenu.setEnabled(false);
		        }
	        }
	        
	        saveMethodMenu = new JMenu("Save Method Result");
	        saveMethodMenu.setToolTipText("Save Method Result");
	        fileMenu.add(saveMethodMenu);
	        
	        //saveXDetItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.ALT_MASK));
	        //saveXDetItem.setMnemonic('D');
	        saveXDetItem.setText("XDet");
	        saveXDetItem.setToolTipText("Save XDet Method");
	        saveXDetItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	AlignmentExporter.exportMethodFile(alignFrame, evt, "xdet");
	            }
	        });
	        saveMethodMenu.add(saveXDetItem);

	        if(methods != null){
	        	if(!methods.isxDetUsed()){
	        		saveXDetItem.setEnabled(false);
				}
	        }	        
	        
	        //saveS3DetItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.ALT_MASK));
	        //saveS3DetItem.setMnemonic('3');
	        saveS3DetItem.setText("S3Det");
	        saveS3DetItem.setToolTipText("Save S3Det Method");
	        saveS3DetItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	AlignmentExporter.exportMethodFile(alignFrame, evt, "S3Det");
	            }
	        });
	        saveMethodMenu.add(saveS3DetItem);	    
	        if(methods != null){
	        	if(!methods.isS3DetUsed()){
	        		saveS3DetItem.setEnabled(false);
				}
	        }	        
	        if(methods != null){
	        	if(!methods.isS3DetUsed() && !methods.isxDetUsed()){
	        		saveMethodMenu.setEnabled(false);
	        	}
	        }
	        
	        

	        fileMenu.addSeparator();


	        exportMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.ALT_MASK));
	        exportMenuItem.setMnemonic('E');
	        exportMenuItem.setText("Export PNG File");
	        exportMenuItem.setToolTipText("Export the alignment as PNG File");
	        exportMenuItem.addActionListener(new ActionListener() {

	            public void actionPerformed(ActionEvent evt) {
	            	AlignmentExporter.exportPNGFile(alignFrame, evt);
	            }
	        });
	        
	        fileMenu.add(exportMenuItem);
	        
	        fileMenu.addSeparator();
	        
	        loadStructureItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_MASK));
	        loadStructureItem.setMnemonic('P');
	        loadStructureItem.setText("Load Stucture (PDB)");
	        loadStructureItem.setToolTipText("Load Stucture (PDB)");
	        loadStructureItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
			    	 SwingUtilities.invokeLater(new Runnable() {
			    	      public void run() {			    	    	
				            	JFrame.setDefaultLookAndFeelDecorated(true);
			    	    	  SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin());
			    	    	  new LoadStructureFrame(viewProps, seqPainter, namePainter, alignFrame.getAlignPanel(),alignFrame);    	          
			    	      }
			    	    });			    	 
	            }
	        });	        
	        fileMenu.add(loadStructureItem);       
	        //fileMenu.add(exportMenuItem);
	        
	        loadClusterItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_MASK));
	        loadClusterItem.setMnemonic('C');
	        loadClusterItem.setText("Show Sequence Spaces");
	        loadClusterItem.setToolTipText("Show Sequence Spaces");
	        loadClusterItem.addActionListener(new ActionListener() {

	            public void actionPerformed(ActionEvent evt) {
	            	EventQueue.invokeLater(new Runnable() {
						public void run() {								
								new Cluster3DFrame(alignFrame.getMethods().getS3DetObject(), viewProps, seqPainter);
						}
					});
	            }
	        });
	        if(methods != null){
	        	if(!methods.isS3DetUsed()){
		        	loadClusterItem.setEnabled(false);
				}
	        }
	        
	        fileMenu.add(loadClusterItem);
	        
	        if(alObj == null) {
	        	filterAlignment.setEnabled(false);
	        	saveAlignmentItem.setEnabled(false);
	        	loadClusterItem.setEnabled(false);
	        	loadMethodMenu.setEnabled(false);
	        	runMethodMenu.setEnabled(false);
	        	saveMethodMenu.setEnabled(false);
	        	loadStructureItem.setEnabled(false);
	        	loadClusterItem.setEnabled(false);
	        	exportMenuItem.setEnabled(false);
	        }
	        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_MASK));
	        exitMenuItem.setMnemonic('x');
	        exitMenuItem.setText("Exit");
	        exitMenuItem.setToolTipText("Exit jDet");
	        exitMenuItem.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent evt) {
	        		close();
	        	}
	        });
	        fileMenu.addSeparator();
	        fileMenu.add(exitMenuItem);
	        
	        
	        //Edit Menu
	    	selectMethodItem = new JMenu();
	    	unselectResiduesItem = new JMenuItem();
	    	unselectProteinsItem = new JMenuItem();
	    	exportFastaItem = new JMenuItem();
	    	showSelectionItem = new JMenuItem();	    	
	    	sequenceLogoItem = new JMenuItem();
	    	dualLogoItem = new JMenuItem();
	    	clusterLogoItem =  new JMenuItem();
	    	
	    	selectMethodItem.setText("Selection from method");
	    	selectMethodItem.setToolTipText("Get selection from method ");
	    	
	    	selectEntropyItem  = new JMenuItem();
	    	selectEntropyItem.setText("Entropy");
	    	selectEntropyItem.setToolTipText("Get selection from Entropy");
	    	selectEntropyItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	selectEntropyItems(Constants.SELECTION_ONLY);	            	
	            }
	        });
	    	selectMethodItem.add(selectEntropyItem);
	        if(methods != null && methods.isEntropyUsed()){
	        	selectEntropyItem.setEnabled(true);
	        }
	        else{
	        	selectEntropyItem.setEnabled(false);
	        }

	    	
	    	selectXDetItem  = new JMenuItem();
	    	selectXDetItem.setText("XDet");
	    	selectXDetItem.setToolTipText("Get selection from XDet");
	    	selectXDetItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	selectXDetItems(Constants.SELECTION_ONLY);
	            }
	        });
	    	selectMethodItem.add(selectXDetItem);
	        if(methods != null && methods.isxDetUsed()){
	        	selectXDetItem.setEnabled(true);
	        }
	        else{
	        	selectXDetItem.setEnabled(false);
	        }

	    	selectS3Item = new JMenuItem();
	    	selectS3Item.setText("S3");
	    	selectS3Item.setToolTipText("Get selection from S3");
	    	selectS3Item.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	selectS3DetItems(Constants.SELECTION_ONLY);
	            }
	        });
	        
	    	if(methods != null && methods.isS3DetUsed()){
	        	selectS3Item.setEnabled(true);
	        }
	        else{
	        	selectS3Item.setEnabled(false);
	        }
	    	
	    	selectMethodItem.add(selectS3Item);
	    	
	        if(methods != null && (methods.isS3DetUsed() || methods.isEntropyUsed() || methods.isxDetUsed() || methods.getUdmNumber()>0)){
	        	selectMethodItem.setEnabled(true);
	        }
	        else{
	        	selectMethodItem.setEnabled(false);
	        }

	        editMenu.add(selectMethodItem);	 
	        
	        selectUdm = new JMenu();
	        selectUdm.setText("User Defined");
	        if(methods != null && (methods.getUdmNumber()>0)){	 
	        	selectUdmItems = new JMenuItem[methods.getUdmNumber()];
	        	for (int j =0; j< methods.getUdmNumber();j++){
	        		selectUdmItems[j] = new JMenuItem();
        			selectUdmItems[j].setText(methods.getUdmObjects()[j].getMethodName());
	        		selectUdmItems[j].setToolTipText("Get selection from "+methods.getUdmObjects()[j].getMethodName());
	        		selectUdmItems[j].addActionListener(new ActionListener() {
			            public void actionPerformed(ActionEvent evt) {
			            	JMenuItem source = (JMenuItem) evt.getSource();			            	
			            	selectUserDefItems(source.getToolTipText(),Constants.SELECTION_ONLY);

			            }
			        });
	        		selectUdm.add(selectUdmItems[j]);
	        	}
	        	selectUdm.setEnabled(true);
	        }
	        else{
	        	selectUdm.setEnabled(false);
	        }
	        
	        selectMethodItem.add(selectUdm);
	    	
	    	
	        unselectProteinsItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.ALT_MASK));
	        unselectProteinsItem.setMnemonic('T');
	        unselectProteinsItem.setText("Unselect Proteins");
	        unselectProteinsItem.setToolTipText("Clear the current selection");
	        unselectProteinsItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	SequenceManager.emptySelectedSequences();	            		 
	            	seqPainter.notifyObservers();
	            }
	        });	      
	        if(alObj == null) {
	        	unselectProteinsItem.setEnabled(false);
	        }
	        editMenu.add(unselectProteinsItem);
	        
	        unselectResiduesItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.ALT_MASK));
	        unselectResiduesItem.setMnemonic('R');
	        unselectResiduesItem.setText("Unselect Residues");
	        unselectResiduesItem.setToolTipText("Clear the current selection");
	        unselectResiduesItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	SelectionManager.emptyCurrentSelection();
	            	ResidueSelectionManager.emptyCurrentSelection();
	            	seqPainter.repaint();
	            	seqPainter.notifyObservers();
	            }
	        });	      
	        if(alObj == null) {
	        	unselectResiduesItem.setEnabled(false);
	        }
	        editMenu.add(unselectResiduesItem);
	        
	        editMenu.addSeparator();
	        
	        exportFastaItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.ALT_MASK)); 
	        exportFastaItem.setMnemonic('V');
	        exportFastaItem.setText("Export as fasta");
	        exportFastaItem.setToolTipText("Export selection as fasta");
	        exportFastaItem.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent evt) {
	            	AlignmentExporter.exportFasta(alignFrame, viewProps, evt, true);
	            }
	        });	      
	        if(alObj == null) {
	        	exportFastaItem.setEnabled(false);
	        }
	        editMenu.add(exportFastaItem);

	        
        	
	        showSelectionItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.ALT_MASK)); 
	        showSelectionItem.setMnemonic('W');
	        showSelectionItem.setText("Show selection");
	        showSelectionItem.setToolTipText("Show selection in new window");
	        showSelectionItem.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent evt) {
	            	new SelectionFrame(viewProps, alignFrame.getMethods().getS3DetObject());
	            }
	        });	      
	        if(alObj == null) {
	        	showSelectionItem.setEnabled(false);
	        }
	        editMenu.add(showSelectionItem);
	        

	        sequenceLogoItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.ALT_MASK)); 
	        sequenceLogoItem.setMnemonic('Q');
	        sequenceLogoItem.setText("Sequence logo");
	        sequenceLogoItem.setToolTipText("Create sequence logo from selection");
	        sequenceLogoItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	new SequenceLogo(viewProps);
	            }
	        });	      
	        if(alObj == null) {
	        	sequenceLogoItem.setEnabled(false);
	        }
	        editMenu.add(sequenceLogoItem);
	        
 
	        dualLogoItem.setText("Selection Vs Complementary logo");
	        dualLogoItem.setToolTipText("Create sequence for selection vs complementary set.");
	        dualLogoItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	if(SequenceManager.isEmpty()){
	            		new SequenceLogo(viewProps);	            		
	            	}
	            	else{
	            		new SequenceLogo(viewProps,true);
	            	}
	            }
	        });	      
	        if(alObj == null) {
	        	dualLogoItem.setEnabled(false);
	        }
	        editMenu.add(dualLogoItem);

	        clusterLogoItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.ALT_MASK)); 
	        clusterLogoItem.setMnemonic('B');
	        clusterLogoItem.setText("Subfamilies logo");
	        clusterLogoItem.setToolTipText("Create logo independently for subfamilies");
	        clusterLogoItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	new SequenceLogo(viewProps, methods.getS3DetObject());
	            }
	        });	      
	    	if(methods != null && methods.isS3DetUsed()){
	    		clusterLogoItem.setEnabled(true);
	        }
	        else{
	        	clusterLogoItem.setEnabled(false);
	        }

	        
	        editMenu.add(clusterLogoItem);
	        
	        
	        
	        //Color menu	        
	        colorSchemeItems = new Vector<JCheckBoxMenuItem>();
	    	defaultColorItem = new JCheckBoxMenuItem();
	    	colorSchemeItems.add(defaultColorItem);
	    	mwColorItem = new JCheckBoxMenuItem();
	    	colorSchemeItems.add(mwColorItem);
	    	polgColorItem = new JCheckBoxMenuItem();
	    	colorSchemeItems.add(polgColorItem);	    	
	    	hdfColorItem = new JCheckBoxMenuItem();
	    	colorSchemeItems.add(hdfColorItem);	    	
	    	userDefColorItem = new JCheckBoxMenuItem();
	    	colorSchemeItems.add(userDefColorItem);

	    	
	    	colorPaletteItems = new Vector<JCheckBoxMenuItem>();
	    	paletteRgbItem = new JCheckBoxMenuItem();
	    	colorPaletteItems.add(paletteRgbItem);
	    	paletteRbItem = new JCheckBoxMenuItem();
	    	colorPaletteItems.add(paletteRbItem);

	    	defaultColorItem.setText(ColorScheme.CLUSTAL_X+" (default)");
	    	defaultColorItem.setToolTipText("Set "+ColorScheme.CLUSTAL_X+" color scheme");
	    	defaultColorItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	ColorScheme.setColorScheme(ColorScheme.CLUSTAL_X);
	            	checkSelectedColor();
	            	seqPainter.repaint();
	            	seqPainter.notifyObservers();	            	
	            }
	        });	      
	    	
	    	mwColorItem.setText(ColorScheme.MW);
	    	mwColorItem.setToolTipText("Set "+ColorScheme.MW+" color scheme");
	    	mwColorItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	ColorScheme.setColorScheme(ColorScheme.MW);
	            	checkSelectedColor();
	            	seqPainter.repaint();
	            	seqPainter.notifyObservers();	            	
	            }
	        });	      

	    	polgColorItem.setText(ColorScheme.POL_G);
	    	polgColorItem.setToolTipText("Set "+ColorScheme.POL_G+" color scheme");
	    	polgColorItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	ColorScheme.setColorScheme(ColorScheme.POL_G);
	            	checkSelectedColor();
	            	seqPainter.repaint();
	            	seqPainter.notifyObservers();	            	
	            }
	        });	      
	    	
	    	
	    	hdfColorItem.setText(ColorScheme.HDF);
	    	hdfColorItem.setToolTipText("Set "+ColorScheme.HDF+" color scheme");
	    	hdfColorItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {	        
	            	ColorScheme.setColorScheme(ColorScheme.HDF);
	            	checkSelectedColor();
	            	seqPainter.repaint();
	            	seqPainter.notifyObservers();
	            }
	        });	      

	    	userDefColorItem.setText(ColorScheme.USER_DEF);
	    	userDefColorItem.setToolTipText("Load user defined color scheme");
	    	userDefColorItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	if(ColorScheme.loadColorScheme(alignFrame)){
	            		seqPainter.repaint();
	            		seqPainter.notifyObservers();
	            	}
	            	checkSelectedColor();	            	
	            }
	        });	      

	    	paletteRgbItem.setText("RGB scale");
	    	paletteRgbItem.setToolTipText("Load red-green-blue scale");
	    	paletteRgbItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	ColorScheme.setColorPalette(ColorScheme.PAL_RED_GREEN_BLUE);
	            	checkSelectedColor();
	            	seqPainter.repaint();
	            	seqPainter.notifyObservers();	            	
	            }
	        });	      

	    	paletteRbItem.setText("RB scale");
	    	paletteRbItem.setToolTipText("Load redblue scale");
	    	paletteRbItem.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	ColorScheme.setColorPalette(ColorScheme.PAL_RED_BLUE);
	            	checkSelectedColor();
	            	seqPainter.repaint();
	            	seqPainter.notifyObservers();	            	
	            }
	        });	      
	    	ColorScheme.init();	    	
	    	if(alObj == null) {
	        	defaultColorItem.setEnabled(false);
		    	mwColorItem.setEnabled(false);
		    	polgColorItem.setEnabled(false);
		    	hdfColorItem.setEnabled(false);
		    	userDefColorItem.setEnabled(false);
		    	paletteRgbItem.setEnabled(false);
		    	paletteRbItem.setEnabled(false);
	        }
	    	else{
	    		checkSelectedColor();
	    	}
	        
	    	colorMenu.add(defaultColorItem);
	    	colorMenu.add(mwColorItem);
  	    	colorMenu.add(polgColorItem);	
   	    	colorMenu.add(hdfColorItem);
   	    	colorMenu.add(userDefColorItem);
   	    	colorMenu.addSeparator();
	    	colorMenu.add(paletteRgbItem);
	    	colorMenu.add(paletteRbItem);

	        //Help items
	        manualMenuItem = new JMenuItem();
	    	tutorialMenuItem = new JMenuItem();
	        aboutMenuItem = new JMenuItem();
	        
	        manualMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, java.awt.event.InputEvent.ALT_MASK));
	        //manualMenuItem.setMnemonic('H');
	        manualMenuItem.setText("User manual");
	        manualMenuItem.addActionListener(new java.awt.event.ActionListener() {

	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	            	try{
		            	Desktop desktop = Desktop.getDesktop();
	                    URI uri = new java.net.URI(Constants.FILE_PREFIX+Constants.MANUAL_PATH);
	                    desktop.browse( uri );
	            	}
	            	catch(Exception e){
	        	 		if(System.getProperty("os.name").toLowerCase().indexOf("win") >=0){
	        	 			try{
	        	 				Runtime.getRuntime().exec("start "+Constants.MANUAL_PATH);
	        	 			}catch(Exception ex){
	        	 				JOptionPane.showMessageDialog(alignFrame, "Web browser couldn't be opened. \nPlease, manually open the "+Constants.MANUAL_PATH+" file.", "Web browser couldn't be opened.", JOptionPane.ERROR_MESSAGE);	        	 				
	        	 			}
	        	 		}
	        	 		else{
	        	 			JOptionPane.showMessageDialog(alignFrame, "Web browser couldn't be opened. \nPlease, manually open the "+Constants.MANUAL_PATH+" file.", "Web browser couldn't be opened.", JOptionPane.ERROR_MESSAGE);
	        	 		}
	            	}
	            }
	        });
	        helpMenu.add(manualMenuItem);
	        
	        //tutorialMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, 0));
	        //tutorialMenuItem.setMnemonic('H');
	        tutorialMenuItem.setText("Tutorial");
	        tutorialMenuItem.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	            	try{
		            	Desktop desktop = Desktop.getDesktop();
	                    URI uri = new java.net.URI(Constants.FILE_PREFIX+Constants.TUTORIAL_PATH);
	                    desktop.browse( uri );
	            	}
	            	catch(Exception e){
	        	 		if(System.getProperty("os.name").toLowerCase().indexOf("win") >=0){
	        	 			try{
	        	 				Runtime.getRuntime().exec("start "+Constants.TUTORIAL_PATH);
	        	 			}catch(Exception ex){
	        	 				JOptionPane.showMessageDialog(alignFrame, "Web browser couldn't be opened. \nPlease, manually open the "+Constants.TUTORIAL_PATH+" file.", "Web browser couldn't be opened.", JOptionPane.ERROR_MESSAGE);	        	 				
	        	 			}
	        	 		}
	        	 		else{
	        	 			JOptionPane.showMessageDialog(alignFrame, "Web browser couldn't be opened. \nPlease, manually open the "+Constants.TUTORIAL_PATH+" file.", "Web browser couldn't be opened.", JOptionPane.ERROR_MESSAGE);
	        	 		}
	            	}
	            }
	        });
	        helpMenu.add(tutorialMenuItem);

	        helpMenu.addSeparator();

	        //aboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, java.awt.event.InputEvent.ALT_MASK));
	        aboutMenuItem.setText("About");
	        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {

	            public void actionPerformed(ActionEvent e) {
	                aboutTriggered();
	            }
	        });
	        helpMenu.add(aboutMenuItem);
	        // LAF changing
	        //menuBar.add(SampleMenuFactory.getLookAndFeelMenu(this));
	        setJMenuBar(menuBar);
	}
	
	private void checkSelectedColor(){
		ColorScheme.updatePaletteImage(statusPnl.getColorPalette());
		statusPnl.repaint();
		for (int i =0; i<colorSchemeItems.size();i++ ){
			if(i == ColorScheme.getColorScheme()){
				colorSchemeItems.get(i).setSelected(true);
			}
			else{
				colorSchemeItems.get(i).setSelected(false);
			}
		}
		if(ColorScheme.getColorSchemeNames() == null || ColorScheme.getColorScheme() == ColorScheme.getColorSchemeNames().indexOf(ColorScheme.CLUSTAL_X)){
			for (int i =0; i<colorPaletteItems.size();i++ ){
				colorPaletteItems.get(i).setEnabled(false);
				colorPaletteItems.get(i).setSelected(false);
			}
		}
		else{
			for (int i =0; i<colorPaletteItems.size();i++ ){
				colorPaletteItems.get(i).setEnabled(true);
				if(i == ColorScheme.getColorPalette()){
					colorPaletteItems.get(i).setSelected(true);
				}
				else{
					colorPaletteItems.get(i).setSelected(false);
				}
			}
		}
	}
	
	/**
	 * Construct the button panel
	 */
	private void constructButtonBar() {

	        buttonBar = new JPanel();
			// Button Panel Layout
	        buttonBar.setLayout(new FormLayout("2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 2dlu, p, 10dlu, p, 2dlu, p, 10dlu, p, 10dlu, p, 2dlu, p, 10dlu, p, 2dlu, p, 10dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 2dlu, p, 10dlu, p, 2dlu, p", "30px"));
			CellConstraints cc = new CellConstraints();
			
			ImageIcon loadAlignmentIcon = getImageIcon("img/align.png");
	        JButton loadAlignmentButton = new JButton(loadAlignmentIcon);
	        loadAlignmentButton.setToolTipText("Load Alignment");	        
	        loadAlignmentButton.addActionListener(new ActionListener() {

	            public void actionPerformed(ActionEvent evt) {
	            	new LoadAlignFrame(alignFrame);
	            }
	        });
	        buttonBar.add(loadAlignmentButton, cc.xy(2, 1));

			ImageIcon filterAlignmentIcon = getImageIcon("img/filteralign.png");
	        JButton filterAlignmentButton = new JButton(filterAlignmentIcon);
	        filterAlignmentButton.setToolTipText("Filter Alignment");	        
	        filterAlignmentButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	new FilterAlignment(alignFrame, viewProps);
	            }
	        });
	        buttonBar.add(filterAlignmentButton, cc.xy(4, 1));

	        ImageIcon saveAlignmentIcon = getImageIcon("img/savealign.png");
	        JButton saveAlignmentButton = new JButton(saveAlignmentIcon);
	        saveAlignmentButton.setToolTipText("Save Alignment");	        
	        saveAlignmentButton.addActionListener(new ActionListener() {

	            public void actionPerformed(ActionEvent evt) {
	            	AlignmentExporter.exportFasta(alignFrame, viewProps, evt, false);
	            }
	        });
	        buttonBar.add(saveAlignmentButton, cc.xy(6, 1));
	        
	        
	        ImageIcon loadXDetIcon = getImageIcon("img/xdet.png");
	        JButton loadXDetButton = new JButton(loadXDetIcon); 
	        loadXDetButton.setToolTipText("Load XDet Method");
	        loadXDetButton.addActionListener(new ActionListener() {

	            public void actionPerformed(ActionEvent evt) {
	            	new LoadXDetFrame(alignFrame);
	            }
	        });
	        buttonBar.add(loadXDetButton, cc.xy(8, 1));
	        
	        ImageIcon loadS3DetIcon = getImageIcon("img/s3det.png");
	        JButton loadS3DetButton = new JButton(loadS3DetIcon); 
	        
	        loadS3DetButton.setToolTipText("Load S3Det Method");
	        loadS3DetButton.addActionListener(new ActionListener() {

	            public void actionPerformed(ActionEvent evt) {
	            	new LoadS3DetFrame(alignFrame);
	            }
	        });
	        buttonBar.add(loadS3DetButton, cc.xy(10, 1));
	        
	        ImageIcon loadMethodIcon = getImageIcon("img/userdef.png");
	        JButton loadMethodButton = new JButton(loadMethodIcon); 
	        loadMethodButton.setToolTipText("Load User Defined Method");
	        loadMethodButton.addActionListener(new ActionListener() {

	            public void actionPerformed(ActionEvent evt) {
	            	new LoadMethodFrame(alignFrame);
	            }
	        });	        
	        buttonBar.add(loadMethodButton, cc.xy(12, 1));
	        
	        
	        ImageIcon runXDetIcon = getImageIcon("img/run_xdet.png");
	        JButton runXDetButton = new JButton(runXDetIcon); 
	        runXDetButton.setToolTipText("Run XDet on current alignment.");
	        runXDetButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	new RunMethods(alignFrame, viewProps, "Xdet");
	            }
	        });
	        
	        buttonBar.add(runXDetButton, cc.xy(14, 1));

	        ImageIcon runS3DetIcon = getImageIcon("img/run_s3det.png");
	        JButton runS3DetButton = new JButton(runS3DetIcon); 
	        runS3DetButton.setToolTipText("Run S3Det on current alignment.");
	        runS3DetButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	new RunMethods(alignFrame, viewProps, "S3Det");
	            }
	        });
	        
	        buttonBar.add(runS3DetButton, cc.xy(16, 1));

	        
	        
	        ImageIcon saveXDetIcon = getImageIcon("img/save_xdet.png");
	        JButton saveXDetButton = new JButton(saveXDetIcon); 
	        saveXDetButton.setToolTipText("Save XDet results.");
	        saveXDetButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	AlignmentExporter.exportMethodFile(alignFrame, evt, "xdet");
	            }
	        });
	        
	        buttonBar.add(saveXDetButton, cc.xy(18, 1));
	        
	        
	        ImageIcon saveS3DetIcon = getImageIcon("img/save_s3det.png");
	        JButton saveS3DetButton = new JButton(saveS3DetIcon); 
	        saveS3DetButton.setToolTipText("Save S3Det results.");
	        saveS3DetButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	AlignmentExporter.exportMethodFile(alignFrame, evt, "S3Det");
	            }
	        });
	        
	        buttonBar.add(saveS3DetButton, cc.xy(20, 1));
	        
	        
	        ImageIcon exportIcon = getImageIcon("img/export.png");
	        JButton exportButton = new JButton(exportIcon); 
	        exportButton.setToolTipText("Export the alignment as PNG File");
	        exportButton.addActionListener(new ActionListener() {

	            public void actionPerformed(ActionEvent evt) {
	            	AlignmentExporter.exportPNGFile(alignFrame, evt);
	            }
	        });
	        
	        buttonBar.add(exportButton, cc.xy(22, 1));
	       
	        ImageIcon loadStructureIcon = getImageIcon("img/structure.png");
	        JButton loadStructureButton = new JButton(loadStructureIcon); 
	        loadStructureButton.setToolTipText("Load Stucture (PDB)");
	        loadStructureButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
			    	 SwingUtilities.invokeLater(new Runnable() {
			    	      public void run() {			    	    	
				            	JFrame.setDefaultLookAndFeelDecorated(true);
			    	    	  SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin());
			    	    	  new LoadStructureFrame(viewProps, seqPainter, namePainter, alignFrame.getAlignPanel(),alignFrame);    	          
			    	      }
			    	    });			    	 
	            }
	        });       
	        buttonBar.add(loadStructureButton, cc.xy(24, 1));
	        
	        ImageIcon loadClusterIcon = getImageIcon("img/cluster.png");
	        JButton loadClusterButton = new JButton(loadClusterIcon); 
	        loadClusterButton.setToolTipText("Show Sequence Spaces");
	        loadClusterButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	EventQueue.invokeLater(new Runnable() {
						public void run() {								
								new Cluster3DFrame(alignFrame.getMethods().getS3DetObject(), viewProps, seqPainter);
						}
					});
	            }
	        }); 
        
	        buttonBar.add(loadClusterButton, cc.xy(26, 1));
	        
	      
	        
	        ImageIcon unselectProteinsIcon = getImageIcon("img/unselectproteins.png");
			JButton unselectProteinsButton = new JButton(unselectProteinsIcon);
			unselectProteinsButton.setToolTipText("Clear current protein selection");
	        unselectProteinsButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	SequenceManager.emptySelectedSequences();	            		 
	            	seqPainter.notifyObservers();
	            }
	        });		        
	        buttonBar.add(unselectProteinsButton, cc.xy(28, 1));
	        
	        ImageIcon unselectResiduesIcon = getImageIcon("img/unselectresidues.png");
	        JButton unselectResiduesButton = new JButton(unselectResiduesIcon); 
	        unselectResiduesButton.setToolTipText("Clear current residue selection");
	        unselectResiduesButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	SelectionManager.emptyCurrentSelection();
	            	ResidueSelectionManager.emptyCurrentSelection();	            	
	            	seqPainter.repaint();
	            	seqPainter.notifyObservers();
	            }
	        });		        
	        buttonBar.add(unselectResiduesButton, cc.xy(30, 1));


	        ImageIcon exportFastaIcon = getImageIcon("img/fasta.png");
	        JButton exportFastaButton = new JButton(exportFastaIcon); 
	        exportFastaButton.setToolTipText("Export selection as fasta");
	        exportFastaButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	AlignmentExporter.exportFasta(alignFrame, viewProps, evt, true);
	            }
	        });		        
	        buttonBar.add(exportFastaButton, cc.xy(32, 1));
	        
	        ImageIcon showSelectionIcon = getImageIcon("img/showselection.png");
	        JButton showSelectionButton = new JButton(showSelectionIcon); 
	        showSelectionButton.setToolTipText("Show selection in new window");
	        showSelectionButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	new SelectionFrame((AlignViewProps)viewProps.clone(), alignFrame.getMethods().getS3DetObject());
	            }
	        });		        
	        buttonBar.add(showSelectionButton, cc.xy(34, 1));
	        
	        ImageIcon sequenceLogoIcon = getImageIcon("img/logos.png");
	        JButton sequenceLogoButton = new JButton(sequenceLogoIcon); 
	        sequenceLogoButton.setToolTipText("Create sequence logo from selection");
	        sequenceLogoButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	new SequenceLogo(viewProps);
	            }
	        });		        
	        buttonBar.add(sequenceLogoButton, cc.xy(36, 1));

	        ImageIcon dualLogoIcon = getImageIcon("img/dualLogos.png");
	        JButton dualLogoButton = new JButton(dualLogoIcon); 
	        dualLogoButton.setToolTipText("Create sequence for selection vs complementary set.");
	        dualLogoButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	if(SequenceManager.isEmpty()){
	            		new SequenceLogo(viewProps);	            		
	            	}
	            	else{
	            		new SequenceLogo(viewProps,true);
	            	}
	            }
	        });		        
	        buttonBar.add(dualLogoButton, cc.xy(38, 1));	        
	        
	        ImageIcon clusterLogoIcon = getImageIcon("img/clusterLogos.png");
	        JButton clusterLogoButton = new JButton(clusterLogoIcon); 
	        clusterLogoButton.setToolTipText("Create logo independently for subfamilies");
	        clusterLogoButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	new SequenceLogo(viewProps, methods.getS3DetObject());
	            }
	        });		        
	        buttonBar.add(clusterLogoButton, cc.xy(40, 1));

	        ImageIcon addGapIcon = getImageIcon("img/addgap.png");
	        addGapButton = new JButton(addGapIcon); 
	        addGapButton.setToolTipText("Add gap on selected position");
	        addGapButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	if(methods.getMethodNumber()!= 0){
    	 				if(JOptionPane.showConfirmDialog(alignFrame, "There is a method loaded, to enable alignment modification it will be unloaded. Do you want to continue?")==JOptionPane.OK_OPTION){
    		            	alObj.reloadOnlyAlignment(alignFrame);
    	 				}
	            	}
	            	else{
		            	// Look for position to modify
		            	if(SelectionManager.getLastSelection() != SelectionManager.NO_SELECTION){
			            	int gapPosition = SelectionManager.getLastSelection()-1; 
			            	// If there are selected sequences
			            	if(!SequenceManager.isEmpty()){
				            	SequenceImpl[] seqs = alignFrame.getAlObj().getSequences();
			            		boolean allRemainAreGaps = true;
			            		for (SequenceImpl currentSeq:seqs){
			            			if(currentSeq.getLetterAt(gapPosition)!='-'){
					            		if(!SequenceManager.getSelectedSequences().contains(currentSeq.getName())){
					            			allRemainAreGaps = false;
					            		}
			            			}		            			
			            		}
			            		if (!allRemainAreGaps){
				            		for(int i=0; i<seqs.length;i++){
					            		if(SequenceManager.getSelectedSequences().contains(seqs[i].getName())){
					            			seqs[i].setSequence(seqs[i].getSequenceAsString().substring(0,gapPosition)+"-"+seqs[i].getSequenceAsString().substring(gapPosition));	            			
					            		}
					            		else{
					            			seqs[i].setSequence(seqs[i].getSequenceAsString()+"-");
					            		}
					            	}
			            		}
			            		else{
				            		for(int i=0; i<seqs.length;i++){
					            		if(!SequenceManager.getSelectedSequences().contains(seqs[i].getName())){
					            			seqs[i].setSequence(seqs[i].getSequenceAsString().substring(0,gapPosition)+seqs[i].getSequenceAsString().substring(gapPosition+1)+"-");
					            		}
					            	}
			            		}
				            	alObj.setSequences(seqs);

			            		/* Remove possible gaps */
			            		alObj.clearGaps();

				            	// Update sequence number and draw an status bar
				            	statusPnl.updateWidth();
				            	seqPainter.repaint();
				            	seqPainter.notifyObservers();
			            	}
			            	else{
			            		JOptionPane.showMessageDialog(alignFrame, "Select at least one sequence to add gaps.", "Unselected sequences.", JOptionPane.INFORMATION_MESSAGE);	
			            	}
		            	}
		            	else{
	            		JOptionPane.showMessageDialog(alignFrame, "Select a position to add gaps.", "No selection", JOptionPane.INFORMATION_MESSAGE);	
		            	}
		            	
	            	}
	            }
	        });		        
	        buttonBar.add(addGapButton, cc.xy(42, 1));

	        ImageIcon removeGapIcon = getImageIcon("img/removegap.png");
	        removeGapButton = new JButton(removeGapIcon); 
	        removeGapButton.setToolTipText("Remove gap selected position");
	        removeGapButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {	            	
	            	if(methods.getMethodNumber()!= 0){
    	 				if(JOptionPane.showConfirmDialog(alignFrame, "There are method(s) loaded, to enable alignment modification thy will be unloaded. Do you want to continue?")==JOptionPane.OK_OPTION){
    		            	alObj.reloadOnlyAlignment(alignFrame);
    	 				}
	            	}
	            	else{
		            	if(SelectionManager.getLastSelection() != SelectionManager.NO_SELECTION){
			            	// Look for position to modify
			            	int gapPosition = SelectionManager.getLastSelection()-1; 

		            		// If there are selected sequences
			            	if(!SequenceManager.isEmpty()){
				            	// Check if all positions on selected sequences are gaps
			            		boolean allGaps = true;
			            		for (String currentSeq:SequenceManager.getSelectedSequences()){
			            			if(SequenceManager.getSequence(currentSeq, alignFrame.getAlObj()).getLetterAt(gapPosition)!='-'){
			            				allGaps = false;
			            			}
			            		}
				            	SequenceImpl[] seqs = alignFrame.getAlObj().getSequences();
	
			            		if (allGaps){
			            			for(int i=0; i<seqs.length;i++){
					            		if(SequenceManager.getSelectedSequences().contains(seqs[i].getName())){
					            			seqs[i].setSequence(seqs[i].getSequenceAsString().substring(0,gapPosition)+seqs[i].getSequenceAsString().substring(gapPosition+1)+"-");	            			
					            		}
					            	}
			            		}
			            		else{
				            		for(int i=0; i<seqs.length;i++){
					            		if(SequenceManager.getSelectedSequences().contains(seqs[i].getName())){
					            			seqs[i].setSequence(seqs[i].getSequenceAsString()+"-");
					            		}
					            		else{
					            			seqs[i].setSequence(seqs[i].getSequenceAsString().substring(0,gapPosition)+"-"+seqs[i].getSequenceAsString().substring(gapPosition));
					            		}
					            	}
			            		}
				            	alObj.setSequences(seqs);

			            		/* Remove possible gaps */
			            		alObj.clearGaps();
			            		

				            	// Update sequence number and draw an status bar
				            	statusPnl.updateWidth();
				            	seqPainter.repaint();
				            	seqPainter.notifyObservers();
			            	}
			            	else{
			            		JOptionPane.showMessageDialog(alignFrame, "Select at least one sequence to remove gaps.", "Unselected sequences", JOptionPane.INFORMATION_MESSAGE);	
			            	}
		            	}
		            	else{
		            		JOptionPane.showMessageDialog(alignFrame, "Select a position to remove gaps.", "No selection", JOptionPane.INFORMATION_MESSAGE);	
		            	}
		            	
	            	}
	            }
	        });		        
	        buttonBar.add(removeGapButton, cc.xy(44, 1));	
	        
	        
	        if(alObj == null) {
	        	saveAlignmentButton.setEnabled(false);
	        	filterAlignmentButton.setEnabled(false);
	        	loadXDetButton.setEnabled(false);
	        	loadS3DetButton.setEnabled(false);
	        	loadMethodButton.setEnabled(false);
	        	runXDetButton.setEnabled(false);
	        	runS3DetButton.setEnabled(false);
	        	saveXDetButton.setEnabled(false);
	        	saveS3DetButton.setEnabled(false);
	        	loadClusterButton.setEnabled(false);
	        	loadStructureButton.setEnabled(false);
	        	exportButton.setEnabled(false);
	        	unselectResiduesButton.setEnabled(false);
	        	unselectProteinsButton.setEnabled(false);
	        	exportFastaButton.setEnabled(false);
	        	showSelectionButton.setEnabled(false);
	        	sequenceLogoButton.setEnabled(false);
	        	dualLogoButton.setEnabled(false);	        	
	        	clusterLogoButton.setEnabled(false);
	        	addGapButton.setEnabled(false);
	        	removeGapButton.setEnabled(false);
	        }
	        
	        if(methods != null){
	        	if(!methods.isxDetUsed()){
	        		saveXDetButton.setEnabled(false);
				}
	        }	        

	        if(methods != null){
	        	if(!methods.isS3DetUsed()){
	        		clusterLogoButton.setEnabled(false);
	        		loadClusterButton.setEnabled(false);
	        		saveS3DetButton.setEnabled(false);	        		
				}
	        }	
	        
	        
	        this.getContentPane().add(buttonBar, BorderLayout.NORTH);
	}
	/*
	private void runMethod(ActionEvent evt, String method){
		File file=new File(System.getProperty("user.dir")+"/"+Constants.TMP_DIR);
		if(!file.exists()){
			boolean success = (new File(Constants.TMP_DIR)).mkdir();
			if (!success) {
				JOptionPane.showMessageDialog(this, "Temp directory cannot be created.", "Tmp directory create error", JOptionPane.ERROR_MESSAGE);
			}  
		}
		String tmpFileName = System.getProperty("user.dir")+"/"+Constants.TMP_DIR+"tmp.mfa";
		File tmpFile = new File(tmpFileName); 
		String outFileName = System.getProperty("user.dir")+"/"+Constants.TMP_DIR+"tmp.Xdet";
		File outFile = new File(outFileName);
		try{
			AlignmentExporter.saveFasta(viewProps, tmpFile, false);
		}
		catch(IOException e){
			JOptionPane.showMessageDialog(this, "Temporary file cannot be created.", "File create error", JOptionPane.ERROR_MESSAGE);
		}
		
	
		File methodsDir=new File(Constants.METHODS_DIR);
		String parameters = methodsDir.getAbsolutePath()+"/Maxhom_McLachlan.metric";		
		String command = "";
 		if(method.equals("Xdet")){
			if(System.getProperty("os.name").toLowerCase().indexOf("nix") >=0 || System.getProperty("os.name").toLowerCase().indexOf("linux") >=0){
	 			command = "xdet_linux32";	
	 		}
	 		else if(System.getProperty("os.name").toLowerCase().indexOf("win") >=0){
	 			command = "xdet_linux32";
	 		}
	 		else if(System.getProperty("os.name").toLowerCase().indexOf("osx") >=0){
	 			command = "xdet_osx";
	 		}
			
			try {
				String completeCommand = methodsDir.getAbsolutePath()+"/"+command+" "+tmpFileName+" "+parameters; 
				System.out.println(completeCommand);
				Process proc = Runtime.getRuntime().exec(completeCommand);
				proc.waitFor();
				//Runtime.getRuntime().exec(command+" "+tmpFileName+" "+parameters+" > "+outFileName, new String[0], methodsDir);
				InputStream pStdOut = proc.getInputStream();
				FileOutputStream outWriter = new FileOutputStream(outFile);
				byte[] output = new byte[1024];
				int readLength = 0;
				while((readLength = pStdOut.read(output))>0){
					outWriter.write(output,0,readLength);
				}
				outWriter.close();
				pStdOut.close();
								
				// AlignFrame
				AlignFrame alignFrame = null;
			

				// XDetParser
				XDetParser xdetparser = new XDetParser();
				XDetObject xdetObject = null;
				try {
					xdetObject = xdetparser.read(outFileName);
					methods.setxDetUsed(true);
					methods.setxDetObject(xdetObject);
					methods.setxDetCutoff(new Double(Constants.XDETCUTOFF_DEFAULT));
					methods.setEntropyUsed(true);
					methods.setEntropyCutoff(new Double(Constants.ENTROPYCUTOFF_DEFAULT));
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null,
							"The XDet file couldn't be loaded! \n"
									+ "Please specify a proper format/location of the file.\nDetails:\n    "+(ex.getMessage().indexOf(":")!= -1 ? ex.getMessage().substring(ex.getMessage().indexOf(":")+1): ex.getMessage()),
							"File load error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				alignFrame = new AlignFrame(this.getAlObj(), methods, Constants.DEFAULT_WIDTH, Constants.DEFAULT_HEIGHT);
				alignFrame.pack();
				
				// Close parent if given
				if (this != null) {
					((AlignFrame) this).closeOtherWindows();							
					this.dispose();
				}
				alignFrame.setVisible(true);

			} catch (IOException e) {
	
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 		}
	}
	*/
	
	

	@SuppressWarnings("unchecked") 
	public void selectXDetItems(int mode){
		Vector<Integer> currentSel = (Vector<Integer>) SelectionManager.getCurrentSelection().clone();
		if(mode == Constants.SELECTION_ONLY || mode == Constants.SELECTION_AND){
			SelectionManager.emptyCurrentSelection();
		}
		
    	double xdetThreshold = methods.getCurrentXDetCutoff(); 
    	for(int i=0;i<alObj.getWidth();i++){
    		if(methods.getxDetObject().getPos2ResMap().containsKey(i)){
    			if(methods.getxDetObject().getPos2ResMap().get(i).getCorrelation().doubleValue() > xdetThreshold){
    				if(mode == Constants.SELECTION_AND){
    					if(currentSel.contains(i)){
    						SelectionManager.addToCurrentSelection(i);
    					}
    				}
    				else{
    					SelectionManager.addToCurrentSelection(i);
    				}
    			}
    		}
    	}
    	seqPainter.repaint();
    	seqPainter.notifyObservers();
	}

	@SuppressWarnings("unchecked")
	public void selectEntropyItems(int mode){
		Vector<Integer> currentSel = (Vector<Integer>) SelectionManager.getCurrentSelection().clone();
		if(mode == Constants.SELECTION_ONLY || mode == Constants.SELECTION_AND){
			SelectionManager.emptyCurrentSelection();
		}
    	double entropyThreshold = methods.getCurrentEntropyCutoff();
    	for(int i=0;i<alObj.getWidth();i++){
    		if(methods.getxDetObject().getPos2ResMap().containsKey(i)){
    			if(methods.getxDetObject().getPos2ResMap().get(i).getEntropy().doubleValue() < entropyThreshold){
    				if(mode == Constants.SELECTION_AND){
    					if(currentSel.contains(i)){
    						SelectionManager.addToCurrentSelection(i);
    					}
    				}
    				else{
    					SelectionManager.addToCurrentSelection(i);
    				}
    			}
    		}
    	}
    	seqPainter.repaint();
    	seqPainter.notifyObservers();
	}

	@SuppressWarnings("unchecked")
	public void selectS3DetItems(int mode){
		Vector<Integer> currentSel = (Vector<Integer>) SelectionManager.getCurrentSelection().clone();
		if(mode == Constants.SELECTION_ONLY || mode == Constants.SELECTION_AND){
			SelectionManager.emptyCurrentSelection();
		}
    	double s3Threshold = methods.getCurrentS3DetCutoff(); 
    	for(int i=0;i<alObj.getWidth();i++){
    		if(methods.getS3DetObject().getPos2PredPosition().containsKey(i)){
    			if(methods.getS3DetObject().getPos2PredPosition().get(i).getAverageRank().doubleValue() < s3Threshold){
    				if(mode == Constants.SELECTION_AND){
    					if(currentSel.contains(i)){
    						SelectionManager.addToCurrentSelection(i);
    					}
    				}
    				else{
    					SelectionManager.addToCurrentSelection(i);
    				}
    			}
    		}
    	}
    	seqPainter.repaint();
    	seqPainter.notifyObservers();
	}
	
	@SuppressWarnings("unchecked")
	public void selectUserDefItems(String methodName, int mode){
    	int index=0;
		Vector<Integer> currentSel = (Vector<Integer>) SelectionManager.getCurrentSelection().clone();
		if(mode == Constants.SELECTION_ONLY || mode == Constants.SELECTION_AND){
			SelectionManager.emptyCurrentSelection();
		}
    	UDMObject[] udmObjects = methods.getUdmObjects();
    	
    	for(int i=0; i< udmObjects.length;i++){
    		if(udmObjects[i].getMethodName().equals(methodName)){
    			index=i;
    		}
    	}
		double methodThreshold = methods.getUdmObjects()[index].getCurrentScoreCutoff();
    	for(int i=0;i<alObj.getWidth();i++){
    		if(methods.getUdmObjects()[index].getPos2ResMap().containsKey(i)){
    			if((methods.getUdmObjects()[index].isIncreasingScore() && methods.getUdmObjects()[index].getPos2ResMap().get(i).getScore().doubleValue() >= methodThreshold) ||
    				(!methods.getUdmObjects()[index].isIncreasingScore() && methods.getUdmObjects()[index].getPos2ResMap().get(i).getScore().doubleValue() <= methodThreshold)){
    				if(mode == Constants.SELECTION_AND){
    					if(currentSel.contains(i)){
    						SelectionManager.addToCurrentSelection(i);
    					}
    				}
    				else{
    					SelectionManager.addToCurrentSelection(i);
    				}
    			}
    		}
    	}
    	seqPainter.repaint();
    	seqPainter.notifyObservers();
	}

	
	 /**
     * The method that builds the about dialog.
     */
    private void aboutTriggered() {
        StringBuffer tMsg = new StringBuffer();
        tMsg.append(Constants.APPTITLE);
        tMsg.append("\n");
        tMsg.append("\n");
        tMsg.append("JDet is a multiplatform software for the interactive calculation and \n" +
        		    "visualization of function-related conservation patterns in multiple \n" +
        		    "sequence alignments and structures.");
        tMsg.append("\n");
        tMsg.append("\n");
        tMsg.append("The latest version and info is available at "+Constants.JDET_URL);
        tMsg.append("\n");
        tMsg.append("\n");
        tMsg.append("If any questions arise, contact the corresponding authors: ");
        tMsg.append("\n");
        tMsg.append("Thilo.Muth@uni-jena.de\n");
        tMsg.append("ja.garcia@cnb.csic.es");
        tMsg.append("\n");
        tMsg.append("\n");
        tMsg.append("");
        tMsg.append("");
        JOptionPane.showMessageDialog(this, tMsg,
                "About " + Constants.APPTITLE, JOptionPane.INFORMATION_MESSAGE);
    }

	 /**
     * Close all dependant windows
     */
    public void closeOtherWindows() {
    	if(seqPainter!= null){
    		seqPainter.closeRegisteredWindows("All");
    	}
    }
    
    /**
     * Returns the alignment panel.
     * @return alignPanel
     */
	public AlignPanel getAlignPanel() {
		return alignPanel;
	}
	
	/**
	 * Returns the methods
	 * @return methods Methods
	 */
	public Methods getMethods() {
		return methods;
	}
	
	/**
	 * Returns the alignment object.
	 * @return alObj AlignObject
	 */
	public AlignObject getAlObj() {
		return alObj;
	}
	
	/**
	 * Loads an image from icon directory
	 * @param name Name of the image file 
	 * @return ImageIcon with image loaded 
	 */
	public static ImageIcon getImageIcon(String name) {
		if(jarMode){
			return new ImageIcon(ClassLoader.getSystemResource(name));
		}
		else{
			return new ImageIcon(name);
		}
	}

	public void processDroppedFile(String fileName){
		if (fileName.endsWith(".mfa") || fileName.endsWith("fasta") ||  fileName.endsWith("pir")|| fileName.endsWith("fas") || fileName.endsWith("fa")){
			AlignObject alignObject = null;
			// Alignment Parser
			MfaParser parser = new MfaParser();
			
			try {
				if(fileName.length() > 0){
					alignObject = parser.read(fileName);	
				} else {
					throw new FileNotFoundException();
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null,
						"The alignment file couldn't be loaded! \n"
								+ "Please specify a proper location/format of the file.\nDetails:\n       "+(ex.getMessage().indexOf(":")!= -1 ? ex.getMessage().substring(ex.getMessage().indexOf(":")+1): ex.getMessage()),
						"File load error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Init Methods
			Methods methods = new Methods();
			alignFrame.closeOtherWindows();
			alignFrame = new AlignFrame(alignObject, methods, Constants.DEFAULT_WIDTH, Constants.DEFAULT_HEIGHT, true);
			alignFrame.pack();
			this.dispose();
			alignFrame.setVisible(true);	
			SelectionManager.setEnabled(true);						
			
		}
		else if (fileName.endsWith(".Xdet")){
			if(alObj!= null){
				// XDetParser
				XDetParser xdetparser = new XDetParser();
				XDetObject xdetObject = null;
				try {
					if(fileName.length() > 0){
						xdetObject = xdetparser.read(fileName,alObj);
						methods.setxDetUsed(true);
						methods.setxDetObject(xdetObject);
						methods.setxDetCutoff(Constants.XDETCUTOFF_DEFAULT);
						methods.setEntropyUsed(true);
						methods.setEntropyCutoff(Constants.ENTROPYCUTOFF_DEFAULT);
					}
					
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null,
							"The XDet file couldn't be loaded! \n"
									+ "Please specify a proper format/location of the file.\nDetails:\n    "+(ex.getMessage().indexOf(":")!= -1 ? ex.getMessage().substring(ex.getMessage().indexOf(":")+1): ex.getMessage()),
							"File load error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				// Init Methods
				alignFrame.closeOtherWindows();
				alignFrame = new AlignFrame(alObj, methods, Constants.DEFAULT_WIDTH, Constants.DEFAULT_HEIGHT, false);
				alignFrame.pack();
				this.dispose();
				alignFrame.setVisible(true);	
				SelectionManager.setEnabled(true);
			}
		}
		else if (fileName.endsWith(".S3det")){
			if(alObj!= null){
				// S3DetParser
				S3DetParser s3detparser = new S3DetParser();
				S3DetObject s3DetObject = null;
				try {
					if(fileName.length() > 0){
						s3DetObject = s3detparser.read(fileName, alObj);/*
						if(s3DetObject.getSeqCoords().length != parent.getAlObj().getHeight()){
							throw new Exception("Incorrect number of sequences. Alignment->"+parent.getAlObj().getHeight()+"  S3File->"+s3DetObject.getSeqCoords().length);
						}*/
						methods.setS3DetUsed(true);
						methods.setS3DetObject(s3DetObject);
						methods.setS3DetCutoff(Constants.S3DETCUTOFF_DEFAULT);
					}
				} catch (Exception ex) {			
					JOptionPane.showMessageDialog(null,
							"The S3Det file couldn't be loaded! \n"
									+ "Please specify a proper format/location of the file.\nDetails:\n    "+(ex.getMessage().indexOf(":")!= -1 ? ex.getMessage().substring(ex.getMessage().indexOf(":")+1): ex.getMessage()),
							"File load error", JOptionPane.ERROR_MESSAGE);
					return;
				}	

				
				// Init Methods
				alignFrame.closeOtherWindows();
				alignFrame = new AlignFrame(alObj, methods, Constants.DEFAULT_WIDTH, Constants.DEFAULT_HEIGHT, false);
				alignFrame.pack();
				this.dispose();
				alignFrame.setVisible(true);	
				SelectionManager.setEnabled(true);
			}	
		}		
	}
	
	/**
	 * Main method ==> Entry point to the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
    	PluginLoader.loadPlugins();
		JFrame.setDefaultLookAndFeelDecorated(true);

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin());
				} catch (Exception e) {
					e.printStackTrace();
				}
				new AlignFrame();
			}
		});
	}
	
	
}
