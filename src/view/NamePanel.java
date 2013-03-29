package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

import model.S3DetObject;

import util.Constants;
/**
 * This panel class is mainly uses by the NameRenderer for the proteinID aka. name strings.
 * @author Thilo Muth
 *
 */
public class NamePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;	
	private NamePainter nameRenderer;
	public NamePainter getNameRenderer() {
		return nameRenderer;
	}

	private S3DetObject s3detObj;
	
	/**
	 * Default constructor
	 */
	public NamePanel(){
		setLayout(new BorderLayout());
		this.setMinimumSize(new Dimension(Constants.NAMEPANEL_WIDTH, Constants.NAMEPANEL_HEIGHT));
		this.setPreferredSize(new Dimension(Constants.NAMEPANEL_WIDTH, Constants.NAMEPANEL_HEIGHT));
		this.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		this.add(panel, BorderLayout.CENTER);
	}
	
	/**
	 * Constructor of the NamePanel.
	 * The most important part is adding the name renderer to the scene.
	 * @param viewProps
	 * @param alignPanel
	 */
	public NamePanel(AlignViewProps viewProps, AlignPanel alignPanel, SequencePainter seqPainter){
		s3detObj = alignPanel.getAlignFrame().getMethods().getS3DetObject();
		nameRenderer = new NamePainter(viewProps, alignPanel, s3detObj, seqPainter);
		setLayout(new BorderLayout());
		this.setMinimumSize(new Dimension(Constants.NAMEPANEL_WIDTH, Constants.NAMEPANEL_HEIGHT));
		this.setPreferredSize(new Dimension(Constants.NAMEPANEL_WIDTH, Constants.NAMEPANEL_HEIGHT));
		this.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		this.add(nameRenderer, BorderLayout.CENTER);
	}
	
	/**
	 * Constructor of the NamePanel.
	 * The most important part is adding the name renderer to the scene.
	 * @param viewProps
	 * @param alignPanel
	 */
	public NamePanel(AlignViewProps viewProps, S3DetObject s3detObj){
		this.s3detObj = s3detObj;
		nameRenderer = new NamePainter(viewProps, s3detObj);
		setLayout(new BorderLayout());
		this.setMinimumSize(new Dimension(Constants.NAMEPANEL_WIDTH, Constants.NAMEPANEL_HEIGHT));
		this.setPreferredSize(new Dimension(Constants.NAMEPANEL_WIDTH, Constants.NAMEPANEL_HEIGHT));
		this.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		this.add(nameRenderer, BorderLayout.CENTER);
	}
	
}
