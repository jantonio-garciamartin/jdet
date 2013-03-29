package view;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Default StructureWindowListener class, handles what happens when the Structure3DFrame is closed.
 * @author Thilo Muth
 *
 */
public class ClusterWindowListener implements WindowListener{
	
	private Cluster3DFrame frame;
	private SequencePainter seqPainter;
	
	public ClusterWindowListener(Cluster3DFrame frame, SequencePainter seqPainter){
		this.frame = frame;
		this.seqPainter = seqPainter;
	}

	/**
	 * When the window is closing, the marking and the selections are canceled.
	 */
	public void windowClosing(WindowEvent e) {
		seqPainter.deregister(frame);
	}
	
	public void windowClosed(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
}
