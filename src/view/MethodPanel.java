package view;

import interfaces.Observable;
import interfaces.Observer;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import javax.swing.JPanel;

import model.FuncResidueImpl;
import model.Methods;
import model.PredictedPosition;
import model.ResidueImpl;
import model.S3DetObject;
import model.UDMObject;
import model.XDetObject;
import util.Constants;
import util.ResidueSelectionManager;
import util.SelectionManager;
/**
 * This class represents the panel where the methods are applied.
 * @author Thilo Muth
 *
 */
public class MethodPanel extends JPanel implements Observer{
	
	private static final long serialVersionUID = 1L;	
	private XDetObject xdetObj;
	private S3DetObject s3detObj;
	private UDMObject[] udmObjs;
	private AlignViewProps viewProps;
	private HashMap<Integer, FuncResidueImpl> pos2funcRes;
	private HashMap<Integer, PredictedPosition> pos2predPos;
	private HashMap<Integer, ResidueImpl> pos2res;
	private double xdetThreshold;
	private double entropyThreshold;
	private double s3detThreshold;
	private double scoreThreshold[];
	private Methods methods;
	private int YINDEX_XDET;
	private int YINDEX_ENTROPY;
	private int YINDEX_S3DET;
	private int[] YINDEX_UDM;
	private String firstSequence;
	private SequencePainter seqPainter;
	private int height;
	
	
	public MethodPanel(){
		setLayout(new BorderLayout());
		this.setMinimumSize(new Dimension(Constants.METHODPANEL_WIDTH, Constants.METHODPANEL_HEIGHT));
		this.setPreferredSize(new Dimension(Constants.METHODPANEL_WIDTH, Constants.METHODPANEL_HEIGHT));
		this.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		JPanel panel = new JPanel();
		panel.setBackground(Constants.METHODPANEL_BGCOLOR);
		this.add(panel, BorderLayout.CENTER);
	}
	/**
	 * Constructor of the MethodPanel.
	 * @param viewProps
	 * @param alignPanel
	 */
	public MethodPanel(AlignViewProps viewProps, AlignPanel alignPanel, SequencePainter seqPainter) {		
		this.xdetObj = alignPanel.getAlignFrame().getMethods().getxDetObject();
		this.s3detObj = alignPanel.getAlignFrame().getMethods().getS3DetObject();
		this.udmObjs = alignPanel.getAlignFrame().getMethods().getUdmObjects();
		this.viewProps = viewProps;
		this.methods = alignPanel.getAlignFrame().getMethods();
		this.xdetThreshold = methods.getxDetCutoff();
		methods.setCurrentXDetCutoff(methods.getxDetCutoff());
		this.entropyThreshold = methods.getEntropyCutoff();
		methods.setCurrentEntropyCutoff(methods.getEntropyCutoff());
		this.s3detThreshold = methods.getS3DetCutoff();
		methods.setCurrentS3DetCutoff(methods.getS3DetCutoff());
		
		this.seqPainter = seqPainter;
		this.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {    
				if (SelectionManager.isEnabled()) {
					notifyOnMouseClick(e);					
				}
            }
        });	
		if (udmObjs != null) {
			this.scoreThreshold = methods.getScoreCutoffs();
			for(int i=0;i<methods.getUdmNumber();i++){
				methods.getUdmObjects()[i].setCurrentScoreCutoff(methods.getUdmObjects()[i].getScoreCutoff()); 
			}
		}
		if(xdetObj != null)	this.pos2funcRes = xdetObj.getPos2ResMap();
		if(s3detObj != null) this.pos2predPos = s3detObj.getPos2PredPosition();
		this.firstSequence = viewProps.getAlObj().getSequenceAt(0).getSequenceAsString();
		setupYIndices();
		seqPainter.register(this);
		setLayout(new BorderLayout());
		calculateHeight();
		this.setMinimumSize(new Dimension(Constants.METHODPANEL_WIDTH, Constants.METHODPANEL_HEIGHT));
		this.setPreferredSize(new Dimension(Constants.METHODPANEL_WIDTH, Constants.METHODPANEL_HEIGHT));
		this.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
	}
	
	/**
	 * This method sets up the y indices for generic panel painting for adjusting the
	 * panel height and positioning the ovals in the panel.
	 */
	private void setupYIndices(){
		int yIndex = 2;
		if(xdetObj != null){
			YINDEX_XDET = yIndex;
			yIndex += Constants.METHOD_OFFSET;
			YINDEX_ENTROPY = yIndex;
			yIndex += Constants.METHOD_OFFSET;
		}
		if(s3detObj != null){
			YINDEX_S3DET = yIndex;
			yIndex += Constants.METHOD_OFFSET;
		}		
		if(udmObjs != null){
			YINDEX_UDM = new int[udmObjs.length];
			for (int i = 0; i < udmObjs.length; i++){
				YINDEX_UDM[i] = yIndex;
				yIndex += Constants.METHOD_OFFSET;
			}			
		}
		height = yIndex;
	}
	
	/**
	 * Calculates the height of the method panel.
	 */	
	private void calculateHeight(){
		int height = 20;		
		height += (Constants.METHOD_OFFSET * methods.getMethodNumber());		
		Constants.METHODPANEL_HEIGHT = height;
	}
		
	/**
	 * This updates the panel with the xdet threshold.
	 * @param xdetThreshold
	 */
	public void updatePanelWithXdetThreshold(double xdetThreshold){
		this.xdetThreshold = xdetThreshold;	
		methods.setCurrentXDetCutoff(xdetThreshold);
		this.repaint();
	}
	
	/**
	 * This updates the panel with the entropy threshold.
	 * @param entropyThreshold
	 */
	public void updatePanelWithEntropyThreshold(double entropyThreshold){
		this.entropyThreshold = entropyThreshold;	
		methods.setCurrentEntropyCutoff(entropyThreshold);
		this.repaint();
	}
	
	/**
	 * This updates the panel with the s3det threshold.
	 * @param s3detThreshold
	 */
	public void updatePanelWithS3detThreshold(double s3detThreshold){
		this.s3detThreshold = s3detThreshold;
		methods.setCurrentS3DetCutoff(s3detThreshold);
		this.repaint();
	}
	
	/**
	 * This updates the panel with the user defined score threshold.
	 * @param scoreThreshold
	 */
	public void updatePanelWithScoreThreshold(double scoreThreshold, int index){
		this.scoreThreshold[index] = scoreThreshold;
		methods.getUdmObjects()[index].setCurrentScoreCutoff(scoreThreshold);
		this.repaint();
	}
	
	/**
	 * Overriding the paintComponent-method.
	 */
	@Override
	public void paintComponent(Graphics g) {
		drawSquares(g, viewProps.getXStart(), viewProps.getXEnd(), getWidth(), getHeight());		
		if (SelectionManager.isEnabled()){ // && SelectionManager.isUpperSelectionOn()) {			
			for(int i=0;i< SelectionManager.getCurrentSelection().size();i++){
				highLightPosition(g, SelectionManager.getCurrentSelection().get(i)-1);
			}
			//highLightPosition(g, currentPosition-1);
		}
	}
	
	/**
	 * Draws the index.
	 * @param g
	 * @param startx
	 * @param endx
	 * @param width
	 * @param height
	 */
	public void drawSquares(Graphics g, int startx, int endx, int width, int height) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setFont(viewProps.getFont());

		// Anti aliasing
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Background color
		g2d.setColor(Constants.METHODPANEL_BGCOLOR);		
		g2d.fillRect(0, 0, width, height);
		g2d.setColor(Color.black);
		
		// FontMetrics object
		FontMetrics fm = g2d.getFontMetrics(viewProps.getFont());

		g2d.setColor(Constants.INDEXPANEL_BGCOLOR);
		for(int i=0;i< SelectionManager.getFullSelection().size();i++){
			int pos = SelectionManager.getFullSelection().get(i)-1;
			int xPos = (pos-viewProps.getXStart()) * viewProps.getCharWidth();
			double startX = new Integer(xPos).doubleValue();
			double endX = new Integer(xPos + viewProps.getCharWidth()).doubleValue();			
			double startY = new Integer(0).doubleValue();	
			double endY = height;
			g2d.fill(new Rectangle2D.Double(new Double(startX).intValue(), new Double(startY).intValue(), new Double(endX-startX).intValue(), new Double(endY-startY).intValue()));
		}
		g2d.setColor(Color.BLACK);
		
		char letter;
		int charOffset;
		int x_index = (startx / 10) * 10;
		int y_index = viewProps.getCharHeight() - fm.getDescent();
		int x;
	
		// Iterate along the x axis.
		for (int i = x_index; i < endx; i++) {
			// Check if the map has an entry a each point
			if (pos2funcRes != null) {
				if (pos2funcRes.containsKey(i)) {
					
					// Check if double-value is higher than the correlation threshold
						if (pos2funcRes.get(i).getCorrelation().doubleValue() > xdetThreshold) {
							letter = firstSequence.charAt(i-1);
							x = viewProps.getCharWidth() * (i - startx) - 10;
							charOffset = (viewProps.getCharWidth() - fm
									.charWidth(letter)) / 2;
							// Draw amino acid
							g2d.drawString(String.valueOf(letter), charOffset + x,
									y_index);
							if(SelectionManager.contains(i) || ResidueSelectionManager.contains(i)){
								g2d.setColor(Color.RED);
							}
							else{
								g2d.setColor(Color.GREEN);
							}
							g2d.fillOval(x + 1, y_index + YINDEX_XDET, 9, 11);
							g2d.setColor(Color.BLACK);
							g2d.drawOval(x, y_index + YINDEX_XDET-1, 10, 12);
						}
					
						// Check if double-value is lower than the rank threshold
						if (pos2funcRes.get(i).getEntropy().doubleValue() < entropyThreshold) {
							letter = firstSequence.charAt(i-1);
							x = viewProps.getCharWidth() * (i - startx) - 10;
							charOffset = (viewProps.getCharWidth() - fm.charWidth(letter)) / 2;
							// Draw amino acid
							g2d.drawString(String.valueOf(letter), charOffset + x,
									y_index);
							if(SelectionManager.contains(i) || ResidueSelectionManager.contains(i)){
								g2d.setColor(Color.RED);
							}
							else{
								g2d.setColor(Color.GREEN);
							}
							g2d.fillOval(x + 1, y_index + YINDEX_ENTROPY, 9, 11);
							g2d.setColor(Color.BLACK);
							g2d.drawOval(x, y_index + YINDEX_ENTROPY-1, 10, 12);							
						}
				}			
			}
			
			if (pos2predPos != null){				
				if (pos2predPos.containsKey(i)) {					
					// Check if double-value is lower than the rank threshold
						if (pos2predPos.get(i).getAverageRank().doubleValue() < s3detThreshold) {
							letter = firstSequence.charAt(i-1);
						
							x = viewProps.getCharWidth() * (i - startx) - 10;
							charOffset = (viewProps.getCharWidth() - fm
									.charWidth(letter)) / 2;
							// Draw amino acid
							g2d.drawString(String.valueOf(letter), charOffset + x,
									y_index);
							if(SelectionManager.contains(i) || ResidueSelectionManager.contains(i)){
								g2d.setColor(Color.RED);
							}
							else{
								g2d.setColor(Color.GREEN);
							}
							g2d.fillOval(x + 1, y_index + YINDEX_S3DET, 9, 11);
							g2d.setColor(Color.BLACK);
							g2d.drawOval(x, y_index + YINDEX_S3DET-1, 10, 12);
						}
				}
			}
			if (udmObjs != null){
				for(int k = 0; k < udmObjs.length; k++){
					pos2res = udmObjs[k].getPos2ResMap();
					if (pos2res.containsKey(i)) {					
						// Check if double-value is lower than the rank threshold
							if ((udmObjs[k].isIncreasingScore() && pos2res.get(i).getScore().doubleValue() >= scoreThreshold[k]) ||
								(!udmObjs[k].isIncreasingScore() && pos2res.get(i).getScore().doubleValue() <= scoreThreshold[k])) {
								letter = firstSequence.charAt(i-1);
								x = viewProps.getCharWidth() * (i - startx) - 10;
								charOffset = (viewProps.getCharWidth() - fm
										.charWidth(letter)) / 2;
								// Draw amino acid
								g2d.drawString(String.valueOf(letter), charOffset + x,
										y_index);
								if(SelectionManager.contains(i) || ResidueSelectionManager.contains(i)){
									g2d.setColor(Color.RED);
								}
								else{
									g2d.setColor(Color.GREEN);
								}
								g2d.fillOval(x + 1, y_index + YINDEX_UDM[k], 9, 11);
								g2d.setColor(Color.BLACK);
								g2d.drawOval(x, y_index + YINDEX_UDM[k]-1, 10, 12);
							}
					}
				}
			}
		}
	}
	
	/**
	 * Highlights a position on an observed event.
	 * @param g The Graphics object
	 * @param pos The position value
	 */
	private void highLightPosition(Graphics g, int pos){		
			// Draw vertical lines
			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(Color.BLACK);
			g2d.setStroke(new BasicStroke(2.0f));
			int xPos = (pos-viewProps.getXStart()) * viewProps.getCharWidth();
			//int xPos = pos * viewProps.getCharWidth() - seqPainter.getSumScrollX() * viewProps.getCharWidth();
			double startX = new Integer(xPos).doubleValue();
			double endX = new Integer(xPos + viewProps.getCharWidth()).doubleValue();			
			double startY = new Integer(0).doubleValue();	
			double endY = new Integer(height + viewProps.getCharHeight()).doubleValue()+2;
			// Draw the lines for the surrounding box
			Line2D topLine = new Line2D.Double(startX, startY, endX, 0.0);
			Line2D leftLine = new Line2D.Double(startX, startY, startX, endY);
			Line2D rightLine = new Line2D.Double(endX, startY, endX, endY);
			g2d.draw(topLine);
			g2d.draw(leftLine);
			g2d.draw(rightLine);
	}
	
	/**
	 * Updates the data from the observable object.
	 */
	public void update(Observable object) {
		if (object instanceof SequencePainter) {
			seqPainter = (SequencePainter) object;
			// Update only if methods are loaded.
			//if (SelectionManager.isUpperSelectionOn()){				
				repaint();
			//} 
		}
		
	}
	public void notifyOnMouseClick(MouseEvent ev){
		SelectionManager.toggleSelection(viewProps.getAlObj().getSequenceAt(0).findPosition(seqPainter.getAAPosition(ev)),false);
		seqPainter.notifyOnMouseClick(ev);
		seqPainter.repaint();
	}	

}
