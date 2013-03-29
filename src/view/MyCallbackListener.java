package view;
import org.jmol.api.JmolCallbackListener;

import view.Structure3DFrame.JmolPanel;

public class MyCallbackListener implements JmolCallbackListener{
	private JmolPanel jmolPanel;
	public MyCallbackListener(JmolPanel jmolPanel){
		this.jmolPanel = jmolPanel;
	}
	
	public void notifyCallback(int arg0, Object[] arg1) {
		if(arg1.length >= 1) {
			//jmolPanel.setSelection(arg1);
			jmolPanel.addSelection(arg1);
			jmolPanel.notifyObservers();
		}
	}

	public boolean notifyEnabled(int arg0) {
		// Index == 9 --> notify on mouseclick
		if (arg0 == 9){
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void setCallbackFunction(String arg0, String arg1) {
		
	}
}