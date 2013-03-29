package view;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JSlider;
import javax.swing.JTextField;

import util.Constants;

public class SliderKeyAdapter extends KeyAdapter{
	
	private JSlider slider;
	private JTextField textField;
	private int index;
	public SliderKeyAdapter(JSlider slider, JTextField textField, int index){
		this.slider = slider;
		this.textField = textField;
		this.index = index;
	}
	
	public void keyReleased(KeyEvent ke) {
		if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
			try {
				String text = textField.getText();
				if (text.length() > 4) {
					textField.addKeyListener(new KeyAdapter() {

						@Override
						public void keyReleased(KeyEvent ke) {
							if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
								try {
									String text = textField.getText();
									if (text.length() > 4) {
										throw new NumberFormatException();
									}
									int value = new Double(Double.parseDouble(text) * 10)
											.intValue();
									slider.setValue(value);
								} catch (NumberFormatException e1) {
									slider.setValue(Constants.OTHERSLIDER_DEFAULT[index]);
									textField.setText(String.valueOf(new Double(slider.getValue()) / 10));
								}
							}
						}
					});			throw new NumberFormatException();
				}
				int value = new Double(Double.parseDouble(text) * 10).intValue();
				slider.setValue(value);
			} catch (NumberFormatException e1) {
				slider.setValue(Constants.OTHERSLIDER_DEFAULT[index]);
				textField.setText(String.valueOf(new Double(slider.getValue()) / 10));
			}
		}

	}
}
