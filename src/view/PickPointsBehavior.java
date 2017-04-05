package view;

import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.media.j3d.Appearance;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingPolytope;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.PickBounds;
import javax.media.j3d.PickCylinderRay;
import javax.media.j3d.PickShape;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.QuadArray;
import javax.media.j3d.SceneGraphPath;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOr;
import javax.vecmath.Color3f;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import util.Constants;
import util.ResidueSelectionManager;
import util.SelectionManager;
import util.SequenceManager;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickIntersection;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;


/**
 * This class defines the behavior needed for selecting items on Cluster3DFrame 
 * @author Juan Antonio García
 */
public class PickPointsBehavior extends Behavior {

    private Canvas3D canvas;
    private BranchGroup objRoot;
    private BranchGroup quadBranch;

    private WakeupCriterion[] mouseEvents;
    private WakeupOr mouseCriterion;

    private Point3d initialPoint, finalPoint;
    private Point2d initialPoint2d, finalPoint2d;

    private boolean mousePressed = false;
    private boolean mouseDragged = false;

    private PickCanvas pickCanvas;
    private int x,y;

    public PickPointsBehavior (Canvas3D canvas, BranchGroup objRoot) {
    	this.canvas = canvas;
    	this.objRoot = objRoot;

	    pickCanvas = new PickCanvas(canvas, objRoot);
	    pickCanvas.setMode(PickTool.GEOMETRY_INTERSECT_INFO);
	    pickCanvas.setTolerance(.5f);
	}

	public void initialize() {
		initialPoint = new Point3d();
		finalPoint = new Point3d();
		initialPoint2d = new Point2d();
		finalPoint2d = new Point2d();
		
		mouseEvents = new WakeupCriterion[4];		
	    mouseEvents[0] = new WakeupOnAWTEvent(MouseEvent.MOUSE_CLICKED);
	    mouseEvents[1] = new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED);
	    mouseEvents[2] = new WakeupOnAWTEvent(MouseEvent.MOUSE_RELEASED);
	    mouseEvents[3] = new WakeupOnAWTEvent(MouseEvent.MOUSE_DRAGGED);	    
		mouseCriterion = new WakeupOr(mouseEvents);
	    wakeupOn (mouseCriterion);
	}

	public void processStimulus (Enumeration criteria) {
		WakeupCriterion wakeup;
		AWTEvent[] event;
		int id;
		int isShiftPressed = 0;
		while (criteria.hasMoreElements()) {
		    wakeup = (WakeupCriterion) criteria.nextElement();
		    if (wakeup instanceof WakeupOnAWTEvent) {
				event = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
				for (int i=0; i<event.length; i++) {
					if(event[i] instanceof MouseEvent){						
						isShiftPressed = ((MouseEvent) event[i]).getModifiers() & MouseEvent.SHIFT_MASK;
					}
					
					if(isShiftPressed == 0){
						if (quadBranch != null) {
						    objRoot.removeChild (quadBranch);
						    quadBranch = null;
						}
						setRotation(true);
			    	}
					else{
						setRotation(false);
					}
					
				    id = event[i].getID();
				    if (isShiftPressed != 0 && id == MouseEvent.MOUSE_CLICKED) {
						mousePressed = true;
						x = ((MouseEvent)event[i]).getX();
						y = ((MouseEvent)event[i]).getY();
		
						initialPoint2d.set(x, y);
					    mousePressed = false;
					    mouseDragged = false;
				    	performPointPicking();
					    SelectionManager.updateSeqPainter();
					}
				    else if (isShiftPressed != 0 && id == MouseEvent.MOUSE_DRAGGED) {
				    	if (!mousePressed){
						    continue;
						}
						mouseDragged = true;
						x = ((MouseEvent)event[i]).getX();
						y = ((MouseEvent)event[i]).getY();
						
						finalPoint2d.set(x, y);
						drawPickingArea();
				    }			    
					else if (isShiftPressed != 0 && id == MouseEvent.MOUSE_PRESSED) {
				    		setRotation(false);
				    		mousePressed = true;
							x = ((MouseEvent)event[i]).getX();
							y = ((MouseEvent)event[i]).getY();
			
							initialPoint2d.set(x, y);
				    }
				    else if (id == MouseEvent.MOUSE_RELEASED) {				    	
				    	if(isShiftPressed != 0){
							if (!mouseDragged) {
							    mousePressed = false;
							    mouseDragged = false;				
							
							} else {
							    mousePressed = false;
							    mouseDragged = false;
				
							    x = ((MouseEvent)event[i]).getX();
							    y = ((MouseEvent)event[i]).getY();
				
							    
								finalPoint2d.set(x, y);
				
							    performBoundsPicking();
							    if (quadBranch != null) {
								    objRoot.removeChild (quadBranch);
								    quadBranch = null;
								}
							    SelectionManager.updateSeqPainter();
							}
				    	}
				    }
				}
		    }
		}
		    
		
		wakeupOn (mouseCriterion);
	}

   /**
    * 0. Remove any previously drawn quadrilaterals.
    * 1. If mouse has been just clicked, then use the initialPoint
    * saved to generate a pickRay and perform single point picking.
    */
    private void performPointPicking() {
	
		
		
		if (quadBranch != null) {
		    objRoot.removeChild (quadBranch);
		    quadBranch = null;
		}
		
		canvas.getPixelLocationInImagePlate(x, y, initialPoint);
		Transform3D transform3D = new Transform3D();
		canvas.getImagePlateToVworld(transform3D);
		transform3D.transform (initialPoint);
		
		
		Vector3d direction = new Vector3d (0, 0, -1);
		PickShape pickRay = new PickCylinderRay (initialPoint, direction, Constants.SELECTION_PRECISION);
		SceneGraphPath[] sgp = objRoot.pickAll (pickRay);
		if (sgp != null) {
		    for (int l=0; l<sgp.length; l++) {
				PickResult pickResult = new PickResult (sgp[l], pickRay);
				
				if (pickResult != null) {
				    Shape3D shape = (Shape3D) pickResult.getNode(PickResult.SHAPE3D);
				    if(shape.getUserData() != null && (shape.getUserData().equals(Constants.SEQUENCE_SHAPE_NAME) || shape.getUserData().equals(Constants.RESIDUE_SHAPE_NAME))){
				    	int numIntersections = pickResult.numIntersections();
					    for (int i=0; i<numIntersections; i++) {
							PickIntersection intersection = pickResult.getIntersection(i);
							javax.media.j3d.GeometryArray ga = intersection.getGeometryArray();
							int[] primitiveVertexIndices = intersection.getPrimitiveVertexIndices();
							if (primitiveVertexIndices != null) {
							    for (int j=0; j<primitiveVertexIndices.length; j++) {
									Point3f point = new Point3f();
									ga.getCoordinate (primitiveVertexIndices[j], point);
					//				System.out.println ("\t\t\tindex="+primitiveVertexIndices[j]+", point="+point);
									if (shape.getUserData()!= null && shape.getUserData().equals(Constants.SEQUENCE_SHAPE_NAME)){
										SequenceManager.addSequenceByPosition(primitiveVertexIndices[j]);
									}
									else{
										ResidueSelectionManager.addResidueByPosition(primitiveVertexIndices[j]);
									}
							    }
							}
					    }
				    }
				} 
		    }
		} 
    }

    private void drawPickingArea() {
		// Remove any previously drawn quadrilaterals.
		if (quadBranch != null) {
		    objRoot.removeChild (quadBranch);
		    quadBranch = null;
		}
		
		Point2d drawInitialPoint = new Point2d(Math.min(initialPoint2d.x,finalPoint2d.x),Math.min(initialPoint2d.y,finalPoint2d.y));
		Point2d drawFinalPoint = new Point2d(Math.max(initialPoint2d.x,finalPoint2d.x),Math.max(initialPoint2d.y,finalPoint2d.y));
		
		Point3d markedPoint = new Point3d();
		Transform3D transform3D = new Transform3D();
	    canvas.getImagePlateToVworld(transform3D);
	    
		// Draw a quadrilateral with endoints at initalPoint and finalPoint
		QuadArray qa = new QuadArray (4, QuadArray.COORDINATES);
	    canvas.getPixelLocationInImagePlate(((Double)drawInitialPoint.x).intValue(),((Double)drawInitialPoint.y).intValue(), markedPoint);
	    transform3D.transform (markedPoint);
		qa.setCoordinate (0, markedPoint);
	    canvas.getPixelLocationInImagePlate(((Double)drawInitialPoint.x).intValue(),((Double)drawFinalPoint.y).intValue(), markedPoint);
	    transform3D.transform (markedPoint);		
		qa.setCoordinate (1, markedPoint);
	    canvas.getPixelLocationInImagePlate(((Double)drawFinalPoint.x).intValue(),((Double)drawFinalPoint.y).intValue(), markedPoint);
	    transform3D.transform (markedPoint);
		markedPoint = new Point3d (markedPoint);
		qa.setCoordinate (2, markedPoint);
	    canvas.getPixelLocationInImagePlate(((Double)drawFinalPoint.x).intValue(),((Double)drawInitialPoint.y).intValue(), markedPoint);
	    transform3D.transform (markedPoint);
		qa.setCoordinate (3, markedPoint);
		
		
	
		
		Appearance appearance = new Appearance();
		PolygonAttributes pa = new PolygonAttributes ();
		pa.setPolygonMode (PolygonAttributes.POLYGON_LINE);
		appearance.setPolygonAttributes (pa);
		LineAttributes la = new LineAttributes();
		la.setLinePattern (LineAttributes.PATTERN_DASH);
		la.setLineAntialiasingEnable (true); 
		appearance.setLineAttributes (la);
		ColoringAttributes ca = new ColoringAttributes ();
		ca.setColor (new Color3f (Constants.PICK_AREA_COLOR));
		appearance.setColoringAttributes (ca);
			
		Shape3D s = new Shape3D (qa, appearance);
		quadBranch = new BranchGroup ();
		quadBranch.setCapability (BranchGroup.ALLOW_DETACH);
		quadBranch.addChild(s);
		objRoot.addChild (quadBranch);
    }
    
    /**
    * 0. Remove any previously drawn quadrilaterals.
    * 1. Draw a quadrilateral with endoints at initalPoint and finalPoint
    * 2. Try picking using a BoundingBox to get all points in this range
    */
    private void performBoundsPicking() {

		Point2d drawInitialPoint = new Point2d(Math.min(initialPoint2d.x,finalPoint2d.x),Math.min(initialPoint2d.y,finalPoint2d.y));
		Point2d drawFinalPoint = new Point2d(Math.max(initialPoint2d.x,finalPoint2d.x),Math.max(initialPoint2d.y,finalPoint2d.y));
		
		Transform3D transform3D = new Transform3D();
	    canvas.getImagePlateToVworld(transform3D);    
	    canvas.getPixelLocationInImagePlate(((Double)drawInitialPoint.x).intValue(),((Double)drawInitialPoint.y).intValue(), initialPoint);
	    transform3D.transform (initialPoint);
	    canvas.getPixelLocationInImagePlate(((Double)drawFinalPoint.x).intValue(),((Double)drawFinalPoint.y).intValue(), finalPoint);
	    transform3D.transform (finalPoint);
	    
    	
    	Point3d upperFrontLeft = new Point3d(initialPoint.x,initialPoint.y,initialPoint.z );
    	Point3d lowerBackRight = new Point3d(finalPoint.x,finalPoint.y,-initialPoint.z);
    	Point3d upperFrontRight = new Point3d(lowerBackRight.x,upperFrontLeft.y,upperFrontLeft.z);
    	Point3d lowerFrontRight = new Point3d(lowerBackRight.x,lowerBackRight.y,upperFrontLeft.z);
    	Point3d upperBackRight = new Point3d(lowerBackRight.x,upperFrontLeft.y,lowerBackRight.z);
    	Point3d lowerFrontLeft = new Point3d(upperFrontLeft.x,lowerBackRight.y,upperFrontLeft.z);
    	Point3d lowerBackLeft = new Point3d(upperFrontLeft.x,lowerBackRight.y,lowerBackRight.z);
    	Point3d upperBackLeft = new Point3d(upperFrontLeft.x,upperFrontLeft.y,lowerBackRight.z);
    	
    	Vector4d[] planes = new Vector4d[6];    	
    	planes[0] = planeFromPoints(upperFrontRight, upperBackRight, lowerFrontRight);
    	planes[1] = planeFromPoints(lowerFrontLeft, lowerBackLeft,upperFrontLeft);
    	planes[2] = planeFromPoints(upperFrontLeft, upperBackLeft, upperFrontRight);
    	planes[3] = planeFromPoints(lowerFrontLeft, lowerFrontRight , lowerBackLeft);
    	planes[4] = planeFromPoints(lowerFrontLeft, upperFrontLeft, lowerFrontRight);
    	planes[5] = planeFromPoints(lowerBackLeft, lowerBackRight, upperBackLeft);
    	
    	
    	BoundingPolytope selectedArea = new BoundingPolytope(planes);
		PickBounds pickBounds = new PickBounds (selectedArea);
		
		SceneGraphPath[] sgp = objRoot.pickAll(pickBounds);		
		if (sgp != null) {
		    for (int l=0; l<sgp.length; l++) {
				PickResult pickResult = new PickResult (sgp[l], pickBounds);		
				
				if (pickResult != null) {
				    Shape3D shape = (Shape3D) pickResult.getNode(PickResult.SHAPE3D);
				    if(shape.getUserData() != null && (shape.getUserData().equals(Constants.SEQUENCE_SHAPE_NAME) || shape.getUserData().equals(Constants.RESIDUE_SHAPE_NAME))){
				    int numIntersections = pickResult.numIntersections();
					    for (int i=0; i<numIntersections; i++) {
							PickIntersection intersection = pickResult.getIntersection(i);
							javax.media.j3d.GeometryArray ga = intersection.getGeometryArray();
							int[] primitiveVertexIndices = intersection.getPrimitiveVertexIndices();
							if (primitiveVertexIndices != null) {
							    for (int j=0; j<primitiveVertexIndices.length; j++) {
									Point3f point = new Point3f();
									ga.getCoordinate (primitiveVertexIndices[j], point);
					//				System.out.println ("\t\t\tindex="+primitiveVertexIndices[j]+", point="+point);
									if (shape.getUserData()!= null && shape.getUserData().equals(Constants.SEQUENCE_SHAPE_NAME)){
										SequenceManager.addSequenceByPosition(primitiveVertexIndices[j]);
									}
									else{
										ResidueSelectionManager.addResidueByPosition(primitiveVertexIndices[j]);
									}
							    }
							}
					    }
				    }
				} 
		    }
		} 
    }
    
	private void setRotation(boolean value) {
		Enumeration rootChildren = objRoot.getAllChildren();
		while(rootChildren.hasMoreElements()){
			Object childRoot = rootChildren.nextElement();
			if(childRoot instanceof MouseRotate){
				((MouseRotate) childRoot).setEnable(value);				
			}
		}
    }
    private Vector4d planeFromPoints(Point3d base, Point3d rightPt,
	    Point3d leftPt) {
	    Vector3d rightVec = new Vector3d();
	    rightVec.sub(rightPt, base);
	    Vector3d leftVec = new Vector3d();
	    leftVec.sub(leftPt, base);
	    Vector3d normal = new Vector3d();
	    // normal should point "out"
	    normal.cross(leftVec, rightVec);
	    normal.normalize();
	    Vector4d plane = new Vector4d(normal.x, normal.y, normal.z,-(normal.x * base.x + normal.y * base.y + normal.z * base.z));
	    //System.out.println("plane = " + plane);
	    return plane;
    }
}