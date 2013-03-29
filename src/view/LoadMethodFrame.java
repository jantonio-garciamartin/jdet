package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import model.AlignObject;
import model.Methods;
import model.UDMObject;

import org.jdesktop.swingx.JXButton;

import parser.UDMParser;
import util.Constants;
import util.ScreenConfig;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * This is the load frame for a user-defined method.
 * @author Thilo Muth
 *
 */
public class LoadMethodFrame extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private String filename = null;
	private JTextField otherTtf;
	private JXButton browseOtherFileBtn;
	private JXButton loadBtn;
	private JXButton closeBtn;
	private AlignFrame parent;
	private LoadMethodFrame loadMethodFrame;
	private Methods methods;
	private AlignObject alignObject;
	private JPanel scorePnl;
	private JLabel scoreCutoffLbl;
	private JTextField scoreCutoffTtf;
	private JCheckBox increasingScoreCB;

	/**
	 * LoadFrame with a given parent frame.
	 * @param parent
	 */
	public LoadMethodFrame(AlignFrame parent) {
		
		// Initialize frame.
		//super("Add Method");
		super(parent,"Add Method",true);
		loadMethodFrame = this;			
		methods = parent.getMethods();
		alignObject = parent.getAlObj();
		this.setModal(true);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				loadMethodFrame.dispose();
			}
		});
		this.parent = parent;
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		// GUI construction
		constructScreen();
	}
	
	private void constructScreen() {
		JPanel panelNorth = null;
		JPanel panelSouth = null;
		
		// Initialize frame
		loadMethodFrame.setSize(Constants.LOADFRAME_WIDTH, Constants.LOADFRAME_HEIGHT);
		
		// Util.centerInScreen(iFrmMain);
		Container tContentPane = loadMethodFrame.getContentPane();
		tContentPane.setLayout(new BorderLayout());

		// Build CellConstraints object
		CellConstraints cc = new CellConstraints();

		/*
		 * two horizontal main panels
		 */
		panelNorth = new JPanel(new FormLayout("10dlu, p, 5dlu, p, 10dlu",
				"p, 5dlu"));
		panelSouth = new JPanel(new FormLayout(
				"10dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 10dlu", "p, 5dlu"));

		// Descripion Panel
		JPanel descPanel = new JPanel (new FormLayout("5dlu, p, 5dlu", "p, 3dlu, p, 1dlu, p, 5dlu"));
		descPanel.setBorder(BorderFactory.createTitledBorder("Method Format"));


		JLabel descLabel = new JLabel("The method to add has to fulfill the following conditions:");
		JLabel desc2Label = new JLabel("[Position] <SPACE> [Amino Acid Letter] <Space> [Score]");
		JLabel desc3Label = new JLabel("For example:      88    P    2.345  (one line of method file)");
		desc2Label.setForeground(Color.RED);
		desc3Label.setForeground(Color.RED);
		descPanel.add(descLabel, cc.xy(2, 1));
		descPanel.add(desc2Label, cc.xy(2, 3));
		descPanel.add(desc3Label, cc.xy(2, 5));
		
		// Other Panel
		JPanel otherPanel = new JPanel(new FormLayout("3dlu, p, 3dlu, p", "p, 3dlu, p"));
		
		// Titled borders		
		otherPanel.setBorder(BorderFactory.createTitledBorder("User Defined Method File"));
		
		scorePnl = new JPanel(new FormLayout("p, 5dlu, p, 20dlu, p", "p"));
		scoreCutoffLbl = new JLabel("Score Cutoff:");
		scoreCutoffTtf = new JTextField(3);
		// Set default cutoff 
		scoreCutoffTtf.setText(new Double(Constants.OTHERCUTOFF_DEFAULT).toString());
		scorePnl.add(scoreCutoffLbl, cc.xy(1, 1));
		scorePnl.add(scoreCutoffTtf, cc.xy(3, 1));

		// Set default cutoff 
		scoreCutoffTtf.setText(new Double(Constants.OTHERCUTOFF_DEFAULT).toString());
		scorePnl.add(scoreCutoffLbl, cc.xy(1, 1));
		scorePnl.add(scoreCutoffTtf, cc.xy(3, 1));

		// Set cutoff sense 
		increasingScoreCB = new JCheckBox("Increasing score",true);
		scorePnl.add(increasingScoreCB, cc.xy(5, 1));

		// Textfields		
		otherTtf = new JTextField(20);
		otherTtf.setEditable(false);
		otherTtf.setEnabled(false);
		otherPanel.add(scorePnl, cc.xy(2, 1));
		otherPanel.add(otherTtf, cc.xy(2, 3));
		
		
		// Buttons
		browseOtherFileBtn = new JXButton(openFileAction("other"));
		otherPanel.add(browseOtherFileBtn, cc.xy(4, 3));
		
		// Adding load panel
	    JPanel loadPanel = new JPanel(new FormLayout("p", "3dlu, p, 3dlu, p, 3dlu"));
	    loadPanel.add(descPanel, cc.xy(1, 2));
	    loadPanel.add(otherPanel, cc.xy(1, 4));
	    
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
						loadMethodFrame.dispose();
						alignFrame.setVisible(true);
					} catch (Exception ex) {
						ex.printStackTrace();				
						
					}
			}
		});
	    closeBtn = new JXButton("Close");
	    closeBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				loadMethodFrame.dispose();				
			}
		});
	    
	    actionPanel.add(closeBtn, cc.xy(2, 2));
	    actionPanel.add(loadBtn, cc.xy(4, 2));
	    
	    panelNorth.add(loadPanel, cc.xy(2, 1));
	    panelSouth.add(actionPanel, cc.xy(4, 1));
	    
		tContentPane.add(panelNorth, BorderLayout.NORTH);
		tContentPane.add(panelSouth, BorderLayout.SOUTH);
		loadMethodFrame.setResizable(false);
		loadMethodFrame.pack();
		// Center in parent window
		ScreenConfig.centerInComponent(this, parent);
		loadMethodFrame.setVisible(true);
	}
	
	private AlignFrame loadAlignFrame() throws Exception{
		
		// AlignFrame
		AlignFrame alignFrame = null;
		
		// UDMParser
		UDMParser udmparser = new UDMParser();
		UDMObject udmObject = null;
		try {
			if(otherTtf.getText().length() > 0){
				udmObject = udmparser.read(otherTtf.getText());
				udmObject.setScoreCutoff(new Double(scoreCutoffTtf.getText()));
				udmObject.setIncreasingScore(increasingScoreCB.isSelected());
				methods.addUdmObject(udmObject);
			}
		} 
		catch (Exception ex) {	
			JOptionPane.showMessageDialog(null,
					"The user defined method file couldn't be loaded! \n"
							+ "Please specify a proper file format/location.\nDetails:\n    "+(ex.getMessage().indexOf(":")!= -1 ? ex.getMessage().substring(ex.getMessage().indexOf(":")+1): ex.getMessage()),
					"File load error", JOptionPane.ERROR_MESSAGE);
			throw new Exception();
		}
		
		alignFrame = new AlignFrame(alignObject, methods, Constants.DEFAULT_WIDTH, Constants.DEFAULT_HEIGHT, false);
		alignFrame.pack();
		return alignFrame;
	}
	
	private Action openFileAction(String type) {
		filename = System.getProperty("user.dir") + File.separator;
	    JFileChooser fc = new JFileChooser(new File(filename));
	    JDialog loadFrame = new JDialog();
	    loadFrame.setModal(true);
	    Action openAction = new OpenFileAction(loadFrame, fc, type);	    
	    return openAction;	   
	}
	

    public class OpenFileAction extends AbstractAction {
    	private static final long serialVersionUID = 1L;
        private JDialog frame;
        private JFileChooser chooser;        
        private File file = null;
        private String name;
        private String type;
        private FileFilter filter;
        
        OpenFileAction(JDialog frame, JFileChooser chooser, String type) {
            super("Browse...");
            this.chooser = chooser;
            this.type = type;
            this.frame = frame; 
            getFilter();
        }
        
        private void getFilter(){
        	if (type == "other") {
        		filter = new InnerOtherFileFilter();
        	} 
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
            	if (type == "other"){
            		otherTtf.setText(getSourceName());                
            		otherTtf.setEnabled(true);    
            		loadBtn.setEnabled(true);
            	}
            }
        }        
    }   
    
    private static class InnerOtherFileFilter extends FileFilter {
		public boolean accept(File f) {
			if (f.isDirectory()){
				return true;
			}
			if(f.isFile()){
				return true;
			} else {
				return false;
			}
        }

		public String getDescription() {
        	return "User Defined Method File (*.*) ";
        }
    }
}