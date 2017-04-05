package util;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import model.AlignObject;
import model.ResidueImpl;
import model.SequenceImpl;
import model.UDMObject;
import plugins.conversion.ConversionPlugin;
import plugins.method.MethodPlugin;
import view.AlignFrame;

public class PluginLoader {
	
	static ArrayList<String> methodPluginsList;
	static ArrayList<String> conversionPluginsList;
	
	static public void loadPlugins(){
		File pluginsDir = new File(Constants.METHOD_PLUGINS_DIR);
		methodPluginsList = new ArrayList<String>();
	    File[] plugins = pluginsDir.listFiles(new FilenameFilter() {
	        public boolean accept(File dir, String name) {
	            return name.toLowerCase().endsWith(".jar");
	        }
	            
	    });
	    System.out.println("Method plugins:");
	    for(File f: plugins){
	    	methodPluginsList.add(f.getName().substring(0,f.getName().lastIndexOf(".")));
	    	System.out.println("\t"+f.getName().substring(0,f.getName().lastIndexOf(".")));
	    }

		pluginsDir = new File(Constants.CONVERSION_PLUGINS_DIR);
		conversionPluginsList = new ArrayList<String>();
	    plugins = pluginsDir.listFiles(new FilenameFilter() {
	        public boolean accept(File dir, String name) {
	            return name.toLowerCase().endsWith(".jar");
	        }
	            
	    });
	    System.out.println("Conversion plugins:");
	    for(File f: plugins){
	    	conversionPluginsList.add(f.getName().substring(0,f.getName().lastIndexOf(".")));
	    	System.out.println("\t"+f.getName().substring(0,f.getName().lastIndexOf(".")));

	    }
	    
	    
	    
	}

	static public String getPluginName(int i, int type){
		if(type==Constants.PLUGIN_TYPE_METHOD){
			return methodPluginsList.get(i);
		}
		else if(type==Constants.PLUGIN_TYPE_CONVERSION){
			return conversionPluginsList.get(i);
		}
		return null;
	}
	
	static public ArrayList<String> getPlugins(int type){
		if(type==Constants.PLUGIN_TYPE_METHOD){
			return methodPluginsList;
		}
		else if(type==Constants.PLUGIN_TYPE_CONVERSION){
			return conversionPluginsList;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static void loadMethodPlugin(String name, AlignObject alObj, AlignFrame parent){
		UDMObject udmObject = new UDMObject();
		@SuppressWarnings("rawtypes")
		Class [] classParm = null;
		Object [] objectParm = null;
		         
		try {
			@SuppressWarnings("rawtypes")
			Class cl = Class.forName(Constants.METHOD_PLUGINS_PACKAGE+"."+name);
			Constructor<MethodPlugin> co = cl.getConstructor(classParm);
			MethodPlugin dp =  (MethodPlugin) co.newInstance(objectParm);
			
	    	ArrayList<String> alignment = new ArrayList<String>();
	    	for(SequenceImpl s: alObj.getSequences()){
	        	alignment.add(s.getSequenceAsString());
	    	}
	    	
	    	dp.runPlugin(alignment);
	    	String[] output = dp.getMethodOutput();
	    	if(output != null){
				try {
					// Array list of  residues
					List<ResidueImpl> resList = new ArrayList<ResidueImpl>();			
					
					// Initialize the position-to-residue hash map
					HashMap<Integer, ResidueImpl> pos2ResMap = new HashMap<Integer, ResidueImpl>();
					
					for(String nextLine: output){// Iterate over all the lines of the file.
					    StringTokenizer tokenizer = new StringTokenizer(nextLine," ");			    
					    List<String> tokenList = new ArrayList<String>();
					    
					    // Iterate over all the tokens
					    while (tokenizer.hasMoreTokens()) {
					        tokenList.add(tokenizer.nextToken());   
					    }
					    
					    // Add the tokens to the residue object 
					    ResidueImpl res = new ResidueImpl();
					    res.setPosition(Integer.parseInt(tokenList.get(0)));
					    res.setAa((tokenList.get(1).charAt(0))); 
					    res.setScore(new BigDecimal(tokenList.get(2)));
					    // Add the residues to the funcRes list
					    resList.add(res);
					    // Add the residues to the hash map
					    pos2ResMap.put(res.getPosition(), res);
					}			
					
					//Convert the funcRes list to an array
					ResidueImpl[] residues = new ResidueImpl[resList.size()];
					for (int i = 0; i < resList.size(); i++) {
						residues[i] = resList.get(i);
					}			
					
					//Add the array to the UDM object
					udmObject.setResidues(residues);
					udmObject.setResNumber(residues.length);
					
					//Add the hash map to the UDM object
					udmObject.setPos2ResMap(pos2ResMap);
					
					udmObject.setMethodName(name);
					udmObject.setScoreCutoff(dp.getCutoff());
					parent.getMethods().addUdmObject(udmObject);
					
					AlignFrame alignFrame = new AlignFrame(parent.getAlObj(), parent.getMethods(), Constants.DEFAULT_WIDTH, Constants.DEFAULT_HEIGHT, false);
	
					// Close parent if given
					if (parent != null) {
						((AlignFrame) parent).closeOtherWindows();
						parent.dispose();
					}
					alignFrame.setVisible(true);
				}
				catch (Exception e) {
					  JOptionPane.showMessageDialog(null, "Error reading output from "+name+" plugin.\n .The plugin output has to fulfill the following conditions:\n[Position] <SPACE> [Amino Acid Letter] <Space> [Score]", "Plugin output load error", JOptionPane.ERROR_MESSAGE);
				}
	    	}
    	}
		catch (Exception e) {
		  JOptionPane.showMessageDialog(null, "Error loading "+name+" plugin.\n Use the batch launcher 'RUNME' to set a valid classpath in order to use plugins.", "Plugin load error", JOptionPane.ERROR_MESSAGE);
		}

	}
	
	@SuppressWarnings("unchecked")
	public static void loadConversionPlugin(String name, AlignObject alObj, AlignFrame parent){
		UDMObject udmObject = new UDMObject();
		@SuppressWarnings("rawtypes")
		Class [] classParm = null;
		Object [] objectParm = null;
		         
		try {
			@SuppressWarnings("rawtypes")
			Class cl = Class.forName(Constants.CONVERSION_PLUGINS_PACKAGE+"."+name);
			Constructor<ConversionPlugin> co = cl.getConstructor(classParm);
			ConversionPlugin dp =  (ConversionPlugin) co.newInstance(objectParm);
			
	    	ArrayList<String> alignment = new ArrayList<String>();
	    	for(SequenceImpl s: alObj.getSequences()){
	        	alignment.add(s.getSequenceAsString());
	    	}
	    	
	    	dp.runPlugin(alignment, parent);
	    	String[] output = dp.getMethodOutput();
	    	if(output != null){
				try {
					udmObject.setIncreasingScore(dp.isIncreasingScore());
					// Array list of  residues
					List<ResidueImpl> resList = new ArrayList<ResidueImpl>();			
					
					// Initialize the position-to-residue hash map
					HashMap<Integer, ResidueImpl> pos2ResMap = new HashMap<Integer, ResidueImpl>();
					
					for(String nextLine: output){// Iterate over all the lines of the file.
					    StringTokenizer tokenizer = new StringTokenizer(nextLine," ");			    
					    List<String> tokenList = new ArrayList<String>();
					    
					    // Iterate over all the tokens
					    while (tokenizer.hasMoreTokens()) {
					        tokenList.add(tokenizer.nextToken());   
					    }
					    
					    // Add the tokens to the residue object 
					    ResidueImpl res = new ResidueImpl();
					    res.setPosition(Integer.parseInt(tokenList.get(0)));
					    res.setAa((tokenList.get(1).charAt(0))); 
					    res.setScore(new BigDecimal(tokenList.get(2)));
					    // Add the residues to the funcRes list
					    resList.add(res);
					    // Add the residues to the hash map
					    pos2ResMap.put(res.getPosition(), res);
					}			
					
					//Convert the funcRes list to an array
					ResidueImpl[] residues = new ResidueImpl[resList.size()];
					for (int i = 0; i < resList.size(); i++) {
						residues[i] = resList.get(i);
					}			
					
					//Add the array to the UDM object
					udmObject.setResidues(residues);
					udmObject.setResNumber(residues.length);
					
					//Add the hash map to the UDM object
					udmObject.setPos2ResMap(pos2ResMap);
					
					udmObject.setMethodName(name);
					udmObject.setScoreCutoff(dp.getCutoff());
					parent.getMethods().addUdmObject(udmObject);
					
					AlignFrame alignFrame = new AlignFrame(parent.getAlObj(), parent.getMethods(), Constants.DEFAULT_WIDTH, Constants.DEFAULT_HEIGHT, false);
	
					// Close parent if given
					if (parent != null) {
						((AlignFrame) parent).closeOtherWindows();
						parent.dispose();
					}
					alignFrame.setVisible(true);
				}
				catch (Exception e) {
					  JOptionPane.showMessageDialog(null, "Error reading output from "+name+" plugin.\n .The plugin output has to fulfill the following conditions:\n[Position] <SPACE> [Amino Acid Letter] <Space> [Score]", "Plugin output load error", JOptionPane.ERROR_MESSAGE);
				}
	    	}
	  	}
		catch (Exception e) {
		  JOptionPane.showMessageDialog(null, "Error loading "+name+" plugin.\n Use the batch launcher 'RUNME' to set a valid classpath in order to use plugins.", "Plugin load error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
