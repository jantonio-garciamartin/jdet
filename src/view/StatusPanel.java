package view;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;

import model.AlignObject;
import util.ColorScheme;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Extended StatusPanel for showing the alignment name, the sequence length and the sequence number.
 * @author Thilo Muth
 *
 */
public class StatusPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private AlignObject alObj;
	private JLabel seqNumberLabel;
	private JLabel alignLengthLabel;
	private JLabel filenameLabel;
	private JLabel filenameLabel2;
	private JLabel alignLengthLabel2;
	private JLabel seqNumberLabel2;
	private JPanel contentPanel;
	private JLabel colorLabel;	
	private ColorSchemeInfo colorPalette;
	
    public StatusPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(getWidth(), 30));
        setBackground(new Color(236, 233, 216));
        init();
    }   
    
    public StatusPanel(AlignViewProps viewProps) {
    	this.alObj = viewProps.getAlObj();
    	setLayout(new BorderLayout());
        setPreferredSize(new Dimension(getWidth(), 30));
        setBackground(new Color(236, 233, 216));
        init();
    }  
    
    private void init(){
    	contentPanel = new JPanel();
    	contentPanel.setOpaque(false);
    	contentPanel.setLayout(new FormLayout("5dlu, p , 5dlu, 200px, 5dlu, 25dlu, p, 5dlu, p, 25dlu, p, 25dlu, p, 5dlu, p, 25dlu, p, 25dlu, p, 5dlu, p, 5dlu", "4dlu, fill:10dlu, 2dlu"));
    	colorLabel = new JLabel();
    	colorLabel.setText("Schema:");   
		colorPalette = ColorScheme.getPaletteImage();
    	
    	seqNumberLabel = new JLabel();
		seqNumberLabel2 = new JLabel();
		seqNumberLabel.setText("Number of sequences:");	
		if(alObj != null){
			seqNumberLabel2.setText(new Integer(alObj.getProteinNumber()).toString());
			Font f = seqNumberLabel2.getFont();
			seqNumberLabel2.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
		} else {
			seqNumberLabel2.setText("0");
		}
		
		alignLengthLabel = new JLabel();
		alignLengthLabel2 = new JLabel();
		alignLengthLabel.setText("Alignment length:");		
		if(alObj != null){
			alignLengthLabel2.setText(new Integer(alObj.getWidth()).toString());
			Font f2 = alignLengthLabel2.getFont();
			alignLengthLabel2.setFont(f2.deriveFont(f2.getStyle() ^ Font.BOLD));
		} else {
			alignLengthLabel2.setText("0");
		}
		
		filenameLabel = new JLabel();
		filenameLabel2 = new JLabel();
		filenameLabel.setText("Filename:");
		if(alObj != null){
			filenameLabel2.setText(alObj.getFilename());
			Font f3 = filenameLabel2.getFont();
			filenameLabel2.setFont(f3.deriveFont(f3.getStyle() ^ Font.BOLD));
		} else {
			filenameLabel2.setText("(No file loaded)");
		}
		
		CellConstraints cc = new CellConstraints();
        contentPanel.add(colorLabel, cc.xy(2, 2));
        contentPanel.add(colorPalette, cc.xy(4, 2));		
		contentPanel.add(new SeparatorPanel(Color.GRAY, Color.WHITE), cc.xy(5, 2));        
		contentPanel.add(seqNumberLabel, cc.xy(7, 2));
		contentPanel.add(seqNumberLabel2, cc.xy(9, 2));
		contentPanel.add(new SeparatorPanel(Color.GRAY, Color.WHITE), cc.xy(11, 2));
		contentPanel.add(alignLengthLabel, cc.xy(13, 2));
		contentPanel.add(alignLengthLabel2, cc.xy(15, 2));
		contentPanel.add(new SeparatorPanel(Color.GRAY, Color.WHITE), cc.xy(17, 2));
		contentPanel.add(filenameLabel, cc.xy(19, 2));
		contentPanel.add(filenameLabel2, cc.xy(21, 2));

		add(contentPanel, BorderLayout.CENTER);
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int y = 0;
        g.setColor(new Color(156, 154, 140));
        g.drawLine(0, y, getWidth(), y);
        y++;
        g.setColor(new Color(196, 194, 183));
        g.drawLine(0, y, getWidth(), y);
        y++;
        g.setColor(new Color(218, 215, 201));
        g.drawLine(0, y, getWidth(), y);
        y++;
        g.setColor(new Color(233, 231, 217));
        g.drawLine(0, y, getWidth(), y);

        y = getHeight() - 3;
        g.setColor(new Color(233, 232, 218));
        g.drawLine(0, y, getWidth(), y);
        y++;
        g.setColor(new Color(233, 231, 216));
        g.drawLine(0, y, getWidth(), y);
        y = getHeight() - 1;
        g.setColor(new Color(221, 221, 220));
        g.drawLine(0, y, getWidth(), y);
    }

	/**
	 * @return the colorPalette
	 */
	public ColorSchemeInfo getColorPalette() {
		return colorPalette;
	}

	/**
	 * @param colorPalette the colorPalette to set
	 */
	public void setColorPalette(ColorSchemeInfo colorPalette) {
		this.colorPalette = colorPalette;
	}
    
	/**
	 * @param colorPalette the colorPalette to set
	 */
	public void updateWidth() {
		alignLengthLabel2.setText(alObj.getWidth()+"");
	}

	
}