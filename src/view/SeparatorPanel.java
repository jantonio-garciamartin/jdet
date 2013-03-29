package view;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * Used to separate the sections of the StatusPanel.
 * @author Thilo Muth
 *
 */
public class SeparatorPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Color leftColor;
	private Color rightColor;
	public SeparatorPanel(Color leftColor, Color rightColor) {		
		this.leftColor = leftColor;
		this.rightColor = rightColor;
		setOpaque(false);
	}
	
	protected void paintComponent(Graphics g){
		g.setColor(leftColor);
		g.drawLine(0, 0, 0, getHeight());
		g.setColor(rightColor);
		g.drawLine(1, 0, 1, getHeight());
	}
}
