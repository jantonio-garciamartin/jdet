package view;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import util.SequenceManager;

/**
 * Default StructureWindowListener class, handles what happens when the Structure3DFrame is closed.
 * @author Thilo Muth
 *
 */
public class StructureWindowListener implements WindowListener{
	
	private Structure3DFrame frame;
	private NamePainter namePainter;
	private SequencePainter seqPainter;
	private MethodPanel methodPanel;
	private IndexPanel indexPanel;
	
	public StructureWindowListener(Structure3DFrame frame, NamePainter namePainter, SequencePainter seqPainter, MethodPanel methodPanel, IndexPanel indexPanel){
		this.frame = frame;
		this.namePainter = namePainter;
		this.seqPainter = seqPainter;
		this.methodPanel = methodPanel;
		this.indexPanel = indexPanel;
	}

	/**
	 * When the window is closing, the marking and the selections are canceled.
	 */
	public void windowClosing(WindowEvent e) {
		SequenceManager.setMarkedSequenceName(null);
		namePainter.repaint();		
		seqPainter.setSumScrollX(0);
		seqPainter.unsetJmolPanel();
		seqPainter.deregister(frame);
		methodPanel.repaint();
		indexPanel.repaint();
		seqPainter.repaint();
	}
	
	public void windowClosed(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
}
