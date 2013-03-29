package util;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import model.ClusterGroup;
import model.S3DetObject;
import model.SequenceImpl;
import view.AlignViewProps;

/**
 * This class draws a sequence logo from the current selection
 * @author Juan Antonio García Martín
 *
 */
public class SequenceLogo extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Vector<Vector<double[]>> frequencyMatrix;
	private Vector<Color> clusterColors;
	private Vector<Integer> currentSelection;
	private JScrollBar horizontalScrollbar;
	private JScrollBar verticalScrollbar;	
	JPanel logoPanel;
	private int horizontalChange = 0;	
	private int verticalChange = 0;
    private JFrame logoFrame;
    private int startX = 0;
    private int endX = 0;
    private int startY = 0;
    private int endY = 1;
    private SequenceOrderer seqOrderer;
    private int sequenceLength = 0;
    private int clusterPanelScale = 1;
    private int logoHeight = Constants.LOGO_HEIGHT+Constants.LOGOHEADER_HEIGHT;
    
    /**
	 * Constructor for a sequence logo from all selected sequences 
	 * @param viewProps Alignment data
	 */
	public SequenceLogo(AlignViewProps viewProps){		
		logoFrame = this;
	    SequenceImpl[] currentSequences;		
		frequencyMatrix = new Vector<Vector<double[]>>();
		clusterColors = new Vector<Color>();
		currentSelection = new Vector<Integer>();
		
		// Store selected sequences
		if(SequenceManager.getSelectedSequences().size() == 0){
			currentSequences = viewProps.getAlObj().getSequences();
		} else{
			currentSequences = new SequenceImpl[SequenceManager.getSelectedSequences().size()];
			for(int i =0; i<SequenceManager.getSelectedSequences().size();i++){
				currentSequences[i] = SequenceManager.getSequence(SequenceManager.getSelectedSequences().get(i),viewProps.getAlObj());
			}			
		}
		
		// Create frequency matrix of selected sequences
		fillFrequencyMatrix(currentSequences);
		
		startX = 0;
		
		// Pick selected positions		
		if(SelectionManager.isEmpty() && ResidueSelectionManager.isEmpty()){
			sequenceLength =  viewProps.getAlObj().getSequenceAt(0).getLength();
			for(int i = 0; i< sequenceLength;i++){
				currentSelection.add(i+1);
			}
		}
		else{
			currentSelection = SelectionManager.getFullSelection();
			sequenceLength = currentSelection.size();			
		}
		
		// Shrink window size to selection if necessary 
		if(sequenceLength> Constants.PANEL_LOGO_WIDTH/Constants.LETTER_WIDTH){
			endX = Constants.PANEL_LOGO_WIDTH/Constants.LETTER_WIDTH;
		}
		else{
			endX = sequenceLength;
		}
		
		//Create sequence logo
		createLogo();
	}

	
    /**
	 * Constructor for two sequence logo from selected and vs no selected sequences   
	 * @param viewProps Alignment data
	 * @param sm SequenceManager  
	 */

	public SequenceLogo(AlignViewProps viewProps, boolean selectedSequence){		
		logoFrame = this;

		//Store alignment and cluster info
		frequencyMatrix = new Vector<Vector<double[]>>();
		clusterColors = new Vector<Color>();

		// Store selected sequences
	    SequenceImpl[] currentSequences;		
		currentSelection = new Vector<Integer>();
		// Store selected sequences
		currentSequences = new SequenceImpl[SequenceManager.getSelectedSequences().size()];
		for(int i =0; i<SequenceManager.getSelectedSequences().size();i++){
			currentSequences[i] = SequenceManager.getSequence(SequenceManager.getSelectedSequences().get(i),viewProps.getAlObj());
		}			

		// Create frequency matrix of selected sequences
		fillFrequencyMatrix(currentSequences);
		clusterColors.add(Constants.NAMEPANEL_SELECTED_BGSEQUENCE);
		
		startX = 0;
		startY = 0;			
		
		// Pick selected positions
		if(SelectionManager.isEmpty() && ResidueSelectionManager.isEmpty()){
			sequenceLength =  viewProps.getAlObj().getSequenceAt(0).getLength();
			for(int i = 0; i< sequenceLength;i++){
				currentSelection.add(i+1);
			}
		}
		else{
			currentSelection = SelectionManager.getFullSelection();
			sequenceLength = currentSelection.size();			
		}
		
		
		// Store unselected sequences
		currentSequences = new SequenceImpl[viewProps.getAlObj().getSequences().length-SequenceManager.getSelectedSequences().size()];
		int insertPos = 0;
		for(SequenceImpl tmpSeq:viewProps.getAlObj().getSequences()){
			if(!SequenceManager.contains(tmpSeq.getName())){
				currentSequences[insertPos] = tmpSeq;
				insertPos++;
			}
		}			
		
		// Create frequency matrix of unselected sequences
		fillFrequencyMatrix(currentSequences);
		clusterColors.add(Color.WHITE);
		
		startX = 0;
		startY = 0;			
		
		// Pick selected positions
		if(SelectionManager.isEmpty() && ResidueSelectionManager.isEmpty()){
			sequenceLength =  viewProps.getAlObj().getSequenceAt(0).getLength();
			for(int i = 0; i< sequenceLength;i++){
				currentSelection.add(i+1);
			}
		}
		else{
			currentSelection = SelectionManager.getFullSelection();
			sequenceLength = currentSelection.size();			
		}
		
		
		// Shrink window size to selection if necessary
		if(sequenceLength> Constants.PANEL_LOGO_WIDTH/Constants.LETTER_WIDTH){
			endX = Constants.PANEL_LOGO_WIDTH/Constants.LETTER_WIDTH;
		}
		else{
			endX = sequenceLength;
		}
		
		clusterPanelScale = 2;

		logoHeight = (Constants.LOGO_HEIGHT/clusterPanelScale)+Constants.LOGOHEADER_HEIGHT;
		
		//Create sequence logo
		createLogo();

	}
	
	
	
    /**
	 * Constructor for a sequence logo from selected sequences of each cluster  
	 * @param viewProps Alignment data
	 * @param s3detobj S3Det method info 
	 */

	public SequenceLogo(AlignViewProps viewProps, S3DetObject s3detobj){		
		logoFrame = this;

		//Store alignment and cluster info
		HashMap<String, Integer> name2ClusterIndex = s3detobj.getName2ClusterIndex();
		seqOrderer = new SequenceOrderer(viewProps.getAlObj(), s3detobj);
		frequencyMatrix = new Vector<Vector<double[]>>();
		clusterColors = new Vector<Color>();
		ClusterGroup[] cl = seqOrderer.getClusterGroups();

		//For each cluster
		for(int c=0; c<cl.length;c++){
			// Store selected sequences
		    SequenceImpl[] currentSequences;		
			currentSelection = new Vector<Integer>();
			if(SequenceManager.getSelectedSequences().size() == 0){
				currentSequences = cl[c].getSequences();
			} else{
				int nSeqCluster = 0;
				for(int i =0; i<SequenceManager.getSelectedSequences().size();i++){
					if((name2ClusterIndex.get(SequenceManager.getSelectedSequences().get(i)))==c){
						nSeqCluster++;
					}
				}
				currentSequences = new SequenceImpl[nSeqCluster];
				int posAdded = 0;
				for(int i =0; i<SequenceManager.getSelectedSequences().size();i++){
					if((name2ClusterIndex.get(SequenceManager.getSelectedSequences().get(i)))==c){
						currentSequences[posAdded] = SequenceManager.getSequence(SequenceManager.getSelectedSequences().get(i),viewProps.getAlObj());
						posAdded++;
					}
				}			
			}			
			// Create frequency matrix of selected sequences
			if(currentSequences != null && currentSequences.length>0){
				fillFrequencyMatrix(currentSequences);
				clusterColors.add(cl[name2ClusterIndex.get(currentSequences[0].getName())].getColor());
			}
			
			startX = 0;
			startY = 0;			
			
			// Pick selected positions
			if(SelectionManager.isEmpty() && ResidueSelectionManager.isEmpty()){
				sequenceLength =  viewProps.getAlObj().getSequenceAt(0).getLength();
				for(int i = 0; i< sequenceLength;i++){
					currentSelection.add(i+1);
				}
			}
			else{
				currentSelection = SelectionManager.getFullSelection();
				sequenceLength = currentSelection.size();			
			}
			
			// Shrink window size to selection if necessary
			if(sequenceLength> Constants.PANEL_LOGO_WIDTH/Constants.LETTER_WIDTH){
				endX = Constants.PANEL_LOGO_WIDTH/Constants.LETTER_WIDTH;
			}
			else{
				endX = sequenceLength;
			}
		}
		
		clusterPanelScale = 1+ (int)Math.floor(Math.log(frequencyMatrix.size()));

		logoHeight = (Constants.LOGO_HEIGHT/clusterPanelScale)+Constants.LOGOHEADER_HEIGHT;
		
		//Create sequence logo
		createLogo();
	}
	
	
	/**
	 * Creates a frequency matrix from given sequences
	 * @param currentSequences
	 */
	private void fillFrequencyMatrix(SequenceImpl[] currentSequences){
		Vector<double[]> newFrecuencyMatrix = new Vector<double[]>();
		//For each position
		for(int i=0; i< currentSequences[0].getLength();i++){
			double[] frequencyElement = new double[Constants.AA_CODES.size()];
			for(int j = 0; j< frequencyElement.length;j++){
				frequencyElement[j]=0;
			}
			for(int j = 0; j< currentSequences.length;j++){
				frequencyElement[Constants.AA_CODES.get(String.valueOf(currentSequences[j].getLetterAt(i)))]++;
			}
			newFrecuencyMatrix.add(frequencyElement);
		}
		frequencyMatrix.add(newFrecuencyMatrix);
	}	
	
	/**
	 * Creates a frame and draws a logo with stored info
	 */
	private void createLogo(){
		/* Print frequency matrix as text 
		StringBuffer output = new StringBuffer();
		
		//Normal mode
		for(int i=0; i< frequencyMatrix.size();i++){
			int totalLetters = 0;
			output.append(i+"\t");
			double[] frequencyElement = frequencyMatrix.get(i);
			for(int j = 0; j< frequencyElement.length;j++){
				output.append(frequencyElement[j]+" ");
				totalLetters+= frequencyElement[j];
			}
			output.append(totalLetters+" \n");
		}
		
		//Transposed matrix
		Set<String> aaCodes = Constants.AA_CODES.keySet();
		Iterator<String> aaIt = aaCodes.iterator();
		while(aaIt.hasNext()){
			String aaCode = aaIt.next();		
			int j= Constants.AA_CODES.get(aaCode);
			output.append(aaCode+" ");
			for(int i = 0; i< frequencyMatrix.size();i++){
				output.append(frequencyMatrix.get(i)[j]+" ");
			}
			output.append("\n");
		}
				
		JOptionPane.showMessageDialog(new JFrame(), output);		
		*/
		
		logoPanel = new JPanel();
		

		//Add scrollbars
		horizontalScrollbar = new JScrollBar(Adjustable.HORIZONTAL);		
		horizontalScrollbar.addAdjustmentListener(new LogoScrollAdjuster());
		
		//Vertical scrollbar is only needed for multiple logos
		if(frequencyMatrix.size()>1){
			verticalScrollbar = new JScrollBar(Adjustable.VERTICAL);		
			verticalScrollbar.addAdjustmentListener(new LogoScrollAdjuster());
		}
		
		//Set window size		
		this.setTitle("Sequence Logo");// = new JFrame("Sequence Logo");
		this.getContentPane().setLayout(new BorderLayout());
		if(verticalScrollbar== null){
			this.setSize(new Dimension(((endX-startX)*Constants.LETTER_WIDTH)+Constants.PANEL_LOGO_MARGIN,Constants.LOGO_HEIGHT+Constants.LOGOHEADER_HEIGHT+Constants.PANEL_LOGO_MARGIN+50));
			//this.setMaximumSize(new Dimension(((frequencyMatrix.get(0).size())*Constants.LETTER_WIDTH)+Constants.PANEL_LOGO_MARGIN,frequencyMatrix.size()*logoHeight+Constants.PANEL_LOGO_MARGIN+55));
		}
		else{
			this.setSize(new Dimension(((endX-startX)*Constants.LETTER_WIDTH)+Constants.PANEL_LOGO_MARGIN+((int)verticalScrollbar.getPreferredSize().getWidth()),Constants.LOGO_HEIGHT+Constants.LOGOHEADER_HEIGHT+Constants.PANEL_LOGO_MARGIN+50));
			//this.setMaximumSize(new Dimension(((frequencyMatrix.get(0).size())*Constants.LETTER_WIDTH)+Constants.PANEL_LOGO_MARGIN+((int)verticalScrollbar.getPreferredSize().getWidth()),frequencyMatrix.size()*logoHeight+Constants.PANEL_LOGO_MARGIN+55));
		}

		// Draw logos
		repaint();		
		
		// Redraw on resize
		this.addComponentListener(new ComponentListener() {
        	public void componentHidden(ComponentEvent e){}
        	public void componentMoved(ComponentEvent e){}
        	public void componentShown(ComponentEvent e){}
        	public void componentResized(ComponentEvent e){
        		repaint();
            }
        });

		// Add scrollbars and logo to window 
		this.getContentPane().add(horizontalScrollbar,BorderLayout.SOUTH);
		if(frequencyMatrix.size()>1){
			this.getContentPane().add(verticalScrollbar,BorderLayout.EAST);
		}
		this.getContentPane().add(logoPanel,BorderLayout.CENTER);

		// Center window in screen
		ScreenConfig.centerInScreen(this);
		
		this.setVisible(true);
		
		
	}
	
	/**
	 * Draws visible portion of logo, based on scroll and window size
	 */
	@Override
	public void paint(Graphics g){
		super.paint(g);
		setScrollValues();
		logoPanel.removeAll();
		
		//logoPanel.setLayout(new GridLayout(endY-startY,1));
		//logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
		logoPanel.setLayout(null);
		logoPanel.setSize(new Dimension(((endX-startX)*Constants.LETTER_WIDTH),(logoHeight)*(endY-startY)));		
//		logoPanel.setPreferredSize(new Dimension(((endX-startX)*Constants.LETTER_WIDTH),(logoHeight)*(endY-startY)));		
		logoPanel.setMaximumSize(new Dimension(((frequencyMatrix.get(0).size())*Constants.LETTER_WIDTH),(logoHeight*frequencyMatrix.size())));		
		for(int j=startY; j< endY; j++){
			Color background = null;
			if(frequencyMatrix.size()>1){
				background = clusterColors.get(j);
			}
			JPanel clusterPanel = new JPanel();
			clusterPanel.setLayout(new BoxLayout(clusterPanel, BoxLayout.X_AXIS));			
			clusterPanel.setSize(new Dimension(((endX-startX)*Constants.LETTER_WIDTH),logoHeight));
			for(int i=startX; i< endX; i++){
				LogoPosition letterLogo = new LogoPosition(frequencyMatrix.get(j).get(currentSelection.get(i)-1), currentSelection.get(i), background);
				letterLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
				clusterPanel.add(letterLogo);
			}
			//logoPanel.setAlignmentY(Component.TOP_ALIGNMENT);
			logoPanel.add(clusterPanel);
			clusterPanel.setBounds(0, logoHeight*(j-startY), ((endX-startX)*Constants.LETTER_WIDTH),logoHeight);
		}
		//logoPanel.validate();
		this.validate();
	}
	
	/**
	 * Calculate scroll values from window size
	 */
	public void setScrollValues(){
	    // Horizontal scrollbar: Alignment width
		int width = currentSelection.size();
	    
		// Fix horizontal steps to position size 
	    horizontalChange = this.getWidth()/Constants.LETTER_WIDTH;

	    if (horizontalChange > width){
	      horizontalChange = width;
	    }

	    if ((horizontalChange + startX) > width){
	      startX = width - horizontalChange;
	    }
	    
	    if (startX < 0){
	    	startX = 0;
	    }
	    
	    horizontalScrollbar.setValues(startX, horizontalChange, 0, width);
	    
	    
	  //Vertical scrollbar is only needed for multiple logos
	    if(frequencyMatrix.size()>1){
		    // Horizontal scrollbar: Alignment width	    	
		    int height = frequencyMatrix.size();
	    	verticalChange = this.getHeight()/logoHeight;
		    if (verticalChange > height){
		    	verticalChange = height;
		    }
		    if ((verticalChange + startY) > height){
			      startY = height - verticalChange;
		    }
		    if (startY < 0){
		    	startY = 0;
		    }
		    verticalScrollbar.setValues(startY, verticalChange, 0, height);
	    }
	    
	  }	
	
	public class LogoScrollAdjuster implements AdjustmentListener {
			public void adjustmentValueChanged(AdjustmentEvent evt) {
				if (evt.getSource() == horizontalScrollbar){					
					startX = horizontalScrollbar.getValue();
					endX = startX + logoFrame.getWidth() / Constants.LETTER_WIDTH;
				}

				if (evt.getSource() == verticalScrollbar){					
					startY = verticalScrollbar.getValue();
					endY = startY + (int) Math.ceil((logoFrame.getHeight()*1.0) / (logoHeight*1.0));
					if (endY > frequencyMatrix.size()){
						endY = frequencyMatrix.size();
					} else if (endY < 0) {
						endY = 0;
					}
				}
				

				if (endX > sequenceLength){
					endX = sequenceLength;
				} else if (endX < 0) {
					endX = 0;
				}
				repaint();
			}
	 }
	
	/**
	 * This class draws a graphic representation of a position in the alignment 
	 * @author Juan Antonio García Martín
	 *
	 */
	public class LogoPosition extends JComponent {
		private static final long serialVersionUID = 1L;
		Image header;		
		Vector<FrequencyImage> fImages; 
		
		
	     int imageHeight = Constants.LOGO_HEIGHT/clusterPanelScale;
	     int imageWidth = Constants.LETTER_WIDTH;
	     int position;
	    
	     
	     public LogoPosition(double[] frequencies, int position, Color background) {
	    	this.position = position;
	    	
			BufferedImage tImg = new BufferedImage(Constants.LOGOHEADER_HEIGHT,Constants.LETTER_WIDTH,BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = (Graphics2D) tImg.getGraphics();
			if(background!= null){
				g2d.setColor(background);
				g2d.fillRect(0, 0, Constants.LOGOHEADER_HEIGHT,Constants.LETTER_WIDTH);
			}
			g2d.setColor(Color.BLACK);
			g2d.setFont(new Font(Constants.LOGOHEADER_FONT,Constants.LOGOHEADER_FONTSTYLE,Constants.LOGOHEADER_FONTSIZE));
			AffineTransform origXform = g2d.getTransform();
			AffineTransform newXform = (AffineTransform)(origXform.clone());
			g2d.drawString(""+position, Constants.LOGOHEADER_MARGIN,Constants.LETTER_WIDTH/2+1);
			
			BufferedImage tImgCont = new BufferedImage(Constants.LETTER_WIDTH,Constants.LOGOHEADER_HEIGHT,BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2dC = (Graphics2D) tImgCont.getGraphics();

			int xRot = 0;
			int yRot = Constants.LOGOHEADER_HEIGHT;
			newXform.translate(xRot, yRot);
			newXform.rotate(Math.toRadians(270));
			g2dC.drawImage(tImg, newXform, this);
			
			this.header = tImgCont;	    	
	    	
	    	Set<String> aaCodes = Constants.AA_CODES.keySet();
	 		Iterator<String> aaIt = aaCodes.iterator();
	    	
	 		Font fittedfont;
	 		if(System.getProperty("os.name").toLowerCase().indexOf("nix") >=0 || System.getProperty("os.name").toLowerCase().indexOf("linux") >=0){
	 			fittedfont = new Font("monospaced",Font.BOLD,42);	
	 		}
	 		else {
	 			fittedfont = new Font("monospaced",Font.BOLD,50);
	 		}	 		
	 		
	    	fImages = new Vector<FrequencyImage>();
	 		int i =0;
	 		int nSequences = 0;
	 		for(int j=0;j<frequencies.length;j++){
	 			nSequences+= frequencies[j];
	 		}
			double scale = (imageHeight*1.0)/(nSequences*1.0);
	 		while(aaIt.hasNext()){
	 			String aaCode = aaIt.next();		
	 			int j= Constants.AA_CODES.get(aaCode);	 			
 				if(frequencies[j] > 0){
 					int height = (int) Math.round(frequencies[j]*scale);
 					BufferedImage bImg = new BufferedImage(Constants.LETTER_WIDTH,Constants.LETTER_WIDTH,BufferedImage.TYPE_INT_ARGB); 					
 					Graphics2D g = (Graphics2D) bImg.getGraphics();
					g.setColor(ColorScheme.getColor(aaCode.charAt(0)));
 					g.setBackground(Color.WHITE);
 					g.setFont(fittedfont); 					
 					if(!aaCode.equals("-")){
 						g.drawString(aaCode, 0, Constants.LETTER_WIDTH);
 					} 					
 					fImages.add(new FrequencyImage(bImg.getScaledInstance(imageWidth, height, Image.SCALE_FAST),frequencies[j]));
 	 				i++; 		    		
 				}
	 		}
	 		Collections.sort(fImages);
	     }
	     
	     public Font calculateFittingFont() {
	    	Font testFont;
	    	FontMetrics fm;
			BufferedImage bImg = new BufferedImage(Constants.LETTER_WIDTH,Constants.LETTER_WIDTH,BufferedImage.TYPE_INT_ARGB);
			Graphics g = bImg.getGraphics();
	    	int i=0;
			do{
				i++;
				testFont = new Font("monospaced",Font.BOLD,20+i); 
				g.setFont(testFont);
				fm = g.getFontMetrics(testFont);				
			}while(fm.getAscent()<Constants.LETTER_WIDTH);
				
			testFont = new Font("monospaced",Font.BOLD,Constants.LETTER_WIDTH+i-1);
			return testFont;
	     }
	     
	     public void paint(Graphics g) {
	    	 g.drawImage(header, 0, 0, this);	    	 

	    	 int yPos = Constants.LOGOHEADER_HEIGHT;
	    	 for(int i =0;i<fImages.size();i++){
	    		 g.drawImage(fImages.get(i).getImage(), 0,yPos, this);
	    		 yPos += fImages.get(i).getImage().getHeight(this);
	    	 }
	     }
	}
	
	/**
	 * This class contains a graphic representation of a residue the alignment and its frequency 
	 * @author Juan Antonio García Martín
	 */
	public class FrequencyImage implements Comparable<FrequencyImage> {
		Image image;
		double frequency;
		public FrequencyImage(Image image,double frequency){
			this.image= image;
			this.frequency=frequency;	
		}
		public double getFrequency(){
			return this.frequency;
		}
		public Image getImage(){
			return this.image;
		}
		
		public int compareTo(FrequencyImage b){
			return (new Double(this.frequency-b.getFrequency())).intValue();
		}
	}
}
