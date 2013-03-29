package view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.AlignObject;
import model.Methods;

import org.jdesktop.swingx.JXButton;

import parser.MfaParser;
import util.Constants;
import util.ScreenConfig;
import util.SelectionManager;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
/**
 * This the main entry point to the application.
 * @author Thilo Muth
 *
 */
public class LoadAlignFrame extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private JTextField multAlignTtf;
	private JXButton browseAlignFileBtn;
	private String filename = null;
	private JXButton loadBtn;
	private JXButton closeBtn;
	private JFrame parent;
	private LoadAlignFrame loadFrame;	
	
	/**
	 * LoadFrame with a given parent frame.
	 * @param parent
	 */
	public LoadAlignFrame(JFrame parent) {
		
		// Initialize frame.
		//super("Load Alignment");
		super(parent,"Load Alignment",true);
		loadFrame = this;
		this.parent = parent;	
		this.setModal(true);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				cancelTriggered();
			}
		});
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		// GUI construction
		constructScreen();
	}

	private void constructScreen() {
		JPanel panelNorth = null;
		JPanel panelSouth = null;
		
		loadFrame.setSize(Constants.LOADFRAME_WIDTH, Constants.LOADFRAME_HEIGHT);
		
		Container tContentPane = loadFrame.getContentPane();
		tContentPane.setLayout(new BorderLayout());

		// Build CellConstraints object
		CellConstraints cc = new CellConstraints();

		/*
		 * Two horizontal main panels
		 */
		panelNorth = new JPanel(new FormLayout("10dlu, p, 5dlu, p, 10dlu",
				"p, 5dlu"));
		panelSouth = new JPanel(new FormLayout(
				"10dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 10dlu", "p, 5dlu"));

		// Multiple Alignment Panel
		JPanel multAlignPanel = new JPanel(new FormLayout("3dlu, p, 3dlu, p", "p"));		
		
		// Titled borders
		multAlignPanel.setBorder(BorderFactory.createTitledBorder("Multiple Alignment"));
		// Textfields
		multAlignTtf = new JTextField(20);
		multAlignTtf.setEditable(false);
		multAlignTtf.setEnabled(false);		
		multAlignPanel.add(multAlignTtf, cc.xy(2, 1));
		
		// Buttons
		browseAlignFileBtn = new JXButton(openFileAction());
		multAlignPanel.add(browseAlignFileBtn, cc.xy(4, 1));
		
		// Adding load panel
	    JPanel loadPanel = new JPanel(new FormLayout("p", "3dlu, p, 3dlu"));
	    loadPanel.add(multAlignPanel, cc.xy(1, 2));
	    
	    // Action panel
	    JPanel actionPanel = new JPanel(new FormLayout("100dlu, p, 5dlu, right:p", "5dlu, p, 5dlu"));
	    
	    loadBtn = new JXButton("Load");
	    loadBtn.setEnabled(false);
	    loadBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				try {
						AlignFrame alignFrame = loadAlignFrame();
						// Close parent if given
						if (parent != null) {													
							((AlignFrame) parent).closeOtherWindows();
							parent.dispose();
						}
						loadFrame.dispose();
						alignFrame.setVisible(true);	
						SelectionManager.setEnabled(true);						
					} catch (Exception ex) {
					}
			}
		});
	    closeBtn = new JXButton("Close");
	    closeBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if(parent == null){
					loadFrame.close();				
				} else {
					loadFrame.dispose();
				}
			}
		});
	    
	    actionPanel.add(closeBtn, cc.xy(2, 2));
	    actionPanel.add(loadBtn, cc.xy(4, 2));
	    
	    panelNorth.add(loadPanel, cc.xy(2, 1));
	    panelSouth.add(actionPanel, cc.xy(4, 1));
	    
		tContentPane.add(panelNorth, BorderLayout.NORTH);
		tContentPane.add(panelSouth, BorderLayout.SOUTH);
		loadFrame.setResizable(false);
		loadFrame.pack();
		
		// Center in the parent window
		ScreenConfig.centerInComponent(this,parent);
		
		loadFrame.setVisible(true);
	}
	
	private AlignFrame loadAlignFrame() throws Exception{
		
		// AlignFrame
		AlignFrame alignFrame = null;
		
		// Alignment Parser
		MfaParser parser = new MfaParser();

		AlignObject alignObject = null;
		try {
			if(multAlignTtf.getText().length() > 0){
				alignObject = parser.read(multAlignTtf.getText());	
			} else {
				throw new FileNotFoundException();
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null,
					"The alignment file couldn't be loaded! \n"
							+ "Please specify a proper location/format of the file.\nDetails:\n       "+(ex.getMessage().indexOf(":")!= -1 ? ex.getMessage().substring(ex.getMessage().indexOf(":")+1): ex.getMessage()),
					"File load error", JOptionPane.ERROR_MESSAGE);
			throw new Exception();
		}

		// Init Methods
		Methods methods = new Methods();

		alignFrame = new AlignFrame(alignObject, methods, Constants.DEFAULT_WIDTH, Constants.DEFAULT_HEIGHT, true);
		alignFrame.pack();
		return alignFrame;
	}

	/**
	 * This method is called when the frame is closed. It shuts down the JVM.
	 */
	private void close() {	
		System.exit(0);
	}
	
	private Action openFileAction() {
		filename = System.getProperty("user.dir") + File.separator;
	    JFileChooser fc = new JFileChooser(new File(filename));
	    FileNameExtensionFilter fnef= new FileNameExtensionFilter("Multiple alignment file (*.mfa,*.pir,*.fasta,*.fas, *.fa)","mfa", "fasta", "pir", "fas","fa"); 
	    fc.setFileFilter(fnef);
	    JDialog loadFrame = new JDialog();
	    loadFrame.setModal(true);
	    Action openAction = new OpenFileAction(loadFrame, fc);	    
	    return openAction;	   
	}

    public class OpenFileAction extends AbstractAction {
    	private static final long serialVersionUID = 1L;
        private JDialog frame;
        private JFileChooser chooser;        
        private File file = null;
        private String name;
        
        OpenFileAction(JDialog frame, JFileChooser chooser){
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
            //chooser.setFileFilter(filter);
            chooser.showOpenDialog(frame);
            // Get the selected file
            file = chooser.getSelectedFile();
            // Only get the path, if a file was selected
            if (file != null) { 
            	setSourceName(file.getAbsolutePath());
	    		multAlignTtf.setText(getSourceName());                
	        	multAlignTtf.setEnabled(true);   
	        	loadBtn.setEnabled(true);
            }
        }        
    }   

    /**
     * This method is called when the user presses cancel.
     */
    private void cancelTriggered() {       
        loadFrame.dispose();        
    }
}
