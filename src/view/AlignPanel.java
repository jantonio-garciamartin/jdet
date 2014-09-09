package view;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import model.S3DetObject;
import model.SequenceImpl;
import util.Constants;
import util.ImageExporter;
import util.SequenceOrderer;
/**
 * Represents the whole alignment panel.
 * @author Thilo Muth
 *
 */
public class AlignPanel extends JPanel implements Printable {
	
	private static final long serialVersionUID = 1L;
	private AlignViewProps viewProps;
	private IndexPanel indexPanel;
	private NamePanel namePanel;
	private SequencePanel seqPanel;
	private AlignFrame alignFrame;
	private JPanel wholeSequencePnl = new JPanel();
	private JScrollBar horizontalScrollbar;
	private JScrollBar verticalScrollbar;
	private JPanel descriptionPnl;
	private JLabel textLabel;
	private JPanel descBoxPanel;
	private JPanel upperPanel;
	private MethodPanel methodPanel;
	private JPanel upperDescPnl;
	private ImageExporter imageExporter;
	private SliderPanel sliderPanel;
	private int horizontalChange = 0;	
    private int verticalChange = 0;    
	private S3DetObject s3detobj;
	
	/**
	 * Default constructor for the alignpanel.
	 * @param alignFrame
	 */
	public AlignPanel(AlignFrame alignFrame){
		this.alignFrame = alignFrame;
		init();
	}
	
	/**
	 * Constructor of the alignment panel.
	 * @param alignFrame
	 * @param viewProps
	 */
	public AlignPanel(AlignFrame alignFrame, final AlignViewProps viewProps) {
		this.alignFrame = alignFrame;
		this.viewProps = viewProps;
		this.s3detobj = alignFrame.getMethods().getS3DetObject();
		init();
		this.repaint();
	}
	
	/**
	 * Initializes the alignment panel.
	 */
	private void init() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setPreferredSize(new Dimension(Constants.ALIGNPANEL_WIDTH, Constants.ALIGNPANEL_HEIGHT));
		textLabel = new JLabel("Protein ID");
		textLabel.setPreferredSize(new Dimension(265, 30));
		textLabel.setMinimumSize(new Dimension(265, 30));
		textLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		descBoxPanel = new JPanel();
		descBoxPanel.setLayout(new BoxLayout(descBoxPanel, BoxLayout.X_AXIS));
		descBoxPanel.setBackground(Constants.DESCBOXPNL_BGCOLOR);
		descBoxPanel.add(Box.createRigidArea(new Dimension(5, 30)));
		descBoxPanel.add(textLabel);
		upperDescPnl = new JPanel(new BorderLayout());		
		upperPanel = new JPanel(new BorderLayout());
		if(viewProps == null){
			indexPanel = new IndexPanel();
			namePanel = new NamePanel();
			seqPanel = new SequencePanel();
			descriptionPnl = new JPanel(new BorderLayout());
			descriptionPnl.setBackground(Color.WHITE);
			methodPanel = new MethodPanel();
			sliderPanel = new SliderPanel();
			upperDescPnl.add(sliderPanel, BorderLayout.NORTH);
			upperPanel.add(methodPanel, BorderLayout.NORTH);
			upperDescPnl.add(descBoxPanel, BorderLayout.CENTER);
			upperPanel.add(indexPanel, BorderLayout.CENTER);
			descriptionPnl.add(upperDescPnl, BorderLayout.NORTH);
			descriptionPnl.add(namePanel, BorderLayout.CENTER);
			wholeSequencePnl = new JPanel(new BorderLayout());
			wholeSequencePnl.add(upperPanel, BorderLayout.NORTH);
			wholeSequencePnl.add(seqPanel, BorderLayout.CENTER);
			this.add(descriptionPnl);
			this.add(wholeSequencePnl);
		} else {
			seqPanel = new SequencePanel(viewProps, this);
			namePanel = new NamePanel(viewProps, this, seqPanel.getSeqPainter());
			indexPanel = new IndexPanel(viewProps, seqPanel.getSeqPainter());
			methodPanel = new MethodPanel(viewProps, this, seqPanel.getSeqPainter());
			sliderPanel = new SliderPanel(alignFrame);
			upperDescPnl.add(sliderPanel, BorderLayout.NORTH);
			upperPanel.add(methodPanel, BorderLayout.NORTH);
			upperPanel.add(indexPanel, BorderLayout.CENTER);
			descriptionPnl = new JPanel(new BorderLayout());
			descriptionPnl.setBackground(Color.WHITE);
			upperDescPnl.add(descBoxPanel, BorderLayout.CENTER);			
			descriptionPnl.add(upperDescPnl, BorderLayout.NORTH);
			descriptionPnl.add(namePanel, BorderLayout.CENTER);
			descriptionPnl.setPreferredSize(new Dimension(250, 450));
			descriptionPnl.setMinimumSize(new Dimension(250, 450));
			descriptionPnl.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
			wholeSequencePnl = new JPanel(new BorderLayout());
			wholeSequencePnl.add(upperPanel, BorderLayout.NORTH);
			wholeSequencePnl.add(seqPanel, BorderLayout.CENTER);
			
			// Add horizontal scrollbar
			horizontalScrollbar = new JScrollBar(Adjustable.HORIZONTAL);
			horizontalScrollbar.addAdjustmentListener(new ScrollAdjuster());
			wholeSequencePnl.add(horizontalScrollbar, BorderLayout.SOUTH);
			
			// Add vertical scrollbar
			verticalScrollbar = new JScrollBar(Adjustable.VERTICAL);
			verticalScrollbar.addAdjustmentListener(new ScrollAdjuster());	
			
			this.add(descriptionPnl);
			this.add(wholeSequencePnl);
			this.add(verticalScrollbar, BorderLayout.EAST);
		}		
	}
	
	public void paintComponent(Graphics g) {
		if(viewProps != null) setScrollValues(viewProps.getXStart(), viewProps.getYStart());
	}
	
	 public void setScrollValues(int x, int y)
	  {

	    int width = viewProps.getAlObj().getWidth();
	    int height = viewProps.getAlObj().getHeight();

	    
	    horizontalChange = seqPanel.getWidth() / viewProps.getCharWidth();
	    verticalChange = seqPanel.getHeight() / viewProps.getCharHeight();

	    if (horizontalChange > width)
	    {
	      horizontalChange = width;
	    }

	    if (verticalChange > height)
	    {
	      verticalChange = height;
	    }

	    if ((horizontalChange + x) > width)
	    {
	      x = width - horizontalChange;
	    }

	    if ((verticalChange + y) > height)
	    {
	      y = height - verticalChange;
	    }

	    if (y < 0)
	    {
	      y = 0;
	    }

	    if (x < 0)
	    {
	      x = 0;
	    }
	    
	    horizontalScrollbar.setValues(x, horizontalChange, 0, width);	    
	    verticalScrollbar.setValues(y, verticalChange, 0, height);
	  }
	 
	public void exportPNGFile(File pngfile){
		int height = ((viewProps.getAlObj().getHeight() + 1) * viewProps.getCharHeight() + indexPanel.getHeight()+methodPanel.getHeight());		
		int width = (viewProps.getAlObj().getWidth() * viewProps.getCharWidth()) + namePanel.getWidth();
		imageExporter = new ImageExporter(this, "Export PNG image from the alignment", width, height, pngfile);
		if (imageExporter.getGraphics() != null){
			try {
				printPanel(imageExporter.getGraphics(), width, height, 0);
			} catch (PrinterException e) {
				e.printStackTrace();
			}
			imageExporter.write();
		} 
	}
	  
	public class ScrollAdjuster implements AdjustmentListener {
		public void adjustmentValueChanged(AdjustmentEvent evt) {

			int oldX = viewProps.getXStart();
			int oldY = viewProps.getYStart();
			
			if (evt.getSource() == verticalScrollbar) {
				int y = verticalScrollbar.getValue(); 
				viewProps.setYStart(y);
				int yQuot = (seqPanel.getSeqPainter().getHeight() / viewProps.getCharHeight());
				if(y+yQuot<=viewProps.getAlObj().getHeight()){
					viewProps.setYEnd(y + yQuot);
				}
				else{
					viewProps.setYEnd(viewProps.getAlObj().getHeight());
				}
			}
		    
			if (evt.getSource() == horizontalScrollbar) {
				int x = horizontalScrollbar.getValue();
				viewProps.setXStart(x);
				viewProps.setXEnd((x + (seqPanel.getSeqPainter().getWidth() / viewProps.getCharWidth())));
			}

			int scrollX = viewProps.getXStart() - oldX;
			int scrollY = viewProps.getYStart() - oldY;

			if (scrollX > viewProps.getXEnd() - viewProps.getXStart()) {
				scrollX = viewProps.getXEnd() - viewProps.getXStart();
			} else if (scrollX < viewProps.getXStart() - viewProps.getXEnd()) {
				scrollX = viewProps.getXStart() - viewProps.getXEnd();
			}
			
			if (scrollY > viewProps.getYEnd() - viewProps.getYStart()) {
				scrollY = viewProps.getYEnd() - viewProps.getYStart();
			} else if (scrollY < viewProps.getYStart() - viewProps.getYEnd()) {
				scrollY = viewProps.getYStart() - viewProps.getYEnd();
			}
			
			if (scrollX != 0 || scrollY != 0) {
				seqPanel.getSeqPainter().scrollPainting(scrollX, scrollY);
				namePanel.getNameRenderer().scrollPainting(scrollX, scrollY);
				indexPanel.repaint();
				methodPanel.repaint();
			}
		}
	}
	
	public MethodPanel getMethodPanel() {
		return methodPanel;
	}
	
	public IndexPanel getIndexPanel() {
		return indexPanel;
	}
	
	public SequencePanel getSeqPanel() {
		return seqPanel;
	}
	
	public NamePanel getNamePanel() {
		return namePanel;
	}
	
	public AlignFrame getAlignFrame() {
		return alignFrame;
	}
	
	/**
	 * Overrides the print Method of Component.
	 */
	public int print(Graphics g, PageFormat format, int p) throws PrinterException {
		g.translate((int) format.getImageableX(), (int) format.getImageableY());
		int w = (int) format.getImageableWidth();
		int h = (int) format.getImageableHeight();		
		return printPanel(g, w, h, p);		
	}
	
	public int printPanel(Graphics graphics, int imageWidth, int imageHeight, int p)
			throws PrinterException {
		this.setScrollValues(0, 0);
		int nameWidth =  namePanel.getWidth();
		FontMetrics fontMetrics = getFontMetrics(viewProps.getFont());
		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, imageWidth, imageHeight);
		graphics.setFont(viewProps.getFont());
		int absHeight = viewProps.getCharHeight() + fontMetrics.getDescent();
		int x = (imageWidth - nameWidth) / viewProps.getCharWidth();
		int y = (int) ((imageHeight - absHeight) / viewProps.getCharHeight()) - 1;
		int wide = (viewProps.getAlObj().getWidth() / x) + 1;
		int startX, endX, startY, endY;
		startX = (p % wide) * x;
		endX = (startX + x) - 1;

		if (endX > (viewProps.getAlObj().getWidth() - 1)) {
			endX = viewProps.getAlObj().getWidth() - 1;
		}

		startY = (p / wide) * y;
		endY = startY + y;

		if (endY > viewProps.getAlObj().getHeight()) {
			endY = viewProps.getAlObj().getHeight();
		}

		int pagesHigh = ((viewProps.getAlObj().getHeight() / y) + 1) * imageHeight;
		
		pagesHigh /= imageHeight;

		if (p >= (wide * pagesHigh)) {
			return NO_SUCH_PAGE;
		}
		
		sliderPanel.drawPanel(graphics, startX, endX, nameWidth, methodPanel.getHeight());
		graphics.translate(nameWidth, 0);
		methodPanel.drawSquares(graphics, startX, endX, imageWidth, methodPanel.getHeight());
		graphics.translate(0, methodPanel.getHeight());
		indexPanel.drawIndex(graphics, startX, endX, imageWidth - nameWidth, absHeight);		
		graphics.translate(-nameWidth, absHeight);

		Color color = null;
		Color currentTextColor = null;
		SequenceImpl sequence;
		if (s3detobj == null){
			for (int i = startY; i < endY; i++) {
				sequence = viewProps.getAlObj().getSequenceAt(i);			
				color = Color.white;
				currentTextColor = Color.black;		
				graphics.setColor(color);
				graphics.fillRect(0, (i - startY) * viewProps.getCharHeight(), nameWidth, viewProps.getCharHeight());
				graphics.setColor(currentTextColor);

				int xValue = 0;
				graphics.drawString(sequence.getName(), xValue,
						(((i - startY) * viewProps.getCharHeight()) + viewProps.getCharHeight())
								- (viewProps.getCharHeight() / 5));
			}
			
			graphics.setFont(viewProps.getFont());
			graphics.translate(nameWidth, 0);
			seqPanel.getSeqPainter().drawPanel(graphics, startX, endX, startY, endY, 0);	
			graphics.translate(-nameWidth, absHeight);
		} else {
			// Initialize the sequence orderer
			SequenceOrderer seqOrderer = new SequenceOrderer(viewProps
					.getAlObj(), s3detobj);
			SequenceImpl[] sequences = seqOrderer.getClusterSequences();

			for (int i = startY; i < endY; i++) {
				sequence = sequences[i];
				namePanel.getNameRenderer().drawName((Graphics2D) graphics,	sequence, 0, ((i - startY) * viewProps.getCharHeight()), sequence.getClusterColor());
			}
			graphics.setFont(viewProps.getFont());
			graphics.translate(nameWidth, 0);
			seqPanel.getSeqPainter().setExport(true);
			seqPanel.getSeqPainter().drawPanel(graphics, startX, endX, startY, endY, 0);
			graphics.translate(-nameWidth, absHeight);
			seqPanel.getSeqPainter().setExport(false);
		}	
		return PAGE_EXISTS;
	}

	/**
	 * @return the horizontalScrollbar
	 */
	public JScrollBar getHorizontalScrollbar() {
		return horizontalScrollbar;
	}

	/**
	 * @param horizontalScrollbar the horizontalScrollbar to set
	 */
	public void setHorizontalScrollbar(JScrollBar horizontalScrollbar) {
		this.horizontalScrollbar = horizontalScrollbar;
	}

	/**
	 * @return the verticalScrollbar
	 */
	public JScrollBar getVerticalScrollbar() {
		return verticalScrollbar;
	}

	/**
	 * @param verticalScrollbar the verticalScrollbar to set
	 */
	public void setVerticalScrollbar(JScrollBar verticalScrollbar) {
		this.verticalScrollbar = verticalScrollbar;
	}	
}
