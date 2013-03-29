package view;

import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This class is an extension to the simple change listener on a JSlider object.
 * @author Thilo Muth
 */
public class SliderChangeListener implements ChangeListener{

	private AlignFrame alignFrame;
	private int index;
	private JSlider slider;
	private JTextField textField;
	
	public SliderChangeListener(AlignFrame alignFrame, JSlider slider, JTextField textField, int index){
		this.alignFrame = alignFrame;
		this.slider = slider;
		this.textField = textField;
		this.index = index;
	}
	
	public void stateChanged(ChangeEvent arg0) {
		double scoreThreshold = (new Double(slider.getValue()) / 10);
		textField.setText(String.valueOf(scoreThreshold));
    	alignFrame.getAlignPanel().getMethodPanel().updatePanelWithScoreThreshold(scoreThreshold, index);
	}
}
