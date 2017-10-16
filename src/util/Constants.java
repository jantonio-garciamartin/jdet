package util;

import java.awt.Color;
import java.util.HashMap;

import com.lowagie.text.Font;

/**
 * This class defines some constants for the whole software.
 * @author Thilo Muth
 *
 */
public class Constants {

	// General Application Constants
	public static final String APPNAME = "JDet";
	public static final String VERSIONDATE = "2017/10/16";
	public static final String VERSION = "1.4.8";
	public static final String APPTITLE = APPNAME + " " + VERSION + " ("	+ VERSIONDATE + ")";	
	public static final int DEFAULT_WIDTH = 1100;
	public static final int DEFAULT_HEIGHT = 650;
	public static final int MAX_UDM_METHODS = 10;
	public static final String dS = (System.getProperty("os.name").toLowerCase().indexOf("win") >=0) ? "\\" :"/";

	// Help website references
	public static final String FILE_PREFIX = "file://";
	public static final String MANUAL_PATH = System.getProperty("user.dir")+"/doc/JDet_help.html";
	public static final String TUTORIAL_PATH = System.getProperty("user.dir")+"/doc/JDet_tutorial.html";
	
	public static final String JDET_URL = "http://csbg.cnb.csic.es/JDet";
	public static final String MANUAL_URL = "http://csbg.cnb.csic.es/JDet/JDet_help.html";
	public static final String TUTORIAL_URL = "http://csbg.cnb.csic.es/JDet/JDet_tutorial.html";
	
	
	//Export Settings
	public static final int AA_PER_LINE = 80;
	
	// PDB FPT File server location
	public static final String FTPSERVER = "ftp://ftp.wwpdb.org/pub/pdb/data/structures/all/pdb/";
	public static final String PDB_WSDLSERVER = "http://www.pdb.org/pdb/services/pdbws?wsdl";
	public static final double PDB_IDENTITIES_THRESHOLD = 25;
	public static final double PDB_EVALUE_THRESHOLD = 0.0001;
	
	// Parameters and URL for PDB search
	public static final String NCBIBLASTURL = "https://www.ncbi.nlm.nih.gov/blast/Blast.cgi";
	public static final String NCBIBLAST_QUERY_PARAMS = "?DATABASE=pdb&BLAST_PROGRAM=blastp&PROGRAM=blastp&FORMAT_TYPE=XML&HITLIST_SIZE=1&CMD=Put&QUERY_BELIEVE_DEFLINE=no&FORMAT_OBJECT=Alignment&EXPECT_LOW=1e-25&QUERY=";
	public static final String NCBIBLAST_RESPONSE_DEFPARAMS = "?FORMAT_TYPE=Text&ALIGNMENTS=1&DESCRIPTION=100&CMD=Get&RID=";
	public static final long NCBIBLAST_TIMEOUT = 300000;

	// External analisys and filtering methods
	public static final String CONFIG_DIR = "."+dS+"conf"+dS;
	public static final String METHODS_DIR = "."+dS+"programs"+dS;
	public static final String TMP_DIR = "."+dS+"tmp"+dS;
	public static final String FILTER_PRESETS_FILE = CONFIG_DIR+"filterPresets.dat";
	public static final String METHOD_METRICS_FILE = CONFIG_DIR+"Maxhom_McLachlan.metric";
	
	
	// Load Frame Settings
	public static final int LOADFRAME_WIDTH = 800;
	public static final int LOADFRAME_HEIGHT = 500;
	
	// JMolPanel Settings
	public static final int JMOLPANEL_WIDTH = 600;
	public static final int JMOLPANEL_HEIGHT = 600;
	
	// 3D Frame Settings
	public static final int JAVA3DFRAME_WIDTH = 800;
	public static final int JAVA3DFRAME_HEIGHT = 600;
	
	// AlignPanel Constants
	public static final int ALIGNPANEL_WIDTH = 1100;
	public static final int ALIGNPANEL_HEIGHT = 510;

	// AlignView Constants
	public static final String ALIGNVIEW_FONT = "SansSerif";
	public static final int ALIGNVIEW_FONTSTYLE = 1;
	public static final int ALIGNVIEW_FONTSIZE = 12;
	
	// SequencePanel Constants
	public static final int SEQPANEL_WIDTH = 830;
	public static final int SEQPANEL_HEIGHT = 430;
	
	// InfoPanel Constants
	public static final int INFOPANEL_WIDTH = 1100;
	public static final int INFOPANEL_HEIGHT = 60;
	//public static final Color INFOPANEL_BGCOLOR = new Color(112, 97, 153);
	
	// IndexPanel Constants
	public static final int INDEXPANEL_WIDTH = 10;
	public static final int INDEXPANEL_HEIGHT = 30;
	public static final Color INDEXPANEL_BGCOLOR = new Color(47, 212, 255);
	
	// DescBoxPnl Constants
	public static final Color DESCBOXPNL_BGCOLOR = new Color(47, 212, 255);	
	
	// MethodDescPnl Constants
	public static final int METHODDESCPNL_WIDTH = 270;
	public static int METHODDESCPNL_HEIGHT;
	public static final Color METHODDESCPNL_BGCOLOR = new Color(60, 112, 255);
	
	
	// MethodPanel Constants
	public static final int METHODPANEL_WIDTH = 830;
	public static int  METHODPANEL_HEIGHT;
	public static final Color METHODPANEL_BGCOLOR = new Color(60, 112, 255);
	
	// OFFSET for each new method added.
	public static int METHOD_OFFSET = 18;
	
	// NamePanel Constants
	public static final int NAMEPANEL_WIDTH = 250;
	public static final int NAMEPANEL_HEIGHT = 450;
	public static final Color NAMEPANEL_SELECTED_BGSEQUENCE = new Color(111, 111, 111);
	public static final Color NAMEPANEL_SELECTED_FGSEQUENCE = Color.WHITE;
	
	// PdbAlign Settings
	public static final double MIN_PDB_SEQ_ALIGNMENT = 0.1;
	
	// Axes Select Frame Settings
	public static final int CLUSTERSETTINGSFRM_WIDTH = 350;
	public static final int CLUSTERSETTINGSFRM_HEIGHT = 220;
	
	// 3D Frame Settings
	public static final int AXESFRAME_WIDTH = 300;
	public static final int AXESFRAME_HEIGHT = 200;
	public static final float AXISFRAME_MAX = 99f;
	public static final String SEQUENCE_SHAPE_NAME = "Sequences";
	public static final String RESIDUE_SHAPE_NAME = "Residues";
	public static final String SELECTED_RESIDUE_SHAPE_NAME = "SelResidues";
	public static final String CLUSTER_SHAPE_NAME = "Cluster";
	public static final float SEQUENCE_POINT_SIZE = 4.0f;
	public static final float RESIDUE_POINT_SIZE = 2.0f;
	public static final float SELECTED_RESIDUE_POINT_SIZE = 4.0f;	
	public static final float CLUSTER_POINT_SIZE = 4.0f;
	public static final float SELECTION_PRECISION = 0.008f;
	
	
	/*
	 * Text for Tooltips on sliders
	 */	
	public static final String XDETSLIDER_TOOLTIP = "Change the correlation threshold.";
	public static final String ENTROPYSLIDER_TOOLTIP = "Change the entropy threshold.";
	public static final String S3DETSLIDER_TOOLTIP = "Change the S3Det threshold.";
	public static final String OTHERSLIDER_TOOLTIP = "Change the score threshold.";	
	
	/*
	 * Method selection modes
	 */
	public static final int SELECTION_ONLY = 0;
	public static final int SELECTION_AND = 1;
	public static final int SELECTION_OR = 2;

	
	/*
	 * Default values for the Sliders.
	 */	
	public static final int OTHERSLIDER_DEFAULT[] = {20, 20, 20, 20, 20};
	public static final int LABEL_FONT_SIZE = 12;
	public static final int MAX_LABEL_LENGTH = 10;
	
	/*
	 * Default threshold values for the methods.
	 */
	
	public static final double XDETCUTOFF_DEFAULT = 0.6;
	public static final double ENTROPYCUTOFF_DEFAULT = 2.5;
	public static final double S3DETCUTOFF_DEFAULT = 10.0;
	public static final double OTHERCUTOFF_DEFAULT = 2.0;
	
	// Tooltip for the textfields
	public static final String TEXTFIELD_TOOLTIP = "Please hit ENTER...";
	
	// Methods Colors
	// One method == green
	public static final Color GREEN =  new Color(0, 235, 0);
	// Two methods == yellow
	public static final Color YELLOW =  new Color(235, 235, 0);
	// Three methods == orange
	public static final Color ORANGE =  new Color(255, 165, 0);
	// Four methods = red
	public static final Color RED = new Color(235, 0, 0);
	
	// Group Colors
	public static final Color[] GROUPCOLORS = {Color.WHITE, Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, 
			Color.YELLOW, Color.PINK, Color.MAGENTA, Color.CYAN, Color.LIGHT_GRAY};
	
	//Cluster panel colors	
	public static final Color SELECTED_PROTEIN_COLOR = Color.BLACK;
	public static final Color SELECTED_RESIDUE_COLOR = Color.RED;
	public static final Color DEFAULT_RESIDUE_COLOR = Color.GRAY;
	public static final String DEFAULT_RESIDUE_COLOR_NAME = "gray";
	public static final Color DEFAULT_CLUSTER_COLOR = Color.BLACK;
	public static final String DEFAULT_CLUSTER_COLOR_NAME = "black";
	public static final Color DEFAULT_AXIS_COLOR = Color.BLACK;
	public static final Color PICK_AREA_COLOR = Color.BLACK;

	

	
	
	// Returns true if the char is a gap
	public static final boolean isGap(char c){
	    return (c == '-' || c == '.' || c == ' ') ? true : false;
	}
	
	/*
	 * Constants for the sequence logo generation 	
	 */
	
	public static final HashMap<String,Integer> AA_CODES = new HashMap<String,Integer>();
	static{
		AA_CODES.put("-", 0);
		AA_CODES.put("A", 1);
		AA_CODES.put("W", 2);
		AA_CODES.put("L", 3);
		AA_CODES.put("V", 4);
		AA_CODES.put("I", 5);
		AA_CODES.put("M", 6);
		AA_CODES.put("F", 7);
		AA_CODES.put("K", 8);
		AA_CODES.put("R", 9);
		AA_CODES.put("T", 10);
		AA_CODES.put("S", 11);
		AA_CODES.put("N", 12);
		AA_CODES.put("Q", 13);
		AA_CODES.put("C", 14);
		AA_CODES.put("D", 15);
		AA_CODES.put("E", 16);
		AA_CODES.put("G", 17);
		AA_CODES.put("H", 18);
		AA_CODES.put("Y", 19);
		AA_CODES.put("P", 20);
		AA_CODES.put("X", 21);
		AA_CODES.put("B", 22);
		AA_CODES.put("Z", 23);
	}
	public static final int PANEL_LOGO_WIDTH = 600;
	public static final int LOGO_HEIGHT = 200;	
	public static final int PANEL_LOGO_MARGIN = 6;
	public static final int LETTER_WIDTH = 30;	
	public static final int LOGOHEADER_HEIGHT = 40;
	public static final int LOGOHEADER_FONT_SIZE = 10;
	public static final String LOGOHEADER_FONT = "SansSerif";
	public static final int LOGOHEADER_FONTSTYLE = Font.BOLD;
	public static final int LOGOHEADER_FONTSIZE = 12;
	public static final int LOGOHEADER_MARGIN = 5;
	
	// Plugins constants
	public static final int PLUGIN_TYPE_METHOD = 1;
	public static final int PLUGIN_TYPE_CONVERSION = 2;

	public static final String METHOD_PLUGINS_DIR = "plugins/method";
	public static final String METHOD_PLUGINS_PACKAGE = "plugins.method";

	public static final String CONVERSION_PLUGINS_DIR = "plugins/conversion";
	public static final String CONVERSION_PLUGINS_PACKAGE = "plugins.conversion";

	
	
}
