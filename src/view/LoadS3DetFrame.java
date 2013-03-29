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
import model.S3DetObject;

import org.jdesktop.swingx.JXButton;

import parser.S3DetParser;
import util.Constants;
import util.ScreenConfig;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
/**
 * This the load frame for the S3Det method.
 * @author Thilo Muth
 *
 */
public class LoadS3DetFrame extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private String filename = null;
	private JXButton loadBtn;
	private JXButton closeBtn;
	private AlignFrame parent;
	private LoadS3DetFrame frame;
	private Methods methods;
	private AlignObject alignObject;
	private JTextField s3detTtf;
	private JXButton browseS3DetFileBtn;
	private JPanel avRankPnl;
	private JLabel avRankCutoffLbl;
	private JTextField avRankCutoffTtf;

	
	/**
	 * LoadXDetFrame with a given parent frame.
	 * @param parent
	 */
	public LoadS3DetFrame(AlignFrame parent) {
		
		// Initialize frame.
		//super("Load S3Det");
		super(parent,"Load S3Det",true);
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
		
		// S3Det Panel
		JPanel s3DetPanel = new JPanel(new FormLayout("3dlu, p, 3dlu, p", "p, 3dlu, p"));
	
		// Titled borders
		s3DetPanel.setBorder(BorderFactory.createTitledBorder("S3Det File"));
		// Textfields
		s3detTtf = new JTextField(20);
		s3detTtf.setEditable(false);
		s3detTtf.setEnabled(false);
		avRankPnl = new JPanel(new FormLayout("p, 5dlu, p", "p"));
		avRankCutoffLbl = new JLabel("Average Rank Cutoff:");
		avRankCutoffTtf = new JTextField(3);
		// Set default
		avRankCutoffTtf.setText(new Double(Constants.S3DETCUTOFF_DEFAULT).toString());
		avRankPnl.add(avRankCutoffLbl, cc.xy(1, 1));
		avRankPnl.add(avRankCutoffTtf, cc.xy(3, 1));
		s3DetPanel.add(avRankPnl, cc.xy(2, 1));
		s3DetPanel.add(s3detTtf, cc.xy(2, 3));
		
		// Buttons
		browseS3DetFileBtn = new JXButton(openFileAction("s3det"));
		s3DetPanel.add(browseS3DetFileBtn, cc.xy(4, 3));
		
		// Adding load panel
	    JPanel loadPanel = new JPanel(new FormLayout("p", "3dlu, p, 3dlu"));
	    loadPanel.add(s3DetPanel, cc.xy(1, 2));
	    
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
	
		// S3DetParser
		S3DetParser s3detparser = new S3DetParser();
		S3DetObject s3DetObject = null;
		try {
			if(s3detTtf.getText().length() > 0){
				s3DetObject = s3detparser.read(s3detTtf.getText(), alignObject);/*
				if(s3DetObject.getSeqCoords().length != parent.getAlObj().getHeight()){
					throw new Exception("Incorrect number of sequences. Alignment->"+parent.getAlObj().getHeight()+"  S3File->"+s3DetObject.getSeqCoords().length);
				}*/
				methods.setS3DetUsed(true);
				methods.setS3DetObject(s3DetObject);
				methods.setS3DetCutoff(new Double(avRankCutoffTtf.getText()));
			}
		} catch (Exception ex) {			
			JOptionPane.showMessageDialog(null,
					"The S3Det file couldn't be loaded! \n"
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
        	 if (type == "s3det"){
        		filter = new InnerS3DetFileFilter();
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
            	if (type == "s3det"){
            		s3detTtf.setText(getSourceName());      
            		s3detTtf.setEnabled(true);     
            		loadBtn.setEnabled(true);
            	} 
            }
        }        
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
    
	/**
     * This method is called when the user presses cancel.
     */
    private void cancelTriggered() {       
    	frame.dispose();        
    }
}
