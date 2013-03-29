package view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
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
import model.XDetObject;

import org.jdesktop.swingx.JXButton;

import parser.XDetParser;
import util.Constants;
import util.ScreenConfig;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
/**
 * This the load frame for the XDet method.
 * @author Thilo Muth
 *
 */
public class LoadXDetFrame extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private String filename = null;
	private JTextField xDetTtf;
	private JXButton browseXDetFileBtn;
	private JXButton loadBtn;
	private JXButton closeBtn;
	private AlignFrame parent;
	private LoadXDetFrame frame;
	private Methods methods;
	private AlignObject alignObject;
	private JPanel correlationPnl;
	private JPanel entropyPnl;
	private JLabel entropyCutoffLbl;
	private JLabel corrCutoffLbl;
	private JTextField corrCutoffTtf;
	private JTextField entropyCutoffTtf;

	
	/**
	 * LoadXDetFrame with a given parent frame.
	 * @param parent
	 */
	public LoadXDetFrame(AlignFrame parent) {
		
		// Initialize frame.
		//super("Load XDet");
		super(parent,"Load XDet",true);
		this.parent = parent;		
		methods = parent.getMethods();
		alignObject = parent.getAlObj();
		this.setModal(true);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				cancelTriggered();
			}
		});
		frame = this;
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		// GUI construction
		constructScreen();
	}

	private void constructScreen() {
		JPanel panelNorth = null;
		JPanel panelSouth = null;
		
		frame.setSize(Constants.LOADFRAME_WIDTH, Constants.LOADFRAME_HEIGHT);
		
		Container tContentPane = frame.getContentPane();
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
		
		// XDet Panel
		JPanel xDetPanel = new JPanel(new FormLayout("3dlu, p, 3dlu, p", "p, 3dlu, p, 3dlu p"));
		
		// Titled borders
		xDetPanel.setBorder(BorderFactory.createTitledBorder("XDet File"));
		
		correlationPnl = new JPanel(new FormLayout("p, 5dlu, p", "p"));
		entropyPnl = new JPanel(new FormLayout("p, 5dlu, p", "p"));
		
		corrCutoffLbl = new JLabel("Correlation Cutoff:");
		entropyCutoffLbl = new JLabel("Entropy Cutoff:");
		corrCutoffTtf = new JTextField(3);
		entropyCutoffTtf = new JTextField(3);
		// Set defaults:
		corrCutoffTtf.setText(new Double(Constants.XDETCUTOFF_DEFAULT).toString());
		entropyCutoffTtf.setText(new Double(Constants.ENTROPYCUTOFF_DEFAULT).toString());
		correlationPnl.add(corrCutoffLbl, cc.xy(1, 1));
		correlationPnl.add(corrCutoffTtf, cc.xy(3, 1));
		entropyPnl.add(entropyCutoffLbl, cc.xy(1, 1));
		entropyPnl.add(entropyCutoffTtf, cc.xy(3, 1));
		
		xDetTtf = new JTextField(20);
		xDetTtf.setEditable(false);
		xDetTtf.setEnabled(false);
		xDetPanel.add(correlationPnl, cc.xy(2, 1));
		xDetPanel.add(entropyPnl, cc.xy(2, 3));
		xDetPanel.add(xDetTtf, cc.xy(2, 5));
		
		// Buttons
		browseXDetFileBtn = new JXButton(openFileAction("xdet"));
		xDetPanel.add(browseXDetFileBtn, cc.xy(4, 5));
		
		// Adding load panel
	    JPanel loadPanel = new JPanel(new FormLayout("p", "3dlu, p, 3dlu"));
	    loadPanel.add(xDetPanel, cc.xy(1, 2));
	    
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
						frame.dispose();
						alignFrame.setVisible(true);
					} catch (Exception ex) {
					}
			}
		});
	    closeBtn = new JXButton("Close");
	    closeBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {				
				frame.dispose();
			}
		});
	    
	    actionPanel.add(closeBtn, cc.xy(2, 2));
	    actionPanel.add(loadBtn, cc.xy(4, 2));
	    
	    panelNorth.add(loadPanel, cc.xy(2, 1));
	    panelSouth.add(actionPanel, cc.xy(4, 1));
	    
		tContentPane.add(panelNorth, BorderLayout.NORTH);
		tContentPane.add(panelSouth, BorderLayout.SOUTH);
		frame.setResizable(false);
		frame.pack();
		
		// Center in parent window
		ScreenConfig.centerInComponent(this, parent);
		
		frame.setVisible(true);
	}
	
	private AlignFrame loadAlignFrame() throws Exception{
		
		// AlignFrame
		AlignFrame alignFrame = null;
	

		// XDetParser
		XDetParser xdetparser = new XDetParser();
		XDetObject xdetObject = null;
		try {
			if(xDetTtf.getText().length() > 0){
				xdetObject = xdetparser.read(xDetTtf.getText(), parent.getAlObj());
				methods.setxDetUsed(true);
				methods.setxDetObject(xdetObject);
				methods.setxDetCutoff(new Double(corrCutoffTtf.getText()));
				methods.setEntropyUsed(true);
				methods.setEntropyCutoff(new Double(entropyCutoffTtf.getText()));
			}
			
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null,
					"The XDet file couldn't be loaded! \n"
							+ "Please specify a proper format/location of the file.\nDetails:\n    "+(ex.getMessage().indexOf(":")!= -1 ? ex.getMessage().substring(ex.getMessage().indexOf(":")+1): ex.getMessage()),
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
        	 if (type == "xdet"){
        		filter = new InnerXDetFileFilter();
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
            	if (type == "xdet"){
            		xDetTtf.setText(getSourceName());                
            		xDetTtf.setEnabled(true);   
            		loadBtn.setEnabled(true);
            	} 
            }
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
    
	/**
     * This method is called when the user presses cancel.
     */
    private void cancelTriggered() {       
    	frame.dispose();        
    }   
   
}
