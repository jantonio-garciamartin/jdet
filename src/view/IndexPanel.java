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
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.Vector;

import javax.swing.JPanel;

import util.Constants;
import util.ResidueSelectionManager;
import util.SelectionManager;

/**
 * This class represents the index of the sequence alignment.
 * The index refers to the position of the amino acid in the protein sequence.
 * @author TMuth 
 *
 */
public class IndexPanel extends JPanel implements Observer{
	
	private static final long serialVersionUID = 1L;
	private AlignViewProps viewProps;
	private SequencePainter seqPainter;	
	private boolean isInteractive = true;
	/**
	 * Default constructor.
	 */
	public IndexPanel(){
		setLayout(new BorderLayout());
		this.setMinimumSize(new Dimension(Constants.INDEXPANEL_WIDTH, Constants.INDEXPANEL_HEIGHT));
		this.setPreferredSize(new Dimension(Constants.INDEXPANEL_WIDTH, Constants.INDEXPANEL_HEIGHT));
		this.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		JPanel panel = new JPanel();
		panel.setBackground(Constants.INDEXPANEL_BGCOLOR);
		this.add(panel, BorderLayout.CENTER);
	}

	/**
	 * Constructor of the IndexPanel.
	 * @param viewProps
	 */
	public IndexPanel(AlignViewProps viewProps, SequencePainter seqPainter) {
		this.viewProps = viewProps;
		setLayout(new BorderLayout());		
		seqPainter.register(this);
		this.seqPainter = seqPainter;
		
		this.setMinimumSize(new Dimension(Constants.INDEXPANEL_WIDTH, Constants.INDEXPANEL_HEIGHT));
		this.setPreferredSize(new Dimension(Constants.INDEXPANEL_WIDTH, Constants.INDEXPANEL_HEIGHT));
		this.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		if(viewProps != null){
			this.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e) {    
					if (SelectionManager.isEnabled()) {
						notifyOnMouseClick(e);					
					}
		        }
		    });
		}
	}
	
	/**
	 * Constructor of the IndexPanel.
	 * @param viewProps
	 */
	public IndexPanel(AlignViewProps viewProps) {
		this.viewProps = viewProps;
		setLayout(new BorderLayout());
		this.isInteractive = false;
		
		this.setMinimumSize(new Dimension(Constants.INDEXPANEL_WIDTH, Constants.INDEXPANEL_HEIGHT));
		this.setPreferredSize(new Dimension(Constants.INDEXPANEL_WIDTH, Constants.INDEXPANEL_HEIGHT));
		this.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
	}
	
	
	
	/**
	 * Overriding the paintComponent-method.
	 */
	@Override
	public void paintComponent(Graphics g) {
		if(isInteractive || (SelectionManager.isEmpty() && ResidueSelectionManager.isEmpty())){
			if(viewProps != null) drawIndex(g, viewProps.getXStart(), viewProps.getXEnd(), getWidth(), getHeight());
			if (isInteractive && SelectionManager.isEnabled()) {			
				for(int i=0;i< SelectionManager.getCurrentSelection().size();i++){
					highLightPosition(g, SelectionManager.getCurrentSelection().get(i)-1);
				}
				markLastPosition(g);				
			}
		}
		else{
			if(viewProps != null) drawSelectedIndex(g, viewProps.getXStart(), viewProps.getXEnd(), getWidth(), getHeight());
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
	public void drawIndex(Graphics g, int startx, int endx, int width, int height) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setFont(viewProps.getFont());

		// Anti aliasing
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Background color
		g2d.setColor(Constants.INDEXPANEL_BGCOLOR);		
		g2d.fillRect(0, 0, width, height);
		g2d.setColor(Color.black);
		
		// FontMetrics object
		FontMetrics fm = g2d.getFontMetrics(viewProps.getFont());
		
		String str_index;		
		int x_index = (startx / 10) * 10;
		int y_index = viewProps.getCharHeight() - fm.getDescent();
		int x_index_max = 0;
		
		if ((x_index % 10) == 0) x_index += 5;		
		
		// Iterate along the x axis with the offset of 5.
		for (int i = x_index; i < endx; i += 5) {
			// Each 10th amino acid draw the number as string.
			if ((i % 10) == 0) {
				str_index = String.valueOf(i);
				if ((i - startx - 1) * viewProps.getCharWidth() > x_index_max) {
					g2d.drawString(str_index, (i - startx - 1) * viewProps.getCharWidth(), y_index);
					x_index_max = (i - startx + 1) * viewProps.getCharWidth() + fm.stringWidth(str_index);
				}

				g2d.drawLine((((i - startx - 1) * viewProps.getCharWidth()) + (viewProps.getCharWidth() / 2)),	y_index + 5,
								(((i - startx - 1) * viewProps.getCharWidth()) + (viewProps.getCharWidth() / 2)),	y_index + (fm.getDescent() * 4));

			} else {
				g2d.drawLine((((i - startx - 1) * viewProps.getCharWidth()) + (viewProps.getCharWidth() / 2)),	(y_index + fm.getDescent()) - 2,
								(((i - startx - 1) * viewProps.getCharWidth()) + (viewProps.getCharWidth() / 2)),	y_index + (fm.getDescent() * 4));
			}
		}
	}
	
	
	/**
	 * Draws the index of selected columns.
	 * @param g
	 * @param startx
	 * @param endx
	 * @param width
	 * @param height
	 */
	public void drawSelectedIndex(Graphics g, int startx, int endx, int width, int height) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setFont(viewProps.getFont());

		// Anti aliasing
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Background color
		g2d.setColor(Constants.INDEXPANEL_BGCOLOR);		
		g2d.fillRect(0, 0, width, height);
		g2d.setColor(Color.black);
		String str_index;		
		
		
		Vector<Integer> currentSelection = SelectionManager.getFullSelection();
		
		for (int i = startx; i< currentSelection.size() && i<endx;i ++) {			
			 str_index = currentSelection.get(i).toString();
	    	 AffineTransform origXform = g2d.getTransform();
	         AffineTransform newXform = (AffineTransform)(origXform.clone());
	         int xRot = 0;
	         int yRot = Constants.INDEXPANEL_HEIGHT;
	         newXform.rotate(Math.toRadians(270), xRot, yRot);
	         g2d.setTransform(newXform);
	         g2d.drawString(str_index, 0, yRot+((1+i-startx)*viewProps.getCharWidth()));	         
	         //g2d.drawString(s, 5, 15);
	    	 g2d.setTransform(origXform);
			
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
			double startX = new Integer(xPos).doubleValue();
			double endX = new Integer(xPos + viewProps.getCharWidth()).doubleValue();			
			double startY = new Integer(0).doubleValue();	
			double endY = new Integer(30).doubleValue();
			// Draw the lines for the surrounding box
	
			Line2D leftLine = new Line2D.Double(startX, startY, startX, endY);
			Line2D rightLine = new Line2D.Double(endX, startY, endX, endY);
		/*	if(!SelectionManager.isUpperSelectionOn()){
				Line2D topLine = new Line2D.Double(startX, startY, endX, 0.0);
				g2d.draw(topLine);
			}*/
			g2d.draw(leftLine);
			g2d.draw(rightLine);
	}

	/**
	 * Checks if this is the last selected position and draws a mark over it if true.
	 * @param g The Graphics object
	 * @param pos The position value
	 */
	private void markLastPosition(Graphics g){		
		if(SelectionManager.getLastSelection() != -1){
			int xPos = (SelectionManager.getLastSelection()-1-viewProps.getXStart()) * viewProps.getCharWidth();

			Point p1 = new Point(xPos +2,20);
			Point p2 = new Point(xPos + viewProps.getCharWidth()-2, 20);
			Point p3 = new Point(xPos +2 +((viewProps.getCharWidth()-4)/2),29);
	
			int[] xs = { p1.x, p2.x, p3.x };
			int[] ys = { p1.y, p2.y, p3.y };
			Polygon triangle = new Polygon(xs, ys, xs.length);
			g.setColor(Color.BLUE);
			g.fillPolygon(triangle);
		}
	}
	
	
	/**
	 * Updates the data from the observable object.
	 */
	public void update(Observable object) {
		if (object instanceof SequencePainter) {
			seqPainter = (SequencePainter) object;
			repaint();			
		}
		
	}
	public void notifyOnMouseClick(MouseEvent ev){
		SelectionManager.toggleSelection(viewProps.getAlObj().getSequenceAt(0).findPosition(seqPainter.getAAPosition(ev)),false);		
		seqPainter.notifyOnMouseClick(ev);
		seqPainter.repaint();
	}
}
