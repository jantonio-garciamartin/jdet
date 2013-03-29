package view;

import interfaces.Observable;
import interfaces.Observer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolAdapter;
import org.jmol.api.JmolSimpleViewer;
import org.jmol.api.JmolViewer;
import org.pushingpixels.substance.internal.contrib.jgoodies.looks.Options;

import util.Alignment;
import util.Constants;
import util.SelectionManager;

import com.jgoodies.looks.HeaderStyle;

/**
 * This class is the viewer for the protein 3D structure.
 * It uses JMol as java library which is similar to RasMol.
 * Implements Observer, which means that it listens to MouseEvents from the alignment GUI. 
 * @author Thilo Muth
 *
 */
public class Structure3DFrame extends JFrame implements Observer{
	
	private static final long serialVersionUID = 1L;
	private JmolPanel jmolPanel;
	private JFrame frame;
	private JMenuItem ribbonMenuItem;
	private JMenuItem cartoonMenuItem;
	private JMenuItem spacefillMenuItem;
	private String selectedChain;
	private JMenuItem zoomOutMenuItem;
	private JMenuItem zoomInMenuItem;
	private Alignment alignment;
	private JPanel commandsPanel;
	private JTextField commandsBox;
	private JButton execCommand;

	/**
	 * Constructor for the Structure3DFrame
	 * @param pdbFile
	 * @param seqPainter
	 * @param selectedChain
	 * @param alignment
	 */
	public Structure3DFrame(String pdbFile, SequencePainter seqPainter, String selectedChain, Alignment alignment, NamePainter namePainter, AlignPanel alignPanel) {
		this.selectedChain = selectedChain;
		this.alignment = alignment;		
		this.addWindowListener(new StructureWindowListener(this, namePainter, seqPainter, alignPanel.getMethodPanel(), alignPanel.getIndexPanel()));
		alignPanel.setScrollValues(0, 0);
		frame = this;
		frame.setTitle("Structure 3D View");
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.Y_AXIS));
		contentPane.setPreferredSize(new Dimension(Constants.JMOLPANEL_WIDTH, Constants.JMOLPANEL_HEIGHT+30));
		jmolPanel = new JmolPanel();
		
		jmolPanel.setPreferredSize(new Dimension(Constants.JMOLPANEL_WIDTH, Constants.JMOLPANEL_HEIGHT));
		jmolPanel.readPDBFile(pdbFile);
		String selectChain = "select *" + selectedChain + " and not hetero and not solvent and not hoh; wireframe off; cartoon on; ribbon off; spacefill off; select hetero and not solvent and not hoh and *"+selectedChain+"; spacefill; color yellow; select not all"; 
		String deselectChain = "select not *" + selectedChain + " or solvent; wireframe off; spacefill off; backbone 20;";
		
		// Select
		jmolPanel.eval(selectChain);
		jmolPanel.eval(deselectChain);
		jmolPanel.eval("set disablePopupMenu on");
		
		
		// Add the option menu to the frame.
		constructMenu();
		contentPane.add(jmolPanel);
		highlightSelection();
		
		commandsBox = new JTextField();
		commandsBox.addKeyListener(new java.awt.event.KeyListener() {
			public void  keyPressed(java.awt.event.KeyEvent evt){}
			public void  keyTyped(java.awt.event.KeyEvent evt){}
            public void  keyReleased(java.awt.event.KeyEvent evt) {
            	if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            		executeCommand();
            	}
            }
		});
		
		execCommand = new JButton("Execute");
		execCommand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
        		executeCommand();
            }
		});

		commandsBox.setMinimumSize(new Dimension(100, 30));
		commandsBox.setPreferredSize(new Dimension(Constants.JMOLPANEL_WIDTH-100, 30));
		commandsBox.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));

		commandsPanel = new JPanel();
		commandsPanel.setSize(new Dimension(Constants.JMOLPANEL_WIDTH, 30));
		commandsPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
		commandsPanel.setLayout(new BorderLayout());
		
		
		commandsPanel.add(commandsBox, BorderLayout.CENTER);
		commandsPanel.add(execCommand,BorderLayout.EAST);
		contentPane.add(commandsPanel);
		
		seqPainter.notifyObservers();
		seqPainter.register(this);		
		seqPainter.setJmolPanel(jmolPanel);
		SelectionManager.setEnabled(true);
		frame.pack();
		frame.setVisible(true);
	}	

	/**
	 * Construct the options menu.
	 */
	private void constructMenu() {

	        JMenuBar menuBar = new JMenuBar();
	        menuBar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.SINGLE);
	        
	       
	        // Defining the menus
	        JMenu optionMenu = new JMenu("Options");
	        optionMenu.setMnemonic('O');
	        menuBar.add(optionMenu);
	        
	        // Defining the menus
	        JMenu zoomMenu = new JMenu("Zoom");
	        optionMenu.setMnemonic('Z');
	        menuBar.add(zoomMenu);
	        
	        // The menu items
	        ribbonMenuItem = new JMenuItem();
	        cartoonMenuItem = new JMenuItem();
	        spacefillMenuItem = new JMenuItem();
	        
	        ribbonMenuItem.setMnemonic('R');
	        ribbonMenuItem.setText("Ribbon");
	        ribbonMenuItem.addActionListener(new java.awt.event.ActionListener() {

	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	            	String ribbon = "select *" + selectedChain + " and not hetero; cartoon off; ribbon on; spacefill off;";// select hetero and not solvent; wireframe off;"; 
	            	jmolPanel.eval(ribbon);
	            	jmolPanel.repaint();
	            }
	        });
	        optionMenu.add(ribbonMenuItem);
	        
	        cartoonMenuItem.setMnemonic('C');
	        cartoonMenuItem.setText("Cartoon");
	        cartoonMenuItem.addActionListener(new java.awt.event.ActionListener() {

	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	            	String cartoon = "select *" + selectedChain + " and not hetero; cartoon on; ribbon off; spacefill off;";// select hetero and not solvent; wireframe off;";
	            	jmolPanel.eval(cartoon);
	            	jmolPanel.repaint();
	            }
	        });
	        optionMenu.add(cartoonMenuItem);
	        
	        spacefillMenuItem.setMnemonic('S');
	        spacefillMenuItem.setText("Spacefill");
	        spacefillMenuItem.addActionListener(new java.awt.event.ActionListener() {

	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	            	String spacefill = "select *" + selectedChain + " and not hetero; cartoon off; ribbon off; spacefill on; select hetero and not solvent; wireframe off;";
	            	jmolPanel.eval(spacefill);
	            	jmolPanel.repaint();
	            }
	        });
	        optionMenu.add(spacefillMenuItem);
	        
	        // The zoom menu items
	        zoomOutMenuItem = new JMenuItem();
	        zoomInMenuItem = new JMenuItem();
	        
	        zoomInMenuItem.setMnemonic('i');
	        zoomInMenuItem.setText("Zoom in +");
	        zoomInMenuItem.addActionListener(new java.awt.event.ActionListener() {

	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	            	jmolPanel.eval("zoomto *1.2");
	            }
	        });
	        zoomMenu.add(zoomInMenuItem);
	        
	        zoomOutMenuItem.setMnemonic('o');
	        zoomOutMenuItem.setText("Zoom out -");
	        zoomOutMenuItem.addActionListener(new java.awt.event.ActionListener() {

	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	            	jmolPanel.eval("zoomto /1.2");
	            }
	        });
	        zoomMenu.add(zoomOutMenuItem);
	        
	   
	        
	        // LAF changing
	        //menuBar.add(SampleMenuFactory.getLookAndFeelMenu(this));
	        frame.setJMenuBar(menuBar);
	}
	
	/**
	 * Returns the viewer from the panel.
	 * @return JmolSimpleViewer
	 */
	public JmolSimpleViewer getViewer() {

		return jmolPanel.getViewer();
	}
	
	class JmolPanel extends JPanel implements Observable{
		
		private static final long serialVersionUID = 1L;
		JmolViewer viewer;
		JmolAdapter adapter;
		private Vector<Observer> observersList;
		private Object[] selection;
		
		public JmolPanel() {
			
			adapter = new SmarterJmolAdapter();			
			observersList = new Vector<Observer>();
			viewer = (JmolViewer) JmolViewer.allocateSimpleViewer(this, adapter);
		    viewer.setJmolCallbackListener(new MyCallbackListener(this));

		}		
		/**
		 * Notify the observer.
		 */
		public void notifyObservers() {
			// Send notify to all Observers
		    for (int i = 0; i < observersList.size(); i++) {
		      Observer observer = (Observer) observersList.elementAt(i);
		      observer.update(this);
		    }
		}
		
		/**
		 * Registers the observer.
		 */
		public void register(Observer obs) {
			// Add to the list of Observers
		    observersList.addElement(obs);	 
		}
		
		/**
		 * Deregisters the observer.
		 */
		public void deregister(Observer obs) {
			// Remove element from the observer list
		    observersList.removeElement(obs);	 
		}
		
		public JmolViewer getViewer() {
			return viewer;
		}
		
		public void readPDBFile (String file){
			viewer.openFile(file);
		}
		
		public void eval(String attributes){
			viewer.evalString(attributes);
		}
		
		final Dimension currentSize = new Dimension();
		final Rectangle rectClip = new Rectangle();

		public void paint(Graphics g) {
			getSize(currentSize);
			g.getClipBounds(rectClip);	
			viewer.renderScreenImage(g, currentSize, rectClip);
		}
		
		public Object[] getSelection() {
			return selection;
		}
		public void addSelection(Object[] selection) {
			String select = selection[1].toString();
			int pos = new Integer(select.substring(select.indexOf(']') + 1, select.indexOf(':')));
			String chain = select.substring(select.indexOf(':')+1, select.indexOf('.'));
			int alignPos = alignment.getAlignPos(pos);
			if(alignPos != -1 && chain.equals(selectedChain)){
				SelectionManager.toggleSelection(alignPos,true);
			}
		}
		public void setSelection(Object[] selection) {
			this.selection = selection;			
		}
	}
	
	/**
	 * Updates the data from the observable object.
	 */
	public void update(Observable object) {
		if (object instanceof SequencePainter) {
			highlightSelection();
		}
	}
	
	private void highlightSelection(){
		// De-select all
		jmolPanel.eval("select all and not hetero; color cpk; wireframe off; ");

		Vector<Integer> currentSelection = SelectionManager.getFullSelection();
		StringBuffer selection = new StringBuffer();
		for(int i=0;i<currentSelection.size();i++){
			int pos = currentSelection.get(i);

			int currentPDBPos = alignment.getPDBPos(pos);

			// Condition valid PDB position and not GAP!
			if (currentPDBPos > 0) {
				// Create string from current selection amino acid
				selection.append(currentPDBPos + ":" + selectedChain+",");
			}
		}
		if(selection.length()>0){
			// Select aminoacids
			jmolPanel.eval("select " + selection.substring(0,selection.length()-1)+ " and not hetero; color green; wireframe 60; ");
		}
		
	}
	private void executeCommand(){
    	jmolPanel.eval(commandsBox.getText());
    	commandsBox.setText("");
    	jmolPanel.repaint();
	}
}