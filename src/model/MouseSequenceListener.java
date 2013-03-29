package model;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;

/**
 * MouseSequenceListener for listening on the mouse event from the alignment panel.
 * @author Thilo Muth
 *
 */
public class MouseSequenceListener extends Observable implements MouseListener{
	
	public void mousePressed(MouseEvent e) {
		setChanged();
		notifyObservers();		
	}
	
	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

}
