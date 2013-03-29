package view;

import interfaces.Observable;
import interfaces.Observer;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JComponent;

import model.FuncResidueImpl;
import model.S3DetObject;
import model.SequenceImpl;
import util.ColorScheme;
import util.ResidueSelectionManager;
import util.SelectionManager;
import util.SequenceManager;
import util.SequenceOrderer;
import view.Structure3DFrame.JmolPanel;
/**
 * This class does all the sequence painting,
 * implements Observable, as the structure frame listens to the mouse events on the alignment panel.
 * @author Thilo Muth
 *
 */
public class SequencePainter extends JComponent implements Observable, Observer{

	private static final long serialVersionUID = 1L;
	/**
	 * Graphics2D object.
	 */
	private Graphics2D graphics2d;
	
	/**
	 * Graphics object.
	 */
	private Graphics graphics;
	
	/**
	 * Alignment view properties.
	 */
	private AlignViewProps viewProps;
	
	/**
	 * Font Metrics
	 */
	private FontMetrics fontMetrics;
	
	/**
	 * Alignment panel
	 */
	private AlignPanel alignPanel;
	
	/**
	 * Integer to color hash map
	 */
	private HashMap<Integer, Color> colColorMap;
	
	/**
	 * Image width.
	 */
	private int iWidth;
	
	/**
	 * Image height.
	 */
	private int iHeight;
	
	/**
	 * s3det object
	 */
	private S3DetObject s3detobj;
	
	/**
	 * List of observers.
	 */
	private Vector<Observer> observersList;
	
	/**
	 * Current position in the sequence.
	 */
	private int currentPosition;
	
	/**
	 * Selected sequence
	 */
	private SequenceImpl selectedSequence;
	
	/**
	 * Residue position of mouse event.
	 */
	//private int highX;
	
	/**
	 * Sum of the scroll x values.
	 */
	private int sumScrollX;
	

	/**
	 * The JmolPanel.
	 */
	private JmolPanel jmolPanel;
	
	/**
	 * Is export flag.
	 */
	private boolean isExport;
	
	/**
	 * Is interactive flag.
	 */
	private boolean isInteractive = true;
	
	/**
	 * Constructor of the SeqRenderer.
	 * In an optional case it uses the s3detobject to order the sequences and color
	 * @param viewProps
	 * @param alignPanel
	 * @param s3detobj
	 */
	public SequencePainter(AlignViewProps viewProps, AlignPanel alignPanel, S3DetObject s3detobj) {
		this.viewProps = viewProps;
		this.alignPanel = alignPanel;
		this.s3detobj = s3detobj;
		setLayout(new BorderLayout());
		observersList = new Vector<Observer>();
		SelectionManager.setEnabled(true);
		this.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {    
				if (SelectionManager.isEnabled()) {
					toggleResidue(e);
					notifyOnMouseClick(e);
				}
            }
        });
		SelectionManager.setSeqPainter(this);
		ResidueSelectionManager.setSeqPainter(this);
	}
	
	/**
	 * Constructor of the SequencePainter without observers.
	 * Use it for paint the selected sequences
	 * @param viewProps
	 * @param alignPanel
	 * @param s3detobj
	 */
	public SequencePainter(AlignViewProps viewProps, S3DetObject s3detobj) {
		this.viewProps = viewProps;
		this.s3detobj = s3detobj;
		setLayout(new BorderLayout());
		this.isInteractive = false;
	}
	
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);	
		graphics2d = (Graphics2D) g.create();
		graphics2d.setFont(viewProps.getFont());
		
		// Antialiasing
		graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// Fill the upper part
		g.setColor(Color.WHITE);
		int yEnd = viewProps.getYEnd() * viewProps.getCharHeight();
		g.fillRect(0, 0, getWidth(), yEnd);
		
		iWidth = getWidth();
		iHeight = getHeight();

		// Fill the bottom part
		g.setColor(Color.WHITE);
		g.fillRect(0, yEnd, getWidth(), getHeight() - yEnd);		
		
		drawPanel(graphics2d, viewProps.getXStart(), viewProps.getXEnd(), viewProps.getYStart(), viewProps.getYEnd(), 0);	
		if(isInteractive){
			for(int i=0;i< SelectionManager.getCurrentSelection().size();i++){
				highlightResidue(g, SelectionManager.getCurrentSelection().get(i)-1);
			}
			for(int i=0;i< ResidueSelectionManager.getCurrentSelection().size();i++){
				highlightAa(g, ResidueSelectionManager.getCurrentSelection().get(i).getPosition()-1,ResidueSelectionManager.getCurrentSelection().get(i).getAa());
			}
		}
		g.setColor(Color.WHITE);
	}
	
	/**
	 * This methods draws the panel.
	 * @param g1
	 * @param startRes
	 * @param endRes
	 * @param startSeq
	 * @param endSeq
	 * @param offset
	 */
	public void drawPanel(Graphics g1, int startRes, int endRes, int startSeq,	int endSeq, int offset) {
			draw(g1, startRes, endRes, startSeq, endSeq, offset);
	}
	
	/**
	 * This is the final draw method.
	 * @param g
	 * @param startRes
	 * @param endRes
	 * @param startSeq
	 * @param endSeq
	 * @param offset
	 */
	private void draw(Graphics g, int startRes, int endRes, int startSeq, int endSeq, int offset) {
		g.setFont(viewProps.getFont());
		init(g);
		SequenceImpl nextSequence;
		int seqPos = startSeq;
		// No S3Det is given
		if (s3detobj == null){
			if(isInteractive){
				for (int i = startSeq; i < endSeq; i++) {
					nextSequence = viewProps.getAlObj().getSequenceAt(i);
					drawSeq(nextSequence, startRes, endRes, offset + ((i - startSeq) * viewProps.getCharHeight()));
				}
			}
			else{
				for (int i = 0; i < viewProps.getAlObj().getWidth(); i++) {
					if(seqPos>= startSeq && seqPos< endSeq){
						nextSequence = viewProps.getAlObj().getSequenceAt(i);
						if(SequenceManager.isEmpty() || SequenceManager.contains(nextSequence.getName())){						
							drawSelectionSeq(nextSequence, startRes, endRes, offset + ((seqPos- startSeq) * viewProps.getCharHeight()));
							seqPos++;
						}
					}
				}
			}	
		} else {
			// Initialize the sequence orderer
			SequenceOrderer seqOrderer = new SequenceOrderer(viewProps.getAlObj(), s3detobj);
			SequenceImpl[] sequences = seqOrderer.getClusterSequences();
			// Iterate over all the clusterGroups;
			int startBox = 0;
			int endBox = 0;
			if(isInteractive){			
				for (int i = startSeq; i < endSeq; i++) {	
					nextSequence = sequences[i];

					drawSeq(nextSequence, startRes, endRes, offset + ((i - startSeq) * viewProps.getCharHeight()));
					// Retrieve the cluster borders
					if(nextSequence.isClusterStart()){
						startBox = i - startSeq;
						if(isExport){
							drawHorizontal(nextSequence, 0, viewProps.getCharWidth() * nextSequence.getLength(), offset + (startBox * viewProps.getCharHeight()), nextSequence.getClusterColor());
						} else {
							drawHorizontal(nextSequence, 0, (endRes-startRes+1)*viewProps.getCharWidth(), offset + (startBox * viewProps.getCharHeight()), nextSequence.getClusterColor());
						}
						
					}
					if(nextSequence.isClusterEnd()){
						endBox = i+1 - startSeq;
						if(isExport){
							drawHorizontal(nextSequence, 0, (viewProps.getCharWidth() * nextSequence.getLength()) - 1, offset + (endBox * viewProps.getCharHeight() - 2), nextSequence.getClusterColor());
						} else {
							drawHorizontal(nextSequence, 0, (endRes-startRes+1)*viewProps.getCharWidth(), offset + (endBox * viewProps.getCharHeight() - 2), nextSequence.getClusterColor());
						}
					}
					// Draw the vertical box lines	
					if(isExport){
						drawVertical(nextSequence, 0, (startBox * viewProps.getCharHeight() + 1), (endBox * viewProps.getCharHeight()), nextSequence.getClusterColor());
						drawVertical(nextSequence, (viewProps.getCharWidth() * nextSequence.getLength()) - 1, (startBox * viewProps.getCharHeight() + 1), (endBox * viewProps.getCharHeight()), nextSequence.getClusterColor());
					} /*else {
						drawVertical(nextSequence, 0, (startBox * viewProps.getCharHeight() + 1), (endBox * viewProps.getCharHeight()), nextSequence.getClusterColor());
						drawVertical(nextSequence, (endRes-startRes)*viewProps.getCharWidth(), (startBox * viewProps.getCharHeight() + 1), (endBox * viewProps.getCharHeight()), nextSequence.getClusterColor());
					}*/
				}
			}
			else{
				for (int i = 0; i < sequences.length; i++) {
					if(seqPos>= startSeq && seqPos< endSeq){
						nextSequence = sequences[i];
						nextSequence = sequences[i];
						if(SequenceManager.isEmpty() || SequenceManager.contains(nextSequence.getName())){						
							drawSelectionSeq(nextSequence, startRes, endRes, offset + ((seqPos - startSeq) * viewProps.getCharHeight()));
							seqPos++;
						}
					}
				}
			}
			if(isInteractive){
				for(int i=0;i< SelectionManager.getCurrentSelection().size();i++){
					highlightResidue(g, SelectionManager.getCurrentSelection().get(i)-1);
				}
				for(int i=0;i< ResidueSelectionManager.getCurrentSelection().size();i++){
					highlightAa(g, ResidueSelectionManager.getCurrentSelection().get(i).getPosition()-1,ResidueSelectionManager.getCurrentSelection().get(i).getAa());
				}
			}
		}
	}
	
	/**
	 * Initializes the graphics object.
	 * @param g
	 */
	public void init(Graphics g) {
		graphics = g;
		fontMetrics = g.getFontMetrics();

	}
	
	/**
	 * This method colors the columns
	 * @param currentColor
	 * @param xPos
	 * @param yPos
	 */
	public void colorColumn(Color currentColor, int xPos, int yPos) {
	    int width = viewProps.getCharWidth();

	    Color standardColor = Color.WHITE;
	    if (standardColor != currentColor) {
			if (currentColor != null) {
				graphics.setColor(currentColor);
				graphics.fillRect(viewProps.getCharWidth() * (xPos - viewProps.getXStart()), yPos, width, viewProps.getCharHeight());
			}
			width += viewProps.getCharWidth();
		}
		
	}
	
	/**
	 * This methods draws the background.
	 * @param start
	 * @param end
	 * @param yPos
	 */
	public void drawBackground(int start, int end, int yPos){
		Color columnColor;
		int i = start;
		while (i < end){
			// Draw background
			if(colColorMap != null){
				if(colColorMap.containsKey(i)){					
					columnColor = colColorMap.get(i);
					colorColumn(columnColor, i-1, yPos);
				}
			} else {
				break;
			}
			i++;
		}
			
	}
	
	/**
	 * This methods draws an horizontal line (used for the group boxes).
	 * @param xStart
	 * @param xEnd
	 * @param yPos
	 */
	public void drawHorizontal(SequenceImpl seq, int xStart, int xEnd, int yPos, Color linecolor){		
			// Draw horizontal lines
			Graphics2D g2d = (Graphics2D) graphics;
			g2d.setColor(linecolor);
			g2d.setStroke(new BasicStroke(2.0f));			
			
			double startX = new Integer(xStart).doubleValue();
			double endX = new Integer(xEnd).doubleValue();
			double startY = new Integer(yPos).doubleValue();
			double endY = new Integer(yPos).doubleValue();
			
			// Clear the field before drawing
			g2d.clearRect(xStart, yPos, xEnd-xStart, 2);
			
			// Draw the line2d object
			Line2D line2d = new Line2D.Double(startX, startY, endX, endY);
			g2d.draw(line2d);
			
			
	}
	
	/**
	 * This methods draws an vertical line (used for the group boxes).
	 * @param seq
	 * @param xPos
	 * @param yStart
	 * @param yEnd
	 * @param linecolor
	 */
	public void drawVertical(SequenceImpl seq, int xPos, int yStart, int yEnd, Color linecolor){		
			// Draw vertical lines
			Graphics2D g2d = (Graphics2D) graphics;
			g2d.setColor(linecolor);
			g2d.setStroke(new BasicStroke(1.5f));			
			double startX = new Integer(xPos).doubleValue();
			double endX = new Integer(xPos).doubleValue();			
			double startY = new Integer(yStart).doubleValue();			
			double endY = new Integer(yEnd).doubleValue();
			// Draw the line2d object
			Line2D line2d = new Line2D.Double(startX, startY, endX, endY);
			g2d.draw(line2d);
	}
	
	/**
	 * This methods actually draws the sequence. 
	 * @param seq
	 * @param start
	 * @param end
	 * @param yPos
	 */
	public void drawSeq(SequenceImpl seq, int start, int end, int yPos) {
		int yStart = yPos;
		yPos += viewProps.getCharHeight() - viewProps.getCharHeight() / 5;
		int charOffset = 0;
		char letter;		
		if (end + 1 >= seq.getLength()) {
				end = seq.getLength() - 1;
		}
		
		// Iterate over the x-axis
		for (int i = start; i <= end; i++) {
			letter = seq.getLetterAt(i);
			// Draw clustalX color scheme
			if(ColorScheme.containsKey(letter)){
				graphics.setColor(ColorScheme.getColor(letter));
				graphics.fillRect(viewProps.getCharWidth() * (i - viewProps.getXStart()), yStart, viewProps.getCharWidth(), viewProps.getCharHeight());
			}
			
			// Draw letters
			graphics.setColor(Color.BLACK);
			Font font = new Font("Sans Serif", Font.PLAIN, 12);
			graphics.setFont(font);
			
			charOffset = (viewProps.getCharWidth() - fontMetrics.charWidth(letter)) / 2;
			graphics.drawString(String.valueOf(letter), charOffset + viewProps.getCharWidth() * (i - start), yPos);
		} 
		
	}
	
	/**
	 * This methods actually draws the sequence. 
	 * @param seq
	 * @param start
	 * @param end
	 * @param yPos
	 */
	public void drawSelectionSeq(SequenceImpl seq, int start, int end, int yPos) {
		if(SelectionManager.getCurrentSelection().isEmpty() && ResidueSelectionManager.isEmpty()){
			drawSeq(seq, start, end, yPos);
		}
		else{
			int yStart = yPos;
			yPos += viewProps.getCharHeight() - viewProps.getCharHeight() / 5;
			int charOffset = 0;
			char letter;		
			if (end + 1 >= seq.getLength()) {
					end = seq.getLength() - 1;
			}
			
			Vector<Integer> currentSelection = SelectionManager.getFullSelection();
			
			// Iterate over the x-axis
			for (int i = start; i< currentSelection.size() && i<end;i ++) {
				int j = currentSelection.get(i)-1;
				letter = seq.getLetterAt(j);
				// Draw clustalX color scheme
				if(ColorScheme.containsKey(letter)){
					graphics.setColor(ColorScheme.getColor(letter));
					graphics.fillRect(viewProps.getCharWidth() * (i - viewProps.getXStart()), yStart, viewProps.getCharWidth(), viewProps.getCharHeight());
				}
				
				// Draw letters
				graphics.setColor(Color.BLACK);
				Font font = new Font("Sans Serif", Font.PLAIN, 12);
				graphics.setFont(font);
				
				charOffset = (viewProps.getCharWidth() - fontMetrics.charWidth(letter)) / 2;
				graphics.drawString(String.valueOf(letter), charOffset + viewProps.getCharWidth() * (i - start), yPos);
			} 
		}
		
	}
	
	/**
	 * This method does the scroll painting.
	 * @param xValue
	 * @param yValue
	 */
	public void scrollPainting(int xValue, int yValue) {
		graphics2d.copyArea(xValue * viewProps.getCharWidth(), yValue * viewProps.getCharHeight(), iWidth, iHeight,
						-xValue * viewProps.getCharWidth(), -yValue * viewProps.getCharHeight());
		
		int xStart = viewProps.getXStart();
		int xEnd = viewProps.getXEnd();
		int yStart = viewProps.getYStart();
		int yEnd = viewProps.getYEnd();		
		int transX = 0;
		int transY = 0;
		
		if (xValue > 0) {
			xEnd++;
			transX = (xEnd - xStart - xValue) * viewProps.getCharWidth();
			xStart = xEnd - xValue;		
			sumScrollX += xValue;	
		} else if (xValue < 0) {
			xEnd = xStart - xValue - 1;
			sumScrollX += xValue;
		} else if (yValue > 0) {
			yStart = yEnd - yValue;
			if (yStart < viewProps.getYStart()) {
				yStart = viewProps.getYStart();
			} else {
				transY = iHeight - (yValue * viewProps.getCharHeight());
			}
		} else if (yValue < 0) {
			yEnd = yStart - yValue;
			if (yEnd > viewProps.getYEnd()) {
				yEnd = viewProps.getYEnd();
			}
		}
		
		// Translation
		graphics2d.translate(transX, transY);		
		drawPanel(graphics2d, xStart, xEnd, yStart, yEnd, 0);		
		graphics2d.translate(-transX, -transY);		
		repaint();
	}
	
	/**
	 * Notifies the observers on a mouse click.
	 * 
	 */
	public void notifyOnMouseClick(MouseEvent ev){
		repaint();
		notifyObservers();			
	}
	
	public void toggleResidue(MouseEvent ev){
		SequenceImpl selectedSeq;
		if(getSequence(ev) >=0){
			if(s3detobj== null){
				selectedSeq = viewProps.getAlObj().getSequenceAt(getSequence(ev));
			}
			else{
				SequenceOrderer seqOrderer = new SequenceOrderer(viewProps.getAlObj(), s3detobj);
				SequenceImpl[] sequences = seqOrderer.getClusterSequences();
				selectedSeq = sequences[getSequence(ev)];
			}		
			int currentPos = selectedSeq.findPosition(getAAPosition(ev));
			char aa = selectedSeq.getLetterAt(currentPos-1);
			ResidueSelectionManager.toggleSelection(new FuncResidueImpl(currentPos, aa));
		}
	}	

		
		


	
	/**
	 * Highlights a residue for a mouse click.
	 * @param ev
	 */
	private void highlightResidue(Graphics g, int x){
		if (SelectionManager.isEnabled()) {
			// Draw vertical lines
			Graphics2D g2d = (Graphics2D) graphics;
			g2d.setColor(Color.BLACK);
			g2d.setStroke(new BasicStroke(2.0f));
			int xPos = (x-viewProps.getXStart()) * viewProps.getCharWidth();
			double startX = new Integer(xPos).doubleValue();
			double endX = new Integer(xPos + viewProps.getCharWidth()).doubleValue();			
			double startY = new Integer(0).doubleValue();			
			double endY = new Integer(viewProps.getCharHeight() * viewProps.getYEnd()).doubleValue();
			// Draw the lines for the surrounding box
			Line2D leftLine = new Line2D.Double(startX, startY, startX, endY);
			Line2D rightLine = new Line2D.Double(endX, startY, endX, endY);
			Line2D bottomLine = new Line2D.Double(startX, endY, endX, endY);
			g2d.draw(leftLine);
			g2d.draw(rightLine);
			g2d.draw(bottomLine);			
		}		
	}
	
	/**
	 * Highlights an aminoacid on a given position for a mouse click.
	 * @param ev
	 */
	private void highlightAa(Graphics g, int x, char aa){
		Graphics2D g2d = (Graphics2D) graphics;
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(2.0f));

		int xPos = (x-viewProps.getXStart()) * viewProps.getCharWidth();
		double startX = new Integer(xPos).doubleValue();
		if(s3detobj== null){
			SequenceImpl[] sequences = viewProps.getAlObj().getSequences();
			for(int i= 0; i<sequences.length;i++ ){
				if(sequences[i].getLetterAt(x) == aa){
					// Draw a rectangle around the residue				
					double startY = ((i-viewProps.getYStart())*viewProps.getCharHeight());			
					Rectangle2D rectangle = new Rectangle2D.Double(startX, startY, viewProps.getCharWidth(), viewProps.getCharHeight());
					g2d.draw(rectangle);
				}
			}
		}
		else{
			SequenceOrderer seqOrderer = new SequenceOrderer(viewProps.getAlObj(), s3detobj);
			SequenceImpl[] sequences = seqOrderer.getClusterSequences();
			for(int i= 0; i<sequences.length;i++ ){
				if(sequences[i].getLetterAt(x) == aa){
					// Draw a rectangle around the residue				
					double startY = ((i-viewProps.getYStart())*viewProps.getCharHeight());			
					Rectangle2D rectangle = new Rectangle2D.Double(startX, startY, viewProps.getCharWidth(), viewProps.getCharHeight());
					g2d.draw(rectangle);
				}
			}
		}
	}	
	
	/**
	 * Returns the position of a amino acid for a mouse event.
	 * 
	 * @param evt
	 * @return pos Integer
	 */
	public int getAAPosition(MouseEvent evt) {
		int pos = 0;
		int x = evt.getX();
		pos = (x / viewProps.getCharWidth()) + viewProps.getXStart();
		return pos;
	}

	/**
	 * Returns the sequence for a mouse event.
	 * 
	 * @param evt
	 * @return seq Integer
	 */
	public int getSequence(MouseEvent evt) {
		int seq = -1;
		int y = evt.getY();
		if((y / viewProps.getCharHeight()) + viewProps.getYStart() < viewProps.getAlObj().getHeight()){
			seq = (y / viewProps.getCharHeight()) + viewProps.getYStart();
		}
		//seq = Math.min((y / viewProps.getCharHeight()) + viewProps.getYStart(),	viewProps.getAlObj().getHeight() - 1);
		return seq;
	}
	
	/**
	 * Returns the alignment panel.
	 * @return alignPanel AlignPanel
	 */
	public AlignPanel getAlignPanel() {
		return alignPanel;
	}
	
	/**
	 * Notify the observer.
	 */
	public void notifyObservers() {
		// Send notify to all Observers
	    for (int i = 0; i < observersList.size(); i++) {
	      Observer observer = (Observer) observersList.elementAt(i);
	      observer.update(this);
	    }
	}
	
	public void notifyUpperPanels() {
		// Send notify to the first two observers
	    for (int i = 0; i < 2; i++) {
	      Observer observer = (Observer) observersList.elementAt(i);	      
	      observer.update(this);	       
	    }
	}
	
	/**
	 * Registers the observer.
	 */
	public void register(Observer obs) {
		// Add to the list of Observers
	    observersList.addElement(obs);	    
	}
	
	/**
	 * Deregisters the observer.
	 */
	public void deregister(Observer obs) {
		// Remove element from the observer list
	    observersList.removeElement(obs);	    
	}
	
	/**
	 * Returns the current position.
	 * @return currentPosition int
	 */
	public int getCurrentPosition() {
		return currentPosition;
	}
	
	/**
	 * Returns the selected sequence.
	 * @return selectedSequence SequenceImpl
	 */
	public SequenceImpl getSelectedSequence() {
		return selectedSequence;
	}

	/**
	 * Updates the data from the observable object.
	 */
	public void update(Observable object) {
		if (object instanceof JmolPanel) {
				notifyObservers();
	            repaint();
		}
	}
	
	/**
	 * Sets the jMolPanel and registers the seqPainter.
	 * @param jmolPanel
	 */
	public void setJmolPanel(JmolPanel jmolPanel) {
		this.jmolPanel = jmolPanel;
		this.jmolPanel.register(this);
	}
	
	/**
	 * Unsets the seqPainter.
	 */
	public void unsetJmolPanel() {
		this.jmolPanel.deregister(this);
	}
	
	
	public void setExport(boolean isExport) {
		this.isExport = isExport;
	}
	
	public void setSumScrollX(int sumScrollX) {
		this.sumScrollX = sumScrollX;
	}
	
	public int getSumScrollX() {
		return sumScrollX;
	}

	 /**
     * Close all registered windows
     */
    public void closeRegisteredWindows(String type) {
	    for (int i = 0; i < observersList.size(); i++) {
		      Observer observer = (Observer) observersList.elementAt(i);		      
		      if(observer instanceof Structure3DFrame && (type.equals("Structures") || type.equals("All"))){
		    	  SequenceManager.setMarkedSequenceName(null);
		    	   deregister(observer);
		    	  ((Structure3DFrame) observer).dispose();
		    	  i--;
		      }
		      else if (observer instanceof Cluster3DFrame && (type.equals("Spaces") || type.equals("All"))){
		    	  deregister(observer);
		    	  ((Cluster3DFrame) observer).dispose();
		    	  i--;
		      }
		}
    }

	/**
	 * @return the viewProps
	 */
	public AlignViewProps getViewProps() {
		return viewProps;
	}

	/**
	 * @param viewProps the viewProps to set
	 */
	public void setViewProps(AlignViewProps viewProps) {
		this.viewProps = viewProps;
	}
    
    
}
