package test;


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import model.AlignObject;
import model.Methods;
import model.XDetObject;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.BusinessBlackSteelSkin;

import parser.MfaParser;
import parser.XDetParser;
import util.Constants;
import view.AlignFrame;

public class AlignViewTest{
	AlignFrame alignFrame;
    public static void main(String[] args) {
    	JFrame.setDefaultLookAndFeelDecorated(true);
    	
    	 SwingUtilities.invokeLater(new Runnable() {
    	      public void run() {
    	    	  SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin());
    	    	  AlignViewTest test = new AlignViewTest();
    	          test.createAndShowGUI();
    	      }
    	    });
    }
   
    private void createAndShowGUI() {
    	
    	MfaParser parser = new MfaParser();
		//AlignObject alignObject = parser.read("C:/PF00202.mfa");    		
    	//AlignObject alignObject = parser.read("C:/TIMbarrel-hydrolase.fasta");
    	AlignObject alignObject = parser.read("/usr/people/tmuth/PF00202.mfa");
    	XDetParser xdetparser = new XDetParser();
		XDetObject xdetObject = xdetparser.read("/usr/people/tmuth/PF00202.Xdet",alignObject);
		Methods methods = new Methods();
		methods.setxDetUsed(true);
		methods.setEntropyUsed(true);
		methods.setxDetObject(xdetObject);
		
        alignFrame = new AlignFrame(alignObject, methods, Constants.DEFAULT_WIDTH,	Constants.DEFAULT_HEIGHT, true);
        alignFrame.pack();
        alignFrame.setVisible(true);
        
        alignFrame.addWindowListener(new WindowAdapter()
        {
            @Override
			public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });
    }
   
}

