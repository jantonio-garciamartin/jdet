package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.Methods;
import util.Constants;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Represents the panel on the upper left corner which contains the threshold panels + sliders.
 * @author Thilo Muth
 *
 */
public class SliderPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private AlignFrame alignFrame;	
	private JSlider xDetSlider;
	private JTextField xDetTtf;
	private JSlider entropySlider;
	private JTextField entropyTtf;
	private JTextField s3DetTtf;
	private JSlider s3DetSlider;
	private JTextField[] otherTtf;
	private JSlider[] otherSlider;
	private Methods methods;
	private JLabel xDetLbl;
	private JLabel entropyLbl;
	private JLabel s3DetLbl;
	private JLabel[] otherLbl;
	private int udmNumber;
	private int index;
	private int xDetCutoff;
	private int entropyCutoff;
	private int s3DetCutoff;
	private int scoreCutoff;
	private JButton xDetSelectOr;
	private JButton entropySelectOr;
	private JButton s3DetSelectOr;
	private JButton[] otherSelectOr;
	private JButton xDetSelectAnd;
	private JButton entropySelectAnd;
	private JButton s3DetSelectAnd;
	private JButton[] otherSelectAnd;
	
	/**
	 * Default constructor.
	 */
	public SliderPanel(){
		this.setBackground(Constants.METHODDESCPNL_BGCOLOR);
		this.setMinimumSize(new Dimension(Constants.METHODDESCPNL_WIDTH, Constants.METHODDESCPNL_HEIGHT));
		this.setPreferredSize(new Dimension(Constants.METHODDESCPNL_WIDTH, Constants.METHODDESCPNL_HEIGHT));
		this.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
	}
	/**
	 * Constructor of the SliderPanel.
	 * @param alignFrame
	 */
	public SliderPanel(AlignFrame alignFrame) {
		this.alignFrame = alignFrame;		
		this.methods = alignFrame.getMethods();		
		this.udmNumber = methods.getUdmNumber();
		this.setBackground(Constants.METHODDESCPNL_BGCOLOR);
		calculateHeight();
		this.setMinimumSize(new Dimension(Constants.METHODDESCPNL_WIDTH, Constants.METHODDESCPNL_HEIGHT));
		this.setPreferredSize(new Dimension(Constants.METHODDESCPNL_WIDTH, Constants.METHODDESCPNL_HEIGHT));
		this.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));		
		buildThresholdPanels();
	}
	
	/**
	 * Helper method to calculate the effective height for this panel,
	 * depending on the number of methods used. 
	 */
	private void calculateHeight(){
		int height = 20;		
		height += (Constants.METHOD_OFFSET * methods.getMethodNumber());	
		Constants.METHODDESCPNL_HEIGHT = height;		
	}
	/**
	 * This methods builds the threshold panels
	 */
	private void buildThresholdPanels(){
		
		// Slider Panel Layout
		//this.setLayout(new FormLayout("1dlu, p, 1dlu, p, p,left:p", "5dlu, p, p, p, p, p, p, p, p, 10dlu"));
		this.setLayout(new FormLayout("5px, 70px, 5px, 45px, 100px, 10px, 10px", "5dlu, p, p, p, p, p, p, p, p, 10dlu"));
		//this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		CellConstraints cc = new CellConstraints();
		
		//XDet
		xDetLbl = new JLabel("XDet");
		xDetLbl.setFont(new Font("monospaced",Font.PLAIN,Constants.LABEL_FONT_SIZE));
		xDetTtf = new JTextField(3);
		xDetTtf.setMinimumSize(new Dimension(100, 18));
		xDetTtf.setPreferredSize(new Dimension(100, 18));
		xDetCutoff = new Double(methods.getxDetCutoff() * 100).intValue();
		xDetSlider = new JSlider(0, (xDetCutoff*2), xDetCutoff);
		xDetSlider.setMinimumSize(new Dimension(120, 18));
		xDetSlider.setPreferredSize(new Dimension(120, 18));
		xDetSlider.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
    	xDetTtf.setText(String.valueOf(new Double(xDetCutoff) / 100)); 
    	xDetTtf.setToolTipText(Constants.TEXTFIELD_TOOLTIP);
		xDetSlider.setPaintLabels(false);
		xDetSlider.setFocusable(false);
		xDetSlider.setOpaque(false);
		xDetSlider.setToolTipText(Constants.XDETSLIDER_TOOLTIP);

        xDetSlider.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {       
            	double corrThreshold = (new Double(xDetSlider.getValue()) / 100);
            	xDetTtf.setText(String.valueOf(corrThreshold));            	
            	alignFrame.getAlignPanel().getMethodPanel().updatePanelWithXdetThreshold(corrThreshold);
            }
        });        
       
		xDetTtf.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					try {
						String text = xDetTtf.getText();
						if (text.length() > 4) {
							throw new NumberFormatException();
						}
						int value = new Double(Double.parseDouble(text) * 100).intValue();
						xDetSlider.setValue(value);
					} catch (NumberFormatException e1) {
						xDetSlider.setValue(xDetCutoff);
						xDetTtf.setText(String.valueOf(new Double(xDetSlider.getValue()) / 100));
					}
				}
			}
		});
		ImageIcon addIcon = AlignFrame.getImageIcon("img/add.png");
		ImageIcon orIcon = AlignFrame.getImageIcon("img/or.png");
		Dimension buttonSize = getPreferredSize();
		buttonSize.width = buttonSize.height = 10;

		xDetSelectOr = new JButton(addIcon);
		xDetSelectOr.setPreferredSize(buttonSize);
		xDetSelectOr.setToolTipText("Add to selection residues from XDet");
		xDetSelectOr.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	alignFrame.selectXDetItems(Constants.SELECTION_OR);
            }
        });

		xDetSelectAnd = new JButton(orIcon);
		xDetSelectAnd.setPreferredSize(buttonSize);
		xDetSelectAnd.setToolTipText("Combine selection with residues from XDet");
		xDetSelectAnd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	alignFrame.selectXDetItems(Constants.SELECTION_AND);
            }
        });
		
		
		
		
        //Entropy
		entropyLbl = new JLabel("Entropy");
		entropyLbl.setFont(new Font("monospaced",Font.PLAIN,Constants.LABEL_FONT_SIZE));
		entropyTtf = new JTextField(3);
        entropyTtf.setMinimumSize(new Dimension(100, 18));
        entropyTtf.setPreferredSize(new Dimension(100, 18));
        entropyCutoff = new Double(methods.getEntropyCutoff() * 10).intValue();
		entropySlider = new JSlider(0, (entropyCutoff*2), entropyCutoff);
		entropySlider.setMinimumSize(new Dimension(120, 18));
		entropySlider.setPreferredSize(new Dimension(120, 18));
		entropySlider.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		entropyTtf.setText(String.valueOf(new Double(entropyCutoff) / 10));
		entropyTtf.setToolTipText(Constants.TEXTFIELD_TOOLTIP);
		entropySlider.setPaintLabels(false);
		entropySlider.setFocusable(false);
		entropySlider.setOpaque(false);
		entropySlider.setToolTipText(Constants.ENTROPYSLIDER_TOOLTIP);
		
		entropySlider.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {       
            	double entropyThreshold = (new Double(entropySlider.getValue()) / 10);
            	entropyTtf.setText(String.valueOf(entropyThreshold));
            	alignFrame.getAlignPanel().getMethodPanel().updatePanelWithEntropyThreshold(entropyThreshold);
            }
        });
		entropyTtf.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					try {
						String text = entropyTtf.getText();
						if (text.length() > 4) {
							entropyTtf.addKeyListener(new KeyAdapter() {

								@Override
								public void keyReleased(KeyEvent ke) {
									if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
										try {
											String text = entropyTtf.getText();
											if (text.length() > 4) {
												throw new NumberFormatException();
											}
											int value = new Double(Double.parseDouble(text) * 100)
													.intValue();
											entropySlider.setValue(value);
										} catch (NumberFormatException e1) {
											entropySlider.setValue(entropyCutoff);
											entropyTtf.setText(String.valueOf(new Double(entropySlider.getValue()) / 100));
										}
									}
								}
							});			throw new NumberFormatException();
						}
						int value = new Double(Double.parseDouble(text) * 10).intValue();
						entropySlider.setValue(value);
					} catch (NumberFormatException e1) {
						entropySlider.setValue(entropyCutoff);
						entropyTtf.setText(String.valueOf(new Double(entropySlider.getValue()) / 10));
					}
				}

			}
		});

		entropySelectOr = new JButton(addIcon);
		entropySelectOr.setPreferredSize(buttonSize);		
		entropySelectOr.setToolTipText("Add to selection residues from Entropy");
		entropySelectOr.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	alignFrame.selectEntropyItems(Constants.SELECTION_OR);
            }
        });

		entropySelectAnd = new JButton(orIcon);
		entropySelectAnd.setPreferredSize(buttonSize);		
		entropySelectAnd.setToolTipText("Combine selection with residues from Entropy");
		entropySelectAnd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	alignFrame.selectEntropyItems(Constants.SELECTION_AND);
            }
        });
		
		
		// Check if method enabled
		if(!methods.isEntropyUsed()){
			entropySlider.setEnabled(false);
			entropyTtf.setEnabled(false);
		}
        
        //S3Det
		s3DetLbl = new JLabel("S3Det");
		s3DetLbl.setFont(new Font("monospaced",Font.PLAIN,Constants.LABEL_FONT_SIZE));
        s3DetTtf = new JTextField(3);
        s3DetTtf.setMinimumSize(new Dimension(100, 18));
        s3DetTtf.setPreferredSize(new Dimension(100, 18));
        s3DetTtf.setToolTipText(Constants.TEXTFIELD_TOOLTIP);
        s3DetCutoff = new Double(methods.getS3DetCutoff()).intValue();
		s3DetSlider = new JSlider(0, (s3DetCutoff*2), s3DetCutoff);
		s3DetSlider.setMinimumSize(new Dimension(120, 18));
		s3DetSlider.setPreferredSize(new Dimension(120, 18));
		s3DetSlider.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		s3DetTtf.setText(String.valueOf(new Double(s3DetCutoff)));
		s3DetSlider.setPaintLabels(false);
		s3DetSlider.setFocusable(false);
		s3DetSlider.setOpaque(false);
		s3DetSlider.setToolTipText(Constants.S3DETSLIDER_TOOLTIP);
		
		s3DetSlider.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {       
            	double rankThreshold = (new Double(s3DetSlider.getValue()) );
            	s3DetTtf.setText(String.valueOf(rankThreshold));
            	alignFrame.getAlignPanel().getMethodPanel().updatePanelWithS3detThreshold(rankThreshold);
            }
        });
		s3DetTtf.addKeyListener(new KeyAdapter(){
        	
			@Override
			public void keyReleased(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					try {
						String text = s3DetTtf.getText();
						if (text.length() > 4) {
							s3DetTtf.addKeyListener(new KeyAdapter() {

								@Override
								public void keyReleased(KeyEvent ke) {
									if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
										try {
											String text = s3DetTtf.getText();
											if (text.length() > 4) {
												throw new NumberFormatException();
											}
											int value = new Double(Double.parseDouble(text)).intValue();
											s3DetSlider.setValue(value);
										} catch (NumberFormatException e1) {
											xDetSlider.setValue(s3DetCutoff);
											s3DetTtf.setText(String.valueOf(new Double(s3DetSlider.getValue()) / 100));
										}
									}
								}
							});			throw new NumberFormatException();
						}
						int value = new Double(Double.parseDouble(text)).intValue();
						s3DetSlider.setValue(value);
					} catch (NumberFormatException e1) {
						s3DetSlider.setValue(s3DetCutoff);
						s3DetTtf.setText(String.valueOf(new Double(s3DetSlider.getValue())));
					}
				}

			}
        });
		
		s3DetSelectOr = new JButton(addIcon);
		s3DetSelectOr.setPreferredSize(buttonSize);		
		s3DetSelectOr.setToolTipText("Add to selection residues from S3Det");
		s3DetSelectOr.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	alignFrame.selectS3DetItems(Constants.SELECTION_OR);
            }
        });
		
		s3DetSelectAnd = new JButton(orIcon);
		s3DetSelectAnd.setPreferredSize(buttonSize);		
		s3DetSelectAnd.setToolTipText("Combine selection with residues from from S3Det");
		s3DetSelectAnd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	alignFrame.selectS3DetItems(Constants.SELECTION_AND);
            }
        });
		
		// Check if method enabled
		if(!methods.isS3DetUsed()){
			s3DetSlider.setEnabled(false);
			s3DetTtf.setEnabled(false);
		}
		
		if(udmNumber > 0){
			otherLbl= new JLabel[udmNumber];
			otherTtf= new JTextField[udmNumber];
			otherSlider = new JSlider[udmNumber];
			otherSelectOr = new JButton[udmNumber];
			otherSelectAnd = new JButton[udmNumber];
			for (index = 0; index < udmNumber; index++){
				// User defined (other) label
				String label = methods.getUdmObjects()[index].getMethodName(); 
				if(label.length()>Constants.MAX_LABEL_LENGTH){
					label = label.substring(0,Constants.MAX_LABEL_LENGTH);
				}	
				otherLbl[index] = new JLabel(label);
				otherLbl[index].setFont(new Font("monospaced",Font.PLAIN,Constants.LABEL_FONT_SIZE));
		        otherTtf[index] = new JTextField(3);
		        otherTtf[index].setMinimumSize(new Dimension(100, 18));
		        otherTtf[index].setPreferredSize(new Dimension(100, 18));
		        otherTtf[index].setToolTipText(Constants.TEXTFIELD_TOOLTIP);
		        scoreCutoff = new Double(methods.getScoreCutoffs()[index] * 10).intValue();
			//	otherSlider[index] = new JSlider(0, scoreCutoff*2, scoreCutoff);
				otherSlider[index] = new JSlider(new Double(methods.getUdmObjects()[index].getMinValue()).intValue(), new Double(methods.getUdmObjects()[index].getMaxValue()).intValue()*10, scoreCutoff);				
				otherTtf[index].setText(String.valueOf(String.valueOf(new Double(scoreCutoff) / 10)));
				otherSlider[index].setMinimumSize(new Dimension(120, 18));
				otherSlider[index].setPreferredSize(new Dimension(120, 18));
				otherSlider[index].setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
				otherSlider[index].setPaintLabels(false);
				otherSlider[index].setFocusable(false);
				otherSlider[index].setOpaque(false);
				otherSlider[index].setToolTipText(Constants.OTHERSLIDER_TOOLTIP);
				otherSlider[index].addChangeListener(new SliderChangeListener(alignFrame, otherSlider[index], otherTtf[index], index));
				otherTtf[index].addKeyListener(new SliderKeyAdapter(otherSlider[index], otherTtf[index], index));
				otherSelectOr[index] = new JButton(addIcon);
				otherSelectOr[index].setPreferredSize(buttonSize);
				otherSelectOr[index].setToolTipText("Add to selection residues from "+methods.getUdmObjects()[index].getMethodName());
				otherSelectOr[index].addActionListener(new ActionListener() {
		            public void actionPerformed(ActionEvent evt) {
		            	JButton source = (JButton) evt.getSource();
		            	String methodName = source.getToolTipText().substring(source.getToolTipText().lastIndexOf(" ")+1);
		            	alignFrame.selectUserDefItems(methodName, Constants.SELECTION_OR);
		            }
		        });

				otherSelectAnd[index] = new JButton(orIcon);
				otherSelectAnd[index].setPreferredSize(buttonSize);
				otherSelectAnd[index].setToolTipText("Combine selection with residues from "+methods.getUdmObjects()[index].getMethodName());
				otherSelectAnd[index].addActionListener(new ActionListener() {
		            public void actionPerformed(ActionEvent evt) {
		            	JButton source = (JButton) evt.getSource();
		            	String methodName = source.getToolTipText().substring(source.getToolTipText().lastIndexOf(" ")+1);
		            	alignFrame.selectUserDefItems(methodName, Constants.SELECTION_AND);
		            }
		        });

				
			}
		}
	    
		
		int yCC = 2;
		// Check if method enabled
		if(methods.isxDetUsed()){
			this.add(xDetLbl, cc.xy(2, yCC));
			this.add(xDetTtf, cc.xy(4, yCC));
			this.add(xDetSlider, cc.xy(5, yCC));
			this.add(xDetSelectOr, cc.xy(6, yCC));			
			this.add(xDetSelectAnd, cc.xy(7, yCC));			
			yCC++;
		}
		if(methods.isEntropyUsed()){
			this.add(entropyLbl, cc.xy(2, yCC));
			this.add(entropySlider, cc.xy(5, yCC));
			this.add(entropyTtf, cc.xy(4, yCC));
			this.add(entropySelectOr, cc.xy(6, yCC));			
			this.add(entropySelectAnd, cc.xy(7, yCC));			
			yCC++;
		}
		if(methods.isS3DetUsed()){
			this.add(s3DetLbl, cc.xy(2, yCC));
			this.add(s3DetSlider, cc.xy(5, yCC));
			this.add(s3DetTtf, cc.xy(4, yCC));
			this.add(s3DetSelectOr, cc.xy(6, yCC));			
			this.add(s3DetSelectAnd, cc.xy(7, yCC));			
			yCC++;
		}
		if(udmNumber > 0){
			for (index = 0; index < udmNumber; index++){
				this.add(otherLbl[index], cc.xy(2, yCC));
				this.add(otherSlider[index], cc.xy(5, yCC));
				this.add(otherTtf[index], cc.xy(4, yCC));
				this.add(otherSelectOr[index], cc.xy(6, yCC));
				this.add(otherSelectAnd[index], cc.xy(7, yCC));
				yCC++;
			}
		}
	}
	
	/**
	 * Draws the method data.
	 * @param g
	 * @param startx
	 * @param endx
	 * @param width
	 * @param height
	 */
	public void drawPanel(Graphics g, int startx, int endx, int width, int height) {
		Graphics2D g2d = (Graphics2D) g;

		// Anti aliasing
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Background color
		g2d.setColor(Constants.METHODDESCPNL_BGCOLOR);		
		g2d.fillRect(0, 0, width, height);
		g2d.setColor(Color.black);
		
		// FontMetrics object
		g2d.setFont(new Font(Constants.ALIGNVIEW_FONT, Constants.ALIGNVIEW_FONTSTYLE, Constants.ALIGNVIEW_FONTSIZE));		
		FontMetrics fm = g2d.getFontMetrics(g2d.getFont());
		
		int margin = Constants.METHOD_OFFSET;
		int y_index = fm.getHeight()-fm.getDescent()+(Constants.METHOD_OFFSET/2)+2;
		
		if (methods.isxDetUsed()) {
			g2d.drawString("XDet: "+methods.getxDetCutoff(), margin, y_index);		
			y_index+= Constants.METHOD_OFFSET;
		}

		if (methods.isEntropyUsed()) {
			g2d.drawString("Entropy: "+methods.getEntropyCutoff(), margin, y_index);
			y_index+= Constants.METHOD_OFFSET;
		}		

		if (methods.isS3DetUsed()) {
			g2d.drawString("S3: "+methods.getS3DetCutoff(), margin, y_index);		
			y_index+= Constants.METHOD_OFFSET;
		}
		
		for (int i =0; i< methods.getUdmNumber();i++) {			
			g2d.drawString(methods.getUdmObjects()[i].getMethodName()+": "+methods.getUdmObjects()[i].getScoreCutoff(), margin, y_index);		
			y_index+= Constants.METHOD_OFFSET;
		}
	}
	
	
}
