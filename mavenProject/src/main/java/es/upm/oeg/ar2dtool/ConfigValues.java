package es.upm.oeg.ar2dtool;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import es.upm.oeg.ar2dtool.logger.AR2DToolLogger;

public class ConfigValues implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1912013956103655512L;
	private static String configPath ="";
	private static final String fileErrorMsg = "CONFIG FILE ERROR: we couldn't open the config file at ";
	private ArrayList<ArrayList<String>> equivalentElementList;
	private ArrayList<ArrayList<String>> specialElementsList;
	private ArrayList<String> ignoreElementList;
	private ArrayList<String> includeOnlyElementList;
	private static AR2DToolLogger log = AR2DToolLogger.getLogger("AR2DTool");

	private Map<String,String> keys;
	
	
	public ConfigValues(String path)
	{
		configPath = path;		
		equivalentElementList = new ArrayList<ArrayList<String>>();
		specialElementsList = new ArrayList<ArrayList<String>>();
		ignoreElementList = new ArrayList<String>();
		includeOnlyElementList = new ArrayList<String>();
		
		//load the default values of the config simple properties
		keys = new HashMap<String, String>();
		keys.put("pathToDot","noPath");
		keys.put("rankdir","LB");
		keys.put("classShape","box3d");
		keys.put("individualShape","oval");
		keys.put("literalShape","house");
		keys.put("arrowhead","normal");
		keys.put("arrowtail","normal");
		keys.put("arrowdir","forward");
		keys.put("classColor","black");
		keys.put("individualColor","black");
		keys.put("literalColor","black");
		keys.put("arrowColor","black");
		keys.put("generateGvFile","false");
		keys.put("ignoreLiterals","false");
		keys.put("useFullUri","true");
		keys.put("synthesizeObjectProperties","false");
		keys.put("ignoreRdfType","false");
		keys.put("generateGraphMLFile","false");
		keys.put("imageSize","500");
	}

	public void readConfigValues()
	{
		//open the file and read it line by line
		try {
			
			FileInputStream fstream = new FileInputStream(configPath);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			
			
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) 
			{
				strLine=strLine.replaceAll("\\s+","");				
				//dbg(strLine);
				processLine(strLine, br);
			}
			
			
			// Close the input stream
			in.close();
		} catch (Exception e) {// Catch exception if any
			log.getWriter().log(fileErrorMsg + configPath + "\n" + e.getMessage(), Level.SEVERE);
		}
	}
	
	private void processLine(String strLine, BufferedReader br) throws IOException {
		if((strLine.startsWith("#"))||(strLine.equals("")))
			return; //skip comment lines
		
		if(strLine.startsWith("equivalentElementList"))
		{
			equivalentElementList = loadEquivalentElementList(strLine, br);
			return;
		}

		
		if(strLine.startsWith("specialElementsList"))
		{
			specialElementsList = loadSpecialElementsList(strLine, br);
			return;
		}
		

		if(strLine.startsWith("includeOnlyElementList"))
		{
			includeOnlyElementList = loadIncludeOnlyElementList(strLine, br);
			return;
		}
		

		if(strLine.startsWith("ignoreElementsList"))
		{
			ignoreElementList = loadIgnoreElementsList(strLine, br);
			return;
		}
			
		//at this point it must be a single property 
		String k=strLine.substring(0, strLine.indexOf("="));
		if(keys.containsKey(k))
		{
			String v = strLine.substring(strLine.indexOf("=")+1,strLine.indexOf(";"));
			keys.put(k, v);
		}
		
	}

	private ArrayList<ArrayList<String>> loadEquivalentElementList(String strLine, BufferedReader br) throws IOException 
	{	
		ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
		String propLine = readPropListLine(strLine,br);
		
		while(propLine.contains("<"))
		{
			String st = propLine.substring(propLine.indexOf("<")+1, propLine.indexOf(">"));
			propLine = propLine.substring(propLine.indexOf(">")+1);
			//dbg("ST:" + st);
			res.add(readCommaValues(st));
		}
		
		return res;
	}



	private ArrayList<ArrayList<String>> loadSpecialElementsList(String strLine, BufferedReader br) throws IOException 
	{	
		ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
		String propLine = readPropListLine(strLine,br);
		
		while(propLine.contains("<"))
		{
			String st = propLine.substring(propLine.indexOf("<")+1, propLine.indexOf(">"));
			propLine = propLine.substring(propLine.indexOf(">")+1);
			//dbg("ST:" + st);
			res.add(readCommaValues(st));
		}
		
		return res;
	}



	private ArrayList<String> loadIncludeOnlyElementList(String strLine, BufferedReader br) throws IOException 
	{	
		ArrayList<String> res = new ArrayList<String>();
		String propLine = readPropListLine(strLine,br);
		
		String st = propLine.substring(propLine.indexOf("<")+1, propLine.indexOf(">"));
		res = readCommaValues(st);
		
		return res;
	}
	


	private ArrayList<String> loadIgnoreElementsList(String strLine, BufferedReader br) throws IOException 
	{	
		ArrayList<String> res = new ArrayList<String>();
		String propLine = readPropListLine(strLine,br);
		
		String st = propLine.substring(propLine.indexOf("<")+1, propLine.indexOf(">"));
		res = readCommaValues(st);
		
		return res;
	}
	
	private ArrayList<String> readCommaValues(String st)
	{
		ArrayList<String> res = new ArrayList<String>();
		
		while(st.contains(","))
		{
			String prop = st.substring(0,st.indexOf(","));
			res.add(prop);
			st = st.substring(st.indexOf(",")+1);
		}
		
		res.add(st);
		
		return res;
	}

	private String readPropListLine(String strLine, BufferedReader br) throws IOException
	{
		String propListLine = strLine;
		
		
		while ((!strLine.contains("];"))&&(strLine = br.readLine()) != null) 
		{
			strLine=strLine.replaceAll("\\s+","");

			if(strLine.startsWith("#"))
				continue;
			
			propListLine+=strLine;
		}
		return propListLine;
	}
	
	
	public static void dbg(String msg)
	{
		log.getWriter().log(msg);
	}

	
	public String toString()
	{
		String res = "Configuration Values:\n";
		res += "-------------------------------\n";
		res+="equivalentElementList:"+equivalentElementList.toString()+"\n";
		res+="specialElementsList:"+specialElementsList.toString()+"\n";
		res+="ignoreElementList"+ignoreElementList.toString()+"\n";
		res+="includeOnlyElementList"+includeOnlyElementList.toString()+"\n";
		
		Iterator<Entry<String, String>> it = keys.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String,String> kv = (Map.Entry<String,String>)it.next();
	        res+=kv.getKey()+":"+kv.getValue()+"\n";
	    }
		

		res += "-------------------------------\n";
	    
		return res;
	}
	
	public ArrayList<ArrayList<String>> getEquivalentElementList() {
		return equivalentElementList;
	}

	public void setEquivalentElementList(
			ArrayList<ArrayList<String>> equivalentElementList) {
		this.equivalentElementList = equivalentElementList;
	}

	public ArrayList<ArrayList<String>> getSpecialElementsList() {
		return specialElementsList;
	}

	public void setSpecialElementsList(
			ArrayList<ArrayList<String>> specialElementsList) {
		this.specialElementsList = specialElementsList;
	}

	public ArrayList<String> getIgnoreElementList() {
		return ignoreElementList;
	}

	public void setIgnoreElementList(ArrayList<String> ignoreElementList) {
		this.ignoreElementList = ignoreElementList;
	}

	public ArrayList<String> getIncludeOnlyElementList() {
		return includeOnlyElementList;
	}

	public void setIncludeOnlyElementList(ArrayList<String> includeOnlyElementList) {
		this.includeOnlyElementList = includeOnlyElementList;
	}

	public Map<String, String> getKeys() {
		return keys;
	}

	public void setKeys(Map<String, String> keys) {
		this.keys = keys;
	}
	
	
	
}
