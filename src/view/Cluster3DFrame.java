package view;

import interfaces.Observable;
import interfaces.Observer;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Group;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Node;
import javax.media.j3d.PointArray;
import javax.media.j3d.PointAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import model.ClusterCoordinates;
import model.ClusterGroup;
import model.ResidueCoordinates;
import model.S3DetObject;
import model.SequenceCoordinates;
import util.Constants;
import util.ResidueSelectionManager;
import util.SelectionManager;
import util.SequenceManager;
import util.SequenceOrderer;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.universe.SimpleUniverse;

/**
 * This class draws a window containing a graphical representation of clusters defined by S3Det.
 * @author Thilo Muth
 *
 */

public class Cluster3DFrame extends JFrame implements ItemListener,Observer {
	
	private static final long serialVersionUID = 1L;
	private Color3f selProteinColor = new Color3f(Constants.SELECTED_PROTEIN_COLOR);
	private Color3f selResidueColor = new Color3f(Constants.SELECTED_RESIDUE_COLOR);
	private static final float CLUSTERTRANS = 0.4f;
	private static final float MAX_VALUE = 0.33f;
	private float highestValue=MAX_VALUE;	
	private float scaleFactor=1;
	private float min_x, min_y, min_z;
	private float max_x, max_y, max_z;
	private int[] boxValues;
	private S3DetObject s3detobj;
	private JPanel clusterPanel;
	private JPanel optionPanel;
	private Choice axesBox1;
	private Choice axesBox2;
	private Choice axesBox3;
	private JLabel label1;
	private JLabel label2;
	private JLabel label3;
	private JButton defaultBtn;
	private Canvas3D canvas3D;
	private SimpleUniverse simpleUni;
	private JPanel legendPanel;
	private JLabel subFamiliesLbl;
	private JLabel residuesLbl;
	private JLabel clusterCentersLbl;
	BranchGroup branchGroup;	
	
	private SequenceOrderer seqOrderer;
	private HashMap<String, Integer> name2ClusterIndex;
	public Canvas3D getCanvas3D() {
		return canvas3D;
	}

	public Cluster3DFrame(S3DetObject s3detobj, AlignViewProps viewProps, SequencePainter seqPainter) {
		this.s3detobj = s3detobj;
		this.setTitle("Sequence Spaces");
		getDefaultBoxValues();
		name2ClusterIndex = s3detobj.getName2ClusterIndex();
		seqOrderer = new SequenceOrderer(viewProps.getAlObj(), s3detobj);
		clusterPanel = new JPanel();
		//put the canvas into a JPanel, add panel into contentPane
		clusterPanel.setLayout(new BorderLayout());		
		
		// Labels
		label1 = new JLabel("x-axis:");
		label2 = new JLabel("y-axis:");
		label3 = new JLabel("z-axis:");
		String axis = " axis";
		String[] comboContents = new String[10];
		comboContents[0] = "1st" + axis;
		comboContents[1] = "2nd" + axis;
		comboContents[2] = "3rd" + axis;
		comboContents[3] = "4th" + axis;
		comboContents[4] = "5th" + axis;
		comboContents[5] = "6th" + axis;
		comboContents[6] = "7th" + axis;
		comboContents[7] = "8th" + axis;
		comboContents[8] = "9th" + axis;
		comboContents[9] = "10th" + axis;
		
		// Option comboboxes for axes
		axesBox1 = new Choice();
		axesBox2 = new Choice();
		axesBox3 = new Choice();
		for(int i =0;i< comboContents.length;i++){
			axesBox1.add(comboContents[i]);
			axesBox2.add(comboContents[i]);
			axesBox3.add(comboContents[i]);
		}
		axesBox1.select(0);
		axesBox1.addItemListener(this);
		axesBox2.select(1);
		axesBox2.addItemListener(this);
		axesBox3.select(2);
		axesBox3.addItemListener(this);
/*
		axesBox1 = new Choice(comboContents);		
		axesBox1.setSelectedIndex(boxValues[0]);
		axesBox1.addItemListener(this);
		axesBox2 = new Choice(comboContents);
		axesBox2.setSelectedIndex(boxValues[1]);
		axesBox2.addItemListener(this);
		axesBox3 = new Choice(comboContents);
		axesBox3.setSelectedIndex(boxValues[2]);
		axesBox3.addItemListener(this);*/
		optionPanel = new JPanel(new FormLayout("50dlu, p, 5dlu, p, 10dlu, p, 5dlu, p, 10dlu, p, 5dlu, p, 10dlu, right:p ", "5dlu, p, 5dlu"));
		optionPanel.setBorder(BorderFactory.createTitledBorder("Axes Selection"));
		
		// Build CellConstraints object
		CellConstraints cc = new CellConstraints();
		
		// Add labels + boxes to the options panel
		optionPanel.add(label1, cc.xy(2, 2));
		optionPanel.add(axesBox1, cc.xy(4, 2));
		optionPanel.add(label2, cc.xy(6, 2));
		optionPanel.add(axesBox2, cc.xy(8, 2));
		optionPanel.add(label3, cc.xy(10, 2));
		optionPanel.add(axesBox3, cc.xy(12, 2));
		
		// Default button
		defaultBtn = new JButton("Default");
		defaultBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {	
				axesBox1.select(0);
				axesBox2.select(1);
				axesBox3.select(2);
				getDefaultBoxValues();
				SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
		              protected String doInBackground() throws InterruptedException {
		                return null;
		              }
		              protected void done() {	                
		                refreshCanvas();
		              }
		            };
		            worker.execute();
			}
		});
		optionPanel.add(defaultBtn, cc.xy(14, 2));
		// Start Java 3D graphics configuration.
		try{
			GraphicsConfiguration graphConfig = SimpleUniverse.getPreferredConfiguration();
			canvas3D = new Canvas3D(graphConfig);
			canvas3D.setSize(100,100);
			
			// SimpleUniverse
			simpleUni = new SimpleUniverse(canvas3D);
			
	/*		// Workaround for the PixelFormatError of Java3D
			SimpleUniverse.addRenderingErrorListener(new RenderingErrorListener() {
				
				public void errorOccurred(RenderingError arg0) {
					SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
			              protected String doInBackground() throws InterruptedException {
			                return null;
			              }
			              protected void done() {	                
			                refreshCanvas();
			              }
			            };
			            worker.execute();								
				}
			});*/
		}
		catch (Error e){
			JOptionPane.showMessageDialog(new JFrame(),"Java3D is not installed\nDownload at http://java3d.java.net/\n");
			this.dispose();
			return;
		}		
		catch (Exception e){
			JOptionPane.showMessageDialog(new JFrame(),"Java3D is not installed\nDownload at http://java3d.java.net/\n");
			this.dispose();
			return;
		}

		BranchGroup newBranchGroup = createSceneGraph();	
		branchGroup = newBranchGroup;
		
		newBranchGroup.compile();
		
		// Zooming out		
		canvas3D.getView().setWindowEyepointPolicy(View.RELATIVE_TO_WINDOW);
		canvas3D.getView().setProjectionPolicy(View.PARALLEL_PROJECTION);
		canvas3D.getView().setFieldOfView(0.15);
	
		simpleUni.getViewingPlatform().setNominalViewingTransform();
		simpleUni.addBranchGraph(newBranchGroup);
		
		
		// Frame Settings
		this.addWindowListener(new ClusterWindowListener(this, seqPainter));		

		// Add the canvas to the clusterPanel
		canvas3D.repaint();
		clusterPanel.add(canvas3D, BorderLayout.CENTER);
		
		// Build the legendPanel
		legendPanel = new JPanel(new FormLayout("center:p:grow, center:p:grow, ", "5dlu, center:p:grow, 5dlu, center:p:grow, 5dlu"));
		subFamiliesLbl = new JLabel("Proteins/Subfamilies");
		subFamiliesLbl.setFont(new Font("Verdana", Font.BOLD, 14));
		subFamiliesLbl.setForeground(Color.RED);
		residuesLbl = new JLabel("Residues ("+Constants.DEFAULT_RESIDUE_COLOR_NAME+")");
		clusterCentersLbl = new JLabel("Cluster Centers ("+Constants.DEFAULT_CLUSTER_COLOR_NAME+")");
		clusterCentersLbl.setFont(new Font("Verdana", Font.BOLD, 14));
		clusterCentersLbl.setForeground(Color.BLACK);
		residuesLbl.setFont(new Font("Verdana", Font.BOLD, 14));
		residuesLbl.setForeground(Color.BLUE);
		legendPanel.add(subFamiliesLbl, cc.xy(1, 2));
		legendPanel.add(residuesLbl, cc.xy(2, 2));
		legendPanel.add(clusterCentersLbl, cc.xy(2, 4));
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(optionPanel, BorderLayout.NORTH);
		this.getContentPane().add(clusterPanel, BorderLayout.CENTER);
		this.getContentPane().add(legendPanel, BorderLayout.SOUTH);
		this.setMinimumSize(new Dimension(Constants.JAVA3DFRAME_WIDTH, Constants.JAVA3DFRAME_HEIGHT));
		this.setPreferredSize(new Dimension(Constants.JAVA3DFRAME_WIDTH, Constants.JAVA3DFRAME_HEIGHT));
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.pack();
		this.validate();
		this.repaint();
		this.setVisible(true);
		seqPainter.register(this);
	}
	
	/**
	 * This method gets called when the user changes the value of a combobox.
	 * @param e
	 */
	public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
        	collectBoxValues();        	
			
			SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
	              protected String doInBackground() throws InterruptedException {
	                return null;
	              }
	              protected void done() {	                
	            	  repaintClusters(branchGroup);
	              }
	            };
	            worker.execute();			
        } 
    }
	
	/**
	 * This method repaints the canvas, recreating the containing objects to get initial position.
	 */	
	private void refreshCanvas(){
		BranchGroup newBranchGroup = createSceneGraph();
		newBranchGroup.compile();
		
		simpleUni.getAllLocales();
		branchGroup.detach();
		branchGroup = newBranchGroup;
		simpleUni.getViewingPlatform().setNominalViewingTransform();
		simpleUni.addBranchGraph(newBranchGroup);

		canvas3D.repaint();
	}
	
	/**
	 * This method get the values specified on axis checkboxes.
	 */
	private void collectBoxValues(){
		boxValues = new int[3];
		boxValues[0] = axesBox1.getSelectedIndex();
		boxValues[1] = axesBox2.getSelectedIndex();
		boxValues[2] = axesBox3.getSelectedIndex();
	}

	/**
	 * This method receives a protein name and gets his assigned color from s3det algorithm and defined constants.
	 * 
	 * @param proteinName Name of the sequence
	 * @return Color assigned to the given sequence
	 * 
	 */
	private Color getColorForProteinName(String proteinName){
		int index = name2ClusterIndex.get(proteinName);
		ClusterGroup[] clusterGroups = seqOrderer.getClusterGroups();
		ClusterGroup clGroup = clusterGroups[index];		
		return clGroup.getColor();	
	}
	
	/** 
	 * This method receives a protein name and gets his assigned color from s3det algorithm and defined constants.
	 */
	public BranchGroup createSceneGraph() {
		
		// Create the objec root branch group
		BranchGroup objRoot = new BranchGroup();
	 
		
	    objRoot.setCapability( BranchGroup.ALLOW_DETACH );
	    objRoot.setCapability( BranchGroup.ALLOW_CHILDREN_READ );
	    TransformGroup objRotate = null;
	    MouseRotate myMouseRotate = null;
	    Transform3D transform = new Transform3D();
		
		// Initialize Min + Max values for the coordinates
		min_x = min_y = min_z = MAX_VALUE;
		max_x = max_y = max_z = -MAX_VALUE;
		calculateScaleFactor();
		// Create points groups and shape
		Shape3D seqCoordShape = getSequencesShape(); 
		Shape3D resCoordShape = getResiduesShape();
		Shape3D selectedResCoordShape = getSelectedResiduesShape();
		Shape3D clusCoordShape = getClusterShape();
		
		
		// Transformation grupo 1		    
		transform = new Transform3D();
		transform.setTranslation(new Vector3d(-CLUSTERTRANS, 0.0f, 0.0f));
		objRotate = new TransformGroup(transform);
		objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		objRotate.setCapability(TransformGroup.ALLOW_PICKABLE_READ);
		objRotate.setCapability(TransformGroup.ALLOW_PICKABLE_WRITE);
		objRotate.setCapability(TransformGroup.ENABLE_PICK_REPORTING);		
		objRotate.setCapability(TransformGroup.ALLOW_CHILDREN_READ);

		objRoot.addChild(objRotate);
		objRotate.addChild(seqCoordShape);	
		objRotate.addChild(createAxis());
		
	    myMouseRotate = new MouseRotate();
	    myMouseRotate.setTransformGroup(objRotate);
	    myMouseRotate.setSchedulingBounds(new BoundingSphere());
	    objRoot.addChild(myMouseRotate);
	    
	    PickPointsBehavior behavior = new PickPointsBehavior (canvas3D, objRoot);
		behavior.setSchedulingBounds (new BoundingSphere(new Point3d(0.0d, 0.0d, 0.0d), 2));
		objRoot.addChild (behavior);
	    
	    // Add background
	    BoundingSphere bounds = new BoundingSphere(new Point3d(), 100.0);    
	    Background bg = new Background(new Color3f(1.0f, 1.0f, 1.0f));
	    bg.setApplicationBounds(bounds);	    
	    objRotate.addChild(bg);

	    //Posicionarse para el grupo 2
	    transform.setTranslation(new Vector3f(CLUSTERTRANS, 0.0f, 0.0f));
	    
		// Transformation grupo 2	    
	    objRotate = new TransformGroup(transform);
	    objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	    objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);    
		objRotate.setCapability(TransformGroup.ALLOW_PICKABLE_READ);
		objRotate.setCapability(TransformGroup.ALLOW_PICKABLE_WRITE);
		objRotate.setCapability(TransformGroup.ENABLE_PICK_REPORTING);		
		objRotate.setCapability(TransformGroup.ALLOW_CHILDREN_READ);		

		
	    objRoot.addChild(objRotate);
	    objRotate.addChild(clusCoordShape);
		objRotate.addChild(resCoordShape);
		objRotate.addChild(selectedResCoordShape);
		
		objRotate.addChild(createAxis());
		
	    myMouseRotate = new MouseRotate();
	    myMouseRotate.setTransformGroup(objRotate);
	    myMouseRotate.setSchedulingBounds(new BoundingSphere());
	    objRoot.addChild(myMouseRotate);
	    objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
	    objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
	    //Compile and return
	    objRoot.compile();	    
		return objRoot;
	}

	/**
	 * This method repaints the clusters and its points, without recreating them to conserve rotation.
	 * 
	 * @param objRoot Branchgroup where the clusters are drawn
	 */
	public void repaintClusters(BranchGroup objRoot) {
		Enumeration rootChildren = objRoot.getAllChildren();
		Vector<TransformGroup> vRootChildren = new Vector<TransformGroup>();
		while(rootChildren.hasMoreElements()){
			Object childRoot = rootChildren.nextElement();
			if(childRoot instanceof TransformGroup){
				vRootChildren.add((TransformGroup) childRoot);				
			}
		}
		calculateScaleFactor();
		for(int i =0;i<vRootChildren.size();i++ ){
			rootChildren = vRootChildren.get(i).getAllChildren();
			while(rootChildren.hasMoreElements()){
				Object childRoot = rootChildren.nextElement();
				if(childRoot instanceof Shape3D){
					Shape3D shape =(Shape3D) childRoot;
					if(shape.getUserData()!= null){					
						if(shape.getUserData().equals(Constants.SEQUENCE_SHAPE_NAME)){
							shape.setGeometry(getSeqPoints());
						}
						else if(shape.getUserData().equals(Constants.RESIDUE_SHAPE_NAME)){
							shape.setGeometry(getResPoints());
						}
						else if(shape.getUserData().equals(Constants.CLUSTER_SHAPE_NAME)){
							shape.setGeometry(getClusterPoints());
						}
						else if(shape.getUserData().equals(Constants.SELECTED_RESIDUE_SHAPE_NAME)){
							shape.setGeometry(getSelectedResPoints());
						}
					}
				}
			}
		}
	}
	
	/**
	 * This method change colors of the cluster points, to mark selected points.
	 * 
	 * @param objRoot Branchgroup where the clusters are drawn
	 */
	public void updateColors(BranchGroup objRoot) {
		Enumeration rootChildren = objRoot.getAllChildren();
		Vector<TransformGroup> vRootChildren = new Vector<TransformGroup>();
		while(rootChildren.hasMoreElements()){
			Object childRoot = rootChildren.nextElement();
			if(childRoot instanceof TransformGroup){
				vRootChildren.add((TransformGroup) childRoot);				
			}
		}
		
		for(int i =0;i<vRootChildren.size();i++ ){
			rootChildren = vRootChildren.get(i).getAllChildren();
			while(rootChildren.hasMoreElements()){
				Object childRoot = rootChildren.nextElement();
				if(childRoot instanceof Shape3D){
					Shape3D shape =(Shape3D) childRoot;
					if(shape.getUserData()!= null) {
						if(shape.getUserData().equals(Constants.SEQUENCE_SHAPE_NAME)){
							setSeqPointsColor((PointArray)shape.getGeometry());
						}
						else if(shape.getUserData().equals(Constants.RESIDUE_SHAPE_NAME)){
							setResPointsColor((PointArray)shape.getGeometry());
						}
						else if(shape.getUserData().equals(Constants.SELECTED_RESIDUE_SHAPE_NAME)){
							shape.setGeometry(getSelectedResPoints());
						}					
					}
				}
			}
		}
	}
	
	private void setSeqPointsColor(PointArray  seqCoordPoints){
	    // Sequence Coordinates	    
	    SequenceCoordinates[] seqCoords = s3detobj.getSeqCoords();
	    int seqCoordNumber = seqCoords.length;
	   
		for (int i = 0; i < seqCoordNumber; i++) {
			if(SequenceManager.isSelected(seqCoords[i].getName())){				
				seqCoordPoints.setColor(i, selProteinColor);
			}
			else{
				seqCoordPoints.setColor(i, new Color3f(getColorForProteinName(seqCoords[i].getName())));
			}
		}
		return;
	}
	
	private PointArray getSeqPoints(){
	    // Sequence Coordinates	    
	    SequenceCoordinates[] seqCoords = s3detobj.getSeqCoords();
	    int seqCoordNumber = seqCoords.length;
	    
		Point3f[] seqPoints = new Point3f[seqCoordNumber];
		PointArray seqCoordPoints = new PointArray(seqCoordNumber, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
	   
		for (int i = 0; i < seqCoordNumber; i++) {
			float x = (float) seqCoords[i].getCoords()[boxValues[0]]*scaleFactor;
			float y = (float) seqCoords[i].getCoords()[boxValues[1]]*scaleFactor;
			float z = (float) seqCoords[i].getCoords()[boxValues[2]]*scaleFactor;
			
			seqPoints[i] = new Point3f(x, y, z);			
			seqCoordPoints.setCoordinate(i, new Point3f(x, y, z));
			if(SequenceManager.isSelected(seqCoords[i].getName())){
				seqCoordPoints.setColor(i, selProteinColor);
			}
			else{
				seqCoordPoints.setColor(i, new Color3f(getColorForProteinName(seqCoords[i].getName())));
			}
		}
		seqCoordPoints.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
		seqCoordPoints.setCapability(GeometryArray.ALLOW_VERTEX_ATTR_READ);
		seqCoordPoints.setCapability(GeometryArray.ALLOW_COUNT_READ);
		seqCoordPoints.setCapability(GeometryArray.ALLOW_FORMAT_READ);
		seqCoordPoints.setCapability(GeometryArray.ALLOW_COORDINATE_READ);		
		return seqCoordPoints;
	}
	
	private Shape3D getSequencesShape(){

		Appearance seqCoordApp = new Appearance();
		PointArray seqCoordPoints = getSeqPoints();
		PointAttributes pa = new PointAttributes();		
		pa.setPointSize(Constants.SEQUENCE_POINT_SIZE);
		seqCoordApp.setPointAttributes(pa);
		Shape3D shape = new Shape3D(seqCoordPoints, seqCoordApp);
		shape.setUserData(Constants.SEQUENCE_SHAPE_NAME);
		shape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);		
		shape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
		return shape;
	}	
	
	
	private void setResPointsColor(PointArray  resCoordPoints){	    	    
		ResidueCoordinates[] resCoords = s3detobj.getResCoords();
		int resCoordNumber = resCoords.length;
	   
		for (int i = 0; i < resCoordNumber; i++) {			
			if(ResidueSelectionManager.contains(resCoords[i].getPosition(), resCoords[i].getLetter()) || SelectionManager.contains(resCoords[i].getPosition())){
				resCoordPoints.setColor(i, selResidueColor);				
			}
			else{
				resCoordPoints.setColor(i, new Color3f(Constants.DEFAULT_RESIDUE_COLOR));
			}
		}
		return;
	}

	private PointArray getResPoints(){
		// Residue Coordinates	    
	    ResidueCoordinates[] resCoords = s3detobj.getResCoords();
	    int resCoordNumber = resCoords.length;
	    
		Point3f[] resPoints = new Point3f[resCoordNumber];
		PointArray resCoordPoints = new PointArray(resCoordNumber, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
		for (int i = 0; i < resCoordNumber; i++) {			
			float x = (float) resCoords[i].getCoords()[boxValues[0]]*scaleFactor;
			if(x < min_x) min_x = x; 
			if(x > max_x) max_x = x; 
			float y = (float) resCoords[i].getCoords()[boxValues[1]]*scaleFactor;
			if(y < min_y) min_y = y;
			if(y > max_y) max_y = y;
			float z = (float) resCoords[i].getCoords()[boxValues[2]]*scaleFactor;
			if(z < min_z) min_z = z;
			if(z > max_z) max_z = z;
			resPoints[i] = new Point3f(x, y, z);
			if(ResidueSelectionManager.contains(resCoords[i].getPosition(), resCoords[i].getLetter()) || SelectionManager.contains(resCoords[i].getPosition())){
				resCoordPoints.setColor(i, selResidueColor);				
			}
			else{
				resCoordPoints.setColor(i, new Color3f(Constants.DEFAULT_RESIDUE_COLOR));
			}
			resCoordPoints.setCoordinate(i, resPoints[i]);
		}
		resCoordPoints.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
		resCoordPoints.setCapability(GeometryArray.ALLOW_VERTEX_ATTR_READ);
		resCoordPoints.setCapability(GeometryArray.ALLOW_COUNT_READ);	
		resCoordPoints.setCapability(GeometryArray.ALLOW_FORMAT_READ);
		resCoordPoints.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
		return resCoordPoints;
	}
	
	private Shape3D getResiduesShape(){
	
		PointArray resCoordPoints= getResPoints();
		Appearance resCoordApp = new Appearance();
		PointAttributes pa = new PointAttributes();
		pa.setPointSize(Constants.RESIDUE_POINT_SIZE);
		resCoordApp.setPointAttributes(pa);
		Shape3D shape = new Shape3D(resCoordPoints, resCoordApp);
		shape.setUserData(Constants.RESIDUE_SHAPE_NAME);
		shape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
		shape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
		return shape;
	}
	
	
	private PointArray getSelectedResPoints(){
		// Residue Coordinates	    
	    ResidueCoordinates[] resCoords = s3detobj.getResCoords();
	    int resCoordNumber = resCoords.length;
	    
	    
		
		Point3f[] resPoints = new Point3f[resCoordNumber];
		PointArray resCoordPoints = new PointArray(resCoordNumber, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
		for (int i = 0; i < resCoordNumber; i++) {			
			if(ResidueSelectionManager.contains(resCoords[i].getPosition(), resCoords[i].getLetter()) || SelectionManager.contains(resCoords[i].getPosition())){			
				float x = (float) resCoords[i].getCoords()[boxValues[0]]*scaleFactor;
				float y = (float) resCoords[i].getCoords()[boxValues[1]]*scaleFactor;
				float z = (float) resCoords[i].getCoords()[boxValues[2]]*scaleFactor;
				resPoints[i] = new Point3f(x, y, z);
				resCoordPoints.setColor(i, selResidueColor);
				resCoordPoints.setCoordinate(i, resPoints[i]);				
			}
		}
		resCoordPoints.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
		resCoordPoints.setCapability(GeometryArray.ALLOW_VERTEX_ATTR_READ);
		resCoordPoints.setCapability(GeometryArray.ALLOW_COUNT_READ);
		resCoordPoints.setCapability(GeometryArray.ALLOW_FORMAT_READ);		
		resCoordPoints.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
		return resCoordPoints;
	}
	
	private Shape3D getSelectedResiduesShape(){
		PointArray resCoordPoints= getSelectedResPoints();
		Appearance resCoordApp = new Appearance();
		PointAttributes pa = new PointAttributes();
		pa.setPointSize(Constants.SELECTED_RESIDUE_POINT_SIZE);
		resCoordApp.setPointAttributes(pa);
		Shape3D shape = new Shape3D(resCoordPoints, resCoordApp);
		shape.setUserData(Constants.SELECTED_RESIDUE_SHAPE_NAME);
		shape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
		shape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
		return shape;
	}	
	
	
	private PointArray getClusterPoints(){
		// Cluster Coordinates	    
	    ClusterCoordinates[] clusCoords = s3detobj.getClusterCoords();
	    int clusCoordNumber = clusCoords.length;
	    
		Point3f[] clusPoints = new Point3f[clusCoordNumber];
		PointArray clusCoordPoints = new PointArray(clusCoordNumber, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
		ClusterGroup[] clGroups = seqOrderer.getClusterGroups();
		for (int i = 0; i < clusCoordNumber; i++) {
			float x = (float) clusCoords[i].getCoords()[boxValues[0]]*scaleFactor;
			float y = (float) clusCoords[i].getCoords()[boxValues[1]]*scaleFactor;
			float z = (float) clusCoords[i].getCoords()[boxValues[2]]*scaleFactor;			
			clusPoints[i] = new Point3f(x + CLUSTERTRANS, y, z);
			clusCoordPoints.setCoordinate(i, new Point3f(x, y, z));
			if(i<clGroups.length){
				clusCoordPoints.setColor(i,new Color3f(clGroups[i].getColor()));
			}
			else{
				clusCoordPoints.setColor(i,new Color3f(Constants.DEFAULT_CLUSTER_COLOR));
			}
		}
		return clusCoordPoints;
	}

		
	private Shape3D getClusterShape(){
		PointArray clusCoordPoints = getClusterPoints();
		
		Appearance clusCoordApp = new Appearance();
		
		PointAttributes pa = new PointAttributes();
		
		pa.setPointSize(Constants.CLUSTER_POINT_SIZE);
		clusCoordApp.setPointAttributes(pa);
		
		Shape3D shape = new Shape3D(clusCoordPoints, clusCoordApp);
		shape.setUserData(Constants.CLUSTER_SHAPE_NAME);
		shape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
		shape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
		return shape;
		
		
	}
	
	private Group createAxis(){
		ColoringAttributes axisColor = new ColoringAttributes(new Color3f(Constants.DEFAULT_AXIS_COLOR), ColoringAttributes.SHADE_FLAT);
		Group lineGroup = new Group();
		// x-axis
	    Point3f[] x_axis = new Point3f[2];	    
	    x_axis[0] = new Point3f(min_x, 0.0f, 0.0f);
	    x_axis[1] = new Point3f(max_x, 0.0f, 0.0f);
	    LineArray xArray = new LineArray(2, GeometryArray.COORDINATES);
	    xArray.setCapability(GeometryArray.ALLOW_COUNT_READ);
	    xArray.setCoordinates(0, x_axis);
	    // y-axis
	    Point3f[] y_axis = new Point3f[2];	    
	    y_axis[0] = new Point3f(0.0f, min_y, 0.0f);
	    y_axis[1] = new Point3f(0.0f, max_y, 0.0f);
	    LineArray yArray = new LineArray(2, GeometryArray.COORDINATES);
	    yArray.setCapability(GeometryArray.ALLOW_COUNT_READ);
	    yArray.setCoordinates(0, y_axis);	    
		// z-axis
	    Point3f[] z_axis = new Point3f[2];	    
	    z_axis[0] = new Point3f(0.0f, 0.0f, max_z);
	    z_axis[1] = new Point3f(0.0f, 0.0f, min_z);
	    LineArray zArray = new LineArray(2, GeometryArray.COORDINATES);
	    zArray.setCapability(GeometryArray.ALLOW_COUNT_READ);
	    
	    
	    zArray.setCoordinates(0, z_axis);
	    LineAttributes dotDashLa = new LineAttributes();
	    dotDashLa.setLineWidth(1.0f);
	    dotDashLa.setLinePattern(LineAttributes.PATTERN_DASH);
	    Appearance dotDashApp = new Appearance();
	    dotDashApp.setLineAttributes(dotDashLa);
	    dotDashApp.setColoringAttributes(axisColor);
	    Shape3D xLine = new Shape3D(xArray, dotDashApp);
	    Shape3D yLine = new Shape3D(yArray, dotDashApp);
	    Shape3D zLine = new Shape3D(zArray, dotDashApp);
	    
	    lineGroup.addChild(xLine);
	    lineGroup.addChild(yLine);
	    lineGroup.addChild(zLine);
	    return lineGroup;
	}
	
	/**
	 * This methods sets the default values to the 1st, 2nd and 3rd axis.
	 */
	private void getDefaultBoxValues(){		
		boxValues = new int[3];
		boxValues[0] = 0;
		boxValues[1] = 1;
		boxValues[2] = 2;
	}
	/**
	 * Updates the data from the observable object.
	 */
	public void update(Observable object) {
		if (object instanceof SequencePainter){
			updateColors(branchGroup);
		}
	}
	public void enablePicking(Node node) {

	    node.setPickable(true);

	    node.setCapability(Node.ENABLE_PICK_REPORTING);

	    try {
	       Group group = (Group) node;
	       for (Enumeration e = group.getAllChildren(); e.hasMoreElements();) {
	          enablePicking((Node)e.nextElement());
	       }
	    }

	    catch(ClassCastException e) {
	        // if not a group node, there are no children so ignore exception
	    }

	    try {
	          Shape3D shape = (Shape3D) node;
	          PickTool.setCapabilities(node, PickTool.INTERSECT_FULL);
	          for (Enumeration e = shape.getAllGeometries(); e.hasMoreElements();) {
	             Geometry g = (Geometry)e.nextElement();
	             g.setCapability(Geometry.ALLOW_INTERSECT);
	          }
	       }

	    catch(ClassCastException e) {

	       // not a Shape3D node ignore exception

	    }
	}
	
	private void calculateScaleFactor(){
		highestValue = MAX_VALUE;
	    ResidueCoordinates[] resCoords = s3detobj.getResCoords();
		for (int i = 0; i < resCoords.length; i++) {			
			float x = (float) resCoords[i].getCoords()[boxValues[0]];
			if(x > highestValue) highestValue = x; 
			float y = (float) resCoords[i].getCoords()[boxValues[1]];
			if(y > highestValue) highestValue = y; 
			float z = (float) resCoords[i].getCoords()[boxValues[2]];
			if(z > highestValue) highestValue = z;
		}
	    SequenceCoordinates[] seqCoords = s3detobj.getSeqCoords();
		for (int i = 0; i < seqCoords.length; i++) {			
			float x = (float) seqCoords[i].getCoords()[boxValues[0]];
			if(x > highestValue) highestValue = x; 
			float y = (float) seqCoords[i].getCoords()[boxValues[1]];
			if(y > highestValue) highestValue = y; 
			float z = (float) seqCoords[i].getCoords()[boxValues[2]];
			if(z > highestValue) highestValue = z;
		}
		
		scaleFactor = MAX_VALUE/highestValue;
	}	
}