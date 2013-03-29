package view;

import interfaces.Observable;
import interfaces.Observer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import model.S3DetObject;
import model.SequenceImpl;
import util.Constants;
import util.ResidueSelectionManager;
import util.SelectionManager;
import util.SequenceManager;
import util.SequenceOrderer;
/**
 * Represents the painter class for the protein names.
 * @author Thilo Muth
 *
 */
public class NamePainter extends JComponent implements Observer{
	
	private static final long serialVersionUID = 1L;
	/**
	 * Alignment view properties.
	 */
	private AlignViewProps viewProps;
	
	/**
	 * Align panel
	 */
//	private AlignPanel alignPanel;
	
	/**
	 * Graphics2D object.
	 */
	private Graphics2D graphics2d;
	
	/**
	 * S3Det object for the cluster groups.
	 */
	private S3DetObject s3detobj;
	
	/**
	 * SequencePanel for registering and update.
	 */
	private SequencePainter seqPainter;	
	
	/**
	 * Image width.
	 */
	private int iWidth;
	
	/**
	 * Image height
	 */
	private int iHeight;

	/**
	 * Is interactive flag.
	 */
	private boolean isInteractive = true;
	
	/**
	 * Marks selection start for multiple sequence selections.
	 */
	private int startSelection = -1;
	
	

	/**
	 * Constructor of the namePainter class.
	 * @param viewProps
	 * @param alignPanel
	 * @param s3detobj
	 */
	public NamePainter(AlignViewProps viewProps, AlignPanel alignPanel, S3DetObject s3detobj, SequencePainter seqPainter) {
		this.viewProps = viewProps;
		this.s3detobj = s3detobj;
//		this.alignPanel = alignPanel;
		setLayout(new BorderLayout());
		SequenceManager.setS3detobj(s3detobj);
		SelectionManager.setS3detobj(s3detobj);
		ResidueSelectionManager.setS3detobj(s3detobj);
		this.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {    
				if (SelectionManager.isEnabled()) {
					sequenceClicked(e);					
				}
            }
			public void mousePressed(MouseEvent e) {
				sequencePressed(e);					
			}

			public void mouseReleased(MouseEvent e) {
				sequenceReleased(e);
			}			
        });
		this.seqPainter = seqPainter; 
		seqPainter.register(this);
		this.repaint();
	}	

	/**
	 * Constructor of the namePainter class without register, used for paint window without interactions.
	 * @param viewProps
	 * @param alignPanel
	 * @param s3detobj
	 */
	public NamePainter(AlignViewProps viewProps, S3DetObject s3detobj) {
		this.viewProps = viewProps;
		this.s3detobj = s3detobj;
		setLayout(new BorderLayout());
		isInteractive = false;
		this.repaint();
	}	
	
	
	/**
	 * paintComponent is overwritten.
	 * @param g
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		graphics2d = (Graphics2D) g.create();
		iWidth = getWidth();
		iHeight = getHeight();
		drawComponent(graphics2d, viewProps.getXStart(), viewProps.getXEnd(), viewProps.getYStart(), viewProps.getYEnd());
	}
	
	/**
	 * Draw component method.
	 * @param g
	 * @param startRes
	 * @param endRes
	 * @param yStart
	 * @param yEnd
	 */
	public void drawComponent(Graphics2D g, int startRes, int endRes, int yStart, int yEnd) {		
		// Fill the upper part
		g.setColor(Color.WHITE);
		//int yEnd = yEnd;
		g.fillRect(0, 0, getWidth(), yEnd);
		
		// Fill the bottom part
		g.setColor(Color.WHITE);
		g.fillRect(0, yEnd, getWidth(), getHeight() - yEnd);
		
		SequenceImpl nextSequence;
		
		// Font of the ProteinID names
		Font font = new Font("Verdana", Font.BOLD, 12);
		g.setFont(font);
		
		int start = yStart;
		int end = yEnd;
		// If no s3det object given, do normal painting!
		int seqPos = start;
		if (s3detobj == null){
			if(isInteractive){
				for (int i = start; i < end; i++) {
					// Set background
					nextSequence = viewProps.getAlObj().getSequenceAt(i);

					if(nextSequence.getName().equals(SequenceManager.getMarkedSequenceName())){
						drawBlackName(g, nextSequence, ((i - start) * viewProps.getCharHeight()));
					} else if(SequenceManager.isSelected(nextSequence.getName())){
						drawSelectedName(g, nextSequence, ((i - start) * viewProps.getCharHeight()));
					} else {
						drawName(g, nextSequence, 0, ((i - start) * viewProps.getCharHeight()), Color.WHITE);
					}
				}
			}
			else{
				for (int i = 0; i < viewProps.getAlObj().getHeight(); i++) {
					if(seqPos>= start && seqPos< end){
						// Set background
						nextSequence = viewProps.getAlObj().getSequenceAt(i);
					
						if(SequenceManager.isEmpty() || SequenceManager.contains(nextSequence.getName())){
							drawName(g, nextSequence, 0, ((seqPos - start) * viewProps.getCharHeight()), Color.WHITE);
							seqPos++;
						}
					}
				}
			}
		} else {
			// Initialize the sequence orderer
			SequenceOrderer seqOrderer = new SequenceOrderer(viewProps.getAlObj(), s3detobj);
			SequenceImpl[] sequences = seqOrderer.getClusterSequences();
			if(isInteractive){
				for (int i = start; i < end; i++) {
					nextSequence = sequences[i];
					if(nextSequence.getName().equals(SequenceManager.getMarkedSequenceName())){
						drawBlackName(g, nextSequence, ((i - start) * viewProps.getCharHeight()));
					} else if(SequenceManager.isSelected(nextSequence.getName())){
						drawSelectedName(g, nextSequence, ((i - start) * viewProps.getCharHeight()));
					} else {
						drawName(g, nextSequence, 0, ((i - start) * viewProps.getCharHeight()), nextSequence.getClusterColor());
					}
				}
			}
			else{
				for (int i = 0; i < sequences.length; i++) {
					if(seqPos>= start && seqPos< end){
						nextSequence = sequences[i];						
						if(SequenceManager.isEmpty() || SequenceManager.contains(nextSequence.getName())){
							drawName(g, nextSequence, 0, ((seqPos - start) * viewProps.getCharHeight()), nextSequence.getClusterColor());
							seqPos++;
						}
					}
				}
			}
		}
	}
	
	/**
	 * Draws the name.
	 * @param g
	 * @param seq
	 * @param start
	 * @param y1
	 * @param box 
	 */
	public void drawName(Graphics2D g, SequenceImpl seq, int start, int y1, Color box) {
		y1 += viewProps.getCharHeight() - viewProps.getCharHeight() / 5;
		String name;	
		
		// Fill the background
		g.setColor(box);
		g.fillRect(start, (y1 - viewProps.getCharHeight()) + 2, getWidth(), viewProps.getCharHeight() + 2);
		
		// Text color
		g.setColor(Color.BLACK);
		
		name = seq.getName();
		// xBorder set to 5!
		g.drawString(name, 5, y1);
	}
	
	/**
	 * Draws the name marked as black.
	 * @param seq
	 * @param y1
	 */
	public void drawBlackName(Graphics2D g, SequenceImpl seq, int y1) {
		y1 += viewProps.getCharHeight() - viewProps.getCharHeight() / 5;
		String name;	
		
		// Fill the background
		g.setColor(Color.BLACK);
		g.fillRect(0, (y1 - viewProps.getCharHeight()) + 2, getWidth(), viewProps.getCharHeight() + 2);
		
		// Text color
		g.setColor(Color.WHITE);
		
		name = seq.getName();
		// xBorder set to 5!
		g.drawString(name, 5, y1);
	}

	/**
	 * Draws the name marked as selected.
	 * @param seq
	 * @param y1
	 */
	public void drawSelectedName(Graphics2D g, SequenceImpl seq, int y1) {
		y1 += viewProps.getCharHeight() - viewProps.getCharHeight() / 5;
		String name;	
		
		// Fill the background
		g.setColor(Constants.NAMEPANEL_SELECTED_BGSEQUENCE);
		g.fillRect(0, (y1 - viewProps.getCharHeight()) + 2, getWidth(), viewProps.getCharHeight() + 2);
		
		// Text color
		g.setColor(Constants.NAMEPANEL_SELECTED_FGSEQUENCE);
		
		name = seq.getName();
		// xBorder set to 5!
		g.drawString(name, 5, y1);
	}
	
	/**
	 * This methods simplifies the drawing of the sequence.
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
		} else if (xValue < 0) {
			xEnd = xStart - xValue - 1;
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
		
		graphics2d.translate(transX, transY);
		drawComponent(graphics2d, xStart, xEnd, yStart, yEnd);
		graphics2d.translate(-transX, -transY);
		repaint();
	}
	
	public void sequenceClicked(MouseEvent ev){
/*		int pos = viewProps.getYStart() + (ev.getY() / viewProps.getCharHeight());		
		if(pos >= 0 && pos < viewProps.getAlObj().getHeight()) {
			if(this.s3detobj == null){
				String seqName = viewProps.getAlObj().getSequenceAt(pos).getName();
				SequenceManager.toggleSequence(seqName);
			}
			else{
				SequenceOrderer seqOrderer = new SequenceOrderer(viewProps.getAlObj(), s3detobj);
				SequenceImpl[] sequences = seqOrderer.getClusterSequences();
				String seqName =sequences[pos].getName();
				SequenceManager.toggleSequence(seqName);
			}
			seqPainter.notifyObservers();
		}*/
	}
	
	public void sequencePressed(MouseEvent ev){
		int pos = viewProps.getYStart() + (ev.getY() / viewProps.getCharHeight());		
		if(pos < 0) {
			pos = 0;
		}
		if(pos > viewProps.getAlObj().getHeight()-1) {
			pos = viewProps.getAlObj().getHeight()-1;		
		}
		startSelection = pos;
	}
	
	public void sequenceReleased(MouseEvent ev){
		if (startSelection!= -1){
			int pos = viewProps.getYStart() + (ev.getY() / viewProps.getCharHeight());
			if(pos < 0) {
				pos = 0;
			}
			if(pos > viewProps.getAlObj().getHeight()-1) {
				pos = viewProps.getAlObj().getHeight()-1;		
			}
	
			int start = startSelection < pos? startSelection: pos;
			int end = startSelection > pos? startSelection: pos;
	
			if(this.s3detobj == null){
				for(int i = start; i<= end; i++){
					String seqName = viewProps.getAlObj().getSequenceAt(i).getName();
					SequenceManager.toggleSequence(seqName);
				}
			}
			else{
				SequenceOrderer seqOrderer = new SequenceOrderer(viewProps.getAlObj(), s3detobj);
				SequenceImpl[] sequences = seqOrderer.getClusterSequences();
				for(int i = start; i<= end; i++){
					String seqName = sequences[i].getName();
					SequenceManager.toggleSequence(seqName);
				}
			} 
			seqPainter.notifyObservers();
			startSelection = -1;
		}	
	}

	
	/**
	 * Updates the data from the observable object.
	 */
	public void update(Observable object) {
		if (object instanceof SequencePainter) {
			repaint();
		}
		
	}

}
