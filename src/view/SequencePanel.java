package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

import model.MouseSequenceListener;
import model.S3DetObject;
import util.Constants;

/**
 * This panel class is mainly uses by the SeqRenderer for the protein sequences.
 * @author Thilo Muth
 *
 */
public class SequencePanel extends JPanel{

	private static final long serialVersionUID = 1L;
	private SequencePainter seqPainter;
	private S3DetObject s3detObj;
	
	/**
	 * Default constructor.
	 */
	public SequencePanel(){
		setLayout(new BorderLayout());
		this.setMinimumSize(new Dimension(Constants.SEQPANEL_WIDTH, Constants.SEQPANEL_HEIGHT));
		this.setPreferredSize(new Dimension(Constants.SEQPANEL_WIDTH, Constants.SEQPANEL_HEIGHT));
		this.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		this.add(panel, BorderLayout.CENTER);
	}
	
	/**
	 * Constructor of the SequencePanel.
	 * The most important part is adding the sequence painter to the scene.
	 * @param viewProps
	 * @param alignPanel
	 */
	public SequencePanel(AlignViewProps viewProps, AlignPanel alignPanel) {
		s3detObj = alignPanel.getAlignFrame().getMethods().getS3DetObject();
		seqPainter = new SequencePainter(viewProps, alignPanel, s3detObj);
		seqPainter.addMouseListener(new MouseSequenceListener());		
		setLayout(new BorderLayout());
		this.setMinimumSize(new Dimension(Constants.SEQPANEL_WIDTH, Constants.SEQPANEL_HEIGHT));
		this.setPreferredSize(new Dimension(Constants.SEQPANEL_WIDTH, Constants.SEQPANEL_HEIGHT));
		this.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		this.add(seqPainter, BorderLayout.CENTER);
	}
	
	/**
	 * Constructor of the SequencePanel for showing the current selection.
	 * The most important part is adding the sequence painter to the scene.
	 * @param viewProps
	 * @param alignPanel
	 */
	public SequencePanel(AlignViewProps viewProps, S3DetObject s3detObj ) {
		this.s3detObj = s3detObj;
		seqPainter = new SequencePainter(viewProps, s3detObj);
		setLayout(new BorderLayout());
		this.setMinimumSize(new Dimension(Constants.SEQPANEL_WIDTH, Constants.SEQPANEL_HEIGHT));
		this.setPreferredSize(new Dimension(Constants.SEQPANEL_WIDTH, Constants.SEQPANEL_HEIGHT));
		this.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		this.add(seqPainter, BorderLayout.CENTER);
	}	
	
	/**
	 * Returns the SeqPainter.
	 * @return seqPainter SeqPainter
	 */
	public SequencePainter getSeqPainter(){		
		return seqPainter;
	}
}
