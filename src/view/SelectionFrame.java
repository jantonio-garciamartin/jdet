package view;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import model.S3DetObject;
import util.Constants;
import util.ResidueSelectionManager;
import util.ScreenConfig;
import util.SelectionManager;
import util.SequenceManager;

public class SelectionFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	/**
	 * Alignment view properties.
	 */
	private AlignViewProps viewProps;
	
	/**
	 * Sequence panel
	 */
	private SequencePanel seqPanel;
	
	/**
	 * S3Det object for the cluster groups.
	 */
	private S3DetObject s3detobj;
	
	private IndexPanel indexPanel;
	private NamePanel namePanel;
	private JPanel wholeSequencePnl = new JPanel();
	private JPanel descriptionPnl;
	private JLabel textLabel;
	private JPanel descBoxPanel;
	private JPanel upperPanel;
	private JPanel upperDescPnl;
	private JScrollBar horizontalScrollbar;
	private JScrollBar verticalScrollbar;
	private int horizontalChange = 0;	
    private int verticalChange = 0;    
	

	/**
	 * This class draws a window containing an graphical representation of a selection from the current alignment.
	 * @author Thilo Muth
	 *
	 */
	public SelectionFrame(AlignViewProps viewProps, S3DetObject s3detobj) {
		this.viewProps = viewProps;
		this.viewProps.init();
		this.s3detobj = s3detobj;
		//this.setModal(true);
		init();
		this.pack();
		
		// Center window in screen
		ScreenConfig.centerInScreen(this);
		
		this.validate();
		this.repaint();
		this.setVisible(true);
	}
	/**
	 * Initializes the alignment panel.
	 */
	private void init() {
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
		this.getContentPane().setPreferredSize(new Dimension(Constants.ALIGNPANEL_WIDTH, Constants.ALIGNPANEL_HEIGHT));
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
			upperDescPnl.add(descBoxPanel, BorderLayout.CENTER);
			upperPanel.add(indexPanel, BorderLayout.CENTER);
			descriptionPnl.add(upperDescPnl, BorderLayout.NORTH);
			descriptionPnl.add(namePanel, BorderLayout.CENTER);
			wholeSequencePnl = new JPanel(new BorderLayout());
			wholeSequencePnl.add(upperPanel, BorderLayout.NORTH);
			wholeSequencePnl.add(seqPanel, BorderLayout.CENTER);
			this.getContentPane().add(descriptionPnl);
			this.getContentPane().add(wholeSequencePnl);
		} else {
			seqPanel = new SequencePanel(viewProps, this.s3detobj);
			namePanel = new NamePanel(viewProps, this.s3detobj);
			indexPanel = new IndexPanel(viewProps);
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
			horizontalScrollbar.addAdjustmentListener(new SelectionScrollAdjuster());
			wholeSequencePnl.add(horizontalScrollbar, BorderLayout.SOUTH);
			
			// Add vertical scrollbar
			verticalScrollbar = new JScrollBar(Adjustable.VERTICAL);
			verticalScrollbar.addAdjustmentListener(new SelectionScrollAdjuster());	
			
			this.getContentPane().add(descriptionPnl);
			this.getContentPane().add(wholeSequencePnl);
			this.getContentPane().add(verticalScrollbar, BorderLayout.EAST);
		}		
	}	
	
	public void paint(Graphics g) {
		if(viewProps != null) {
			setScrollValues(viewProps.getXStart(), viewProps.getYStart());
		}
	}
	
	 public void setScrollValues(int x, int y)
	  {

	    int width = viewProps.getAlObj().getWidth();
	    if(!SelectionManager.isEmpty() || !ResidueSelectionManager.isEmpty()){
	    	width = SelectionManager.getFullSelection().size();
	    }
	    int height = viewProps.getAlObj().getHeight();
	    if(!SequenceManager.isEmpty()){
	    	height = SequenceManager.getSelectedSequences().size();
	    }

	    
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
	 
	 public class SelectionScrollAdjuster implements AdjustmentListener {
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
				}
			}
		}
	
}
