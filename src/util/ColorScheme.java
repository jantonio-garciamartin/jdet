package util;

import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.JFileChooser;

import parser.ColorParser;
import view.AlignFrame;
import view.ColorSchemeInfo;

/**
 * This class represents the color scheme for the amino acids in the alignment.
 * @author Thilo Muth
 *
 */
public class ColorScheme {
	
	private static Vector<HashMap<Character, Color>> colorSchemes;
	private static HashMap<Integer, Double> minValues;
	private static HashMap<Integer, Double> maxValues;
	private static Vector<String> colorSchemeNames;
	private static HashMap<Character, Double> userDefSchemeValues = null;
	private static int currentScheme = 0;
	private static int colorPalette = 0;
	
	private static final long serialVersionUID = 1L;
	private static final Color RED = new Color (230, 50, 25); 
	private static final Color BLUE = new Color (25, 127, 230);
	private static final Color GREEN = new Color (25, 204, 25);
	private static final Color CYAN = new Color (25, 178, 178);
	private static final Color PINK = new Color (239, 127, 127);
	private static final Color MAGENTA = new Color (204, 77, 204);
	private static final Color ORANGE = new Color (230, 154, 77);
	private static final Color YELLOW = new Color (204, 204, 0);
	
	
	public static final String CLUSTAL_X = "ClustalX" ;
	public static final String MW = "Molecular weight" ;
	public static final String POL_G = "Polarity" ;
	public static final String HDF = "Hidrophobicity";
	public static final String USER_DEF = "User defined" ;
	
	public static final int PAL_RED_GREEN_BLUE = 0 ;
	public static final int PAL_RED_BLUE = 1;	

	public static final int PALETTE_WIDTH = 100;
	public static final int PALETTE_HEIGHT = 20;
	public static final int PALETTE_TEXT_MARGIN = 5;
	public static final double PALETTE_STEPS = 10.;
	
	/**
	 * Default Clustal X color scheme.
	 */
	public static final HashMap<Character, Color> defScheme = new HashMap<Character, Color>();
	static{
		defScheme.put('A', BLUE);
		defScheme.put('W', BLUE);
		defScheme.put('L', BLUE);
		defScheme.put('V', BLUE);
		defScheme.put('I', BLUE);
		defScheme.put('M', BLUE);	
		defScheme.put('F', BLUE);
		defScheme.put('K', RED);
		defScheme.put('R', RED);
		defScheme.put('T', GREEN);
		defScheme.put('S', GREEN);
		defScheme.put('N', GREEN);
		defScheme.put('Q', GREEN);		
		defScheme.put('C', PINK);		
		defScheme.put('D', MAGENTA);
		defScheme.put('E', MAGENTA);		
		defScheme.put('G', ORANGE);
		defScheme.put('H', CYAN);
		defScheme.put('Y', CYAN);
		defScheme.put('P', YELLOW);
	}	

	/**
	 * MW color scheme.
	 */
	public static final HashMap<Character, Double> mwScheme = new HashMap<Character, Double>(); 
	static{
		mwScheme.put('A', 89.);
		mwScheme.put('W', 204.);
		mwScheme.put('L', 131.);
		mwScheme.put('V', 117.);
		mwScheme.put('I', 131.);
		mwScheme.put('M', 149.);	
		mwScheme.put('F', 165.);
		mwScheme.put('K', 146.);
		mwScheme.put('R', 174.);
		mwScheme.put('T', 119.);
		mwScheme.put('S', 105.);
		mwScheme.put('N', 132.);
		mwScheme.put('Q', 146.);		
		mwScheme.put('C', 121.);		
		mwScheme.put('D', 133.);
		mwScheme.put('E', 147.);		
		mwScheme.put('G', 75.);
		mwScheme.put('H', 155.);
		mwScheme.put('Y', 181.);
		mwScheme.put('P', 115.);
	}
	
	/**
	 * Pol G color scheme.
	 */
	public static final HashMap<Character, Double> polScheme = new HashMap<Character, Double>(); 
	static{
		polScheme.put('A', 8.1);
		polScheme.put('W', 5.4);
		polScheme.put('L', 4.9);
		polScheme.put('V', 5.9);
		polScheme.put('I', 5.2);
		polScheme.put('M', 5.7);	
		polScheme.put('F', 5.2);
		polScheme.put('K', 11.3);
		polScheme.put('R', 10.5);
		polScheme.put('T', 8.6);
		polScheme.put('S', 9.2);
		polScheme.put('N', 11.6);
		polScheme.put('Q', 10.5);		
		polScheme.put('C', 5.5);		
		polScheme.put('D', 13.);
		polScheme.put('E', 12.3);		
		polScheme.put('G', 9.);
		polScheme.put('H', 10.4);
		polScheme.put('Y', 6.2);
		polScheme.put('P', 8.);
	}
	
	/**
	 * Hdf  color scheme.
	 */
	public static final HashMap<Character, Double> hdfScheme = new HashMap<Character, Double>();
	static{
		hdfScheme.put('A', 0.62);
		hdfScheme.put('W', 0.81);
		hdfScheme.put('L', 1.06);
		hdfScheme.put('V', 1.08);
		hdfScheme.put('I', 1.38);
		hdfScheme.put('M', 0.64);	
		hdfScheme.put('F', 1.19);
		hdfScheme.put('K', -1.5);
		hdfScheme.put('R', -2.53);
		hdfScheme.put('T', -0.05);
		hdfScheme.put('S', 0.18);
		hdfScheme.put('N', -0.78);
		hdfScheme.put('Q', -0.85);		
		hdfScheme.put('C', 0.29);		
		hdfScheme.put('D', -0.9);
		hdfScheme.put('E', -0.74);		
		hdfScheme.put('G', 0.48);
		hdfScheme.put('H', -0.4);
		hdfScheme.put('Y', 0.26);
		hdfScheme.put('P', 1.08);
	}
	
	/**
	 * Constructor of the color scheme hash map.
	 */
	public ColorScheme(){
		init();
	}
	
	public static void init(){
		if(colorSchemes== null){
			colorSchemes = new Vector<HashMap<Character, Color>>();
			colorSchemeNames = new Vector<String>();
			minValues = new HashMap<Integer,Double>();
			maxValues = new HashMap<Integer,Double>();
			colorPalette = ColorScheme.PAL_RED_GREEN_BLUE;
			addFixedScheme(defScheme, ColorScheme.CLUSTAL_X);
			setColorPalette(ColorScheme.PAL_RED_GREEN_BLUE);
		}
	}
	/**
	 * Get the color associated to residue on current color scheme
	 * @param Residue to find color
	 * @return Color associated to residue on current color scheme
	 */
	public static Color getColor(Character key){
		return colorSchemes.get(currentScheme).get(key);
	}
	
	/**
	 * Changes color pallete and reclacule colors for all numeric color schemes
	 * @param Identifier of color palette
	 */
	public static void setColorPalette(int colorPaletteCode){
		colorPalette = colorPaletteCode;
		addGradualScheme(mwScheme,ColorScheme.MW);
		addGradualScheme(polScheme,ColorScheme.POL_G);
		addGradualScheme(hdfScheme,ColorScheme.HDF);
		if(userDefSchemeValues!=null){
			ColorScheme.addGradualScheme(userDefSchemeValues, colorSchemeNames.get(currentScheme));
		}
	}

	/**
	 * Getter for colorPallete
	 * @return Identifier of color palette
	 */
	public static int getColorPalette(){
		return colorPalette;
	}
	
	/**
	 * Setter for user defined color scheme
	 * @param New user defined color scheme
	 */
	public static void setUserDefSchemeValues(HashMap<Character, Double> newUserDefSchemeValues){
		userDefSchemeValues=newUserDefSchemeValues;
	}
	
	
	/**
	 * Function to add a new color scheme.
	 * 
	 * @param colorScheme HashMap with new color scheme
	 * @param schemeName Name of the new scheme
	 */
	private static void addFixedScheme(HashMap<Character, Color> colorScheme, String schemeName){
		colorSchemes.add(colorScheme);
		colorSchemeNames.add(schemeName);
	}
	
	
	/**
	 * Function to add a new numeric color scheme.
	 * 
	 * @param colorScheme HashMap with values of new color scheme
	 * @param schemeName Name of the new scheme
	 */
	public static void addGradualScheme(HashMap<Character, Double> colorScheme, String schemeName){
		HashMap<Character, Color> newScheme = new HashMap<Character, Color>();
		Set<Character> keys = colorScheme.keySet(); 
		Iterator<Character> itKeys = keys.iterator();
		double maxValue = Double.MIN_VALUE;
		double minValue = Double.MAX_VALUE;

		while(itKeys.hasNext()){
			Character c = itKeys.next();
			if(colorScheme.get(c) > maxValue){
				maxValue = colorScheme.get(c);
			}
			if(colorScheme.get(c) < minValue){
				minValue = colorScheme.get(c);
			}
		}

		double scale = 255./(maxValue-minValue);
		
		itKeys = keys.iterator();		
		while(itKeys.hasNext()){
			Character c = itKeys.next();
			Color color = ColorScheme.getColorFromValue(colorScheme.get(c),  maxValue,  minValue,  scale, colorPalette);
			newScheme.put(c, color);
		}
		
		if(colorSchemeNames.contains(schemeName)){
			int index = colorSchemeNames.indexOf(schemeName);
			colorSchemes.add(index, newScheme);
		}
		else{
			minValues.put(colorSchemes.size(), minValue);
			maxValues.put(colorSchemes.size(), maxValue);
			colorSchemes.add(newScheme);
			colorSchemeNames.add(schemeName);
		}
	}	

	/**
	 * Function to get the color associated to value in the specified range and palette.
	 * 
	 * @param value Value to get color
	 * @param maxValue Maximum value in range 
	 * @param minValue Minimum value in range
	 * @param colorPalette Identifier of the color palette
	 * @return Color associated to given value in the specified range and palette
	 */
	public static Color getColorFromValue(double value, double maxValue, double minValue, double scale, int colorPalette){
		int r = 0;
		int g = 0;
		int b = 0;
		if(colorPalette == ColorScheme.PAL_RED_BLUE){
			Double modifier = Math.floor((value-minValue)*scale);
			r = modifier.intValue();
			g = 0;
			b = 255-modifier.intValue();
		}
		else{
			int modifier = (int)(((value-minValue)*scale)-127.5);
			r = modifier>0 ? 0 : -(modifier*2);
			g = 255-(2*(Math.abs(modifier)));
			b = modifier<0 ? 0 : (modifier*2);
		}
		return new Color(r,g,b);
		
	}
	
	public static boolean containsKey(Character key){
		return colorSchemes.get(currentScheme).containsKey(key);
	}

	/**
	 * Setter for new color scheme
	 * @param colorSchemeName Selected color scheme name
	 */
	public static void setColorScheme(String colorSchemeName){
		if(colorSchemeNames.contains(colorSchemeName)){
			currentScheme = colorSchemeNames.indexOf(colorSchemeName); 
		}
	}
	

	/**
	 * Getter for new color scheme
	 * @return Identifier of the selected color scheme name
	 */
	public static int getColorScheme(){
		return 	currentScheme; 
	}

	public static Vector<String> getColorSchemeNames(){
		return 	colorSchemeNames; 
	}

	
	/**
	 * Function which shows a file dialog to load a new color schema 
	 * @param alignFrame Main frame of the application
	 */
	public static boolean loadColorScheme(AlignFrame alignFrame){
		boolean retValue = false;
		String filename = System.getProperty("user.dir") + File.separator;
	    JFileChooser fc = new JFileChooser(new File(filename));
        fc.requestFocusInWindow();
	    fc.showOpenDialog(alignFrame);
        // Get the selected file
        File file = fc.getSelectedFile();
        // Only get the path, if a file was selected
        if (file != null) { 
        	ColorParser cp = new ColorParser();
        	retValue =  cp.read(file.getAbsolutePath());
        }
	    return retValue;	   
	}

	/**
	 * Function which shows an image of the current palette 
	 */
	public static ColorSchemeInfo getPaletteImage(){
		if(ColorScheme.getColorSchemeNames() == null){
			return new ColorSchemeInfo("");
		}
		else if(currentScheme == ColorScheme.getColorSchemeNames().indexOf(ColorScheme.CLUSTAL_X)){
			return new ColorSchemeInfo(ColorScheme.CLUSTAL_X);
		}
		else{
			return new ColorSchemeInfo(minValues.get(currentScheme), maxValues.get(currentScheme));
		}
	}

	/**
	 * Function which shows an image of the current palette 
	 */
	public static void updatePaletteImage(ColorSchemeInfo paletteCanvas){
		if(paletteCanvas != null){
			if(currentScheme == ColorScheme.getColorSchemeNames().indexOf(ColorScheme.CLUSTAL_X)){
				paletteCanvas.setPaletteName(ColorScheme.CLUSTAL_X);
			}
			else{
				paletteCanvas.setValues(minValues.get(currentScheme), maxValues.get(currentScheme));
			}
		}
	}
}


