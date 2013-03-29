package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;

import util.ColorScheme;
import util.Constants;

public class ColorSchemeInfo extends Component{
	private static final long serialVersionUID = 1L;
	private double minValue =0;
	private double maxValue =0;
	private String paletteName = "";
	
	public ColorSchemeInfo(double minValue,double maxValue){
		super();
		this.minValue = minValue;
		this.maxValue = maxValue;	
		paletteName = "";
	}

	public ColorSchemeInfo(String paletteName){
		super();
		this.paletteName = paletteName;
	}

	public void setPaletteName(String paletteName){
		this.paletteName = paletteName;
		this.minValue =0;
		this.maxValue =0;
		this.repaint();		
	}
	public void setValues(double minValue,double maxValue){
		this.minValue = minValue;
		this.maxValue = maxValue;	
		this.paletteName = "";
		this.repaint();
	}

	
	
	public void paint(Graphics g){
		g.setFont(new Font(Constants.LOGOHEADER_FONT,Constants.LOGOHEADER_FONTSTYLE,Constants.LOGOHEADER_FONTSIZE));
		int textHeight = g.getFontMetrics().getAscent();	
		if(!paletteName.equals("")){
			g.setColor(Color.BLACK);
			g.drawString(paletteName, 0, textHeight);
		}
		else if(minValue!=0 && maxValue != 0){
			double scale = 255./(maxValue-minValue);
			int minValueWidth = g.getFontMetrics().stringWidth(minValue+"");
			g.setColor(Color.BLACK);
			g.drawString(minValue+"", 0, textHeight);		
			for(int i=0; i<ColorScheme.PALETTE_STEPS; i++){
				Color color = ColorScheme.getColorFromValue(minValue+(i*(maxValue-minValue)/ColorScheme.PALETTE_STEPS),  this.maxValue,  this.minValue,  scale, ColorScheme.getColorPalette());
				g.setColor(color);
				g.fillRect(minValueWidth+ColorScheme.PALETTE_TEXT_MARGIN+(i*ColorScheme.PALETTE_WIDTH/10), 0, (ColorScheme.PALETTE_WIDTH/10), ColorScheme.PALETTE_HEIGHT);
			}
			g.setColor(Color.BLACK);
			g.drawString(maxValue+"", minValueWidth+ColorScheme.PALETTE_WIDTH+(2*ColorScheme.PALETTE_TEXT_MARGIN), textHeight);
		}
	}
}

