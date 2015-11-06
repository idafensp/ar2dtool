package es.upm.oeg.ar2dtool.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.upm.oeg.ar2dtool.exceptions.ConfigFileNotFoundException;
import es.upm.oeg.ar2dtool.exceptions.ConfigKeyNotFound;

public class ConfigValues {
	
	private static final String fileNotFoundErrorMsg = "CONFIG FILE ERROR:  couldn't open config file at ";
	private static final String fileNotCompliantErrorMsg = "CONFIG FILE ERROR: config file is not compliant with the sintax aroun line ";

	private static Level logLevel;


	private ArrayList<ArrayList<String>> equivalentElementList;
	private ArrayList<ArrayList<String>> specialElementsList;
	private ArrayList<String> ignoreElementList;
	private ArrayList<String> includeOnlyElementList;
	

	private Map<String,String> keys;
	
	
	public ConfigValues()
	{
		equivalentElementList = new ArrayList<ArrayList<String>>();
		specialElementsList = new ArrayList<ArrayList<String>>();
		ignoreElementList = new ArrayList<String>();
		includeOnlyElementList = new ArrayList<String>();
		
		//load the default values of the config simple properties
		keys = new HashMap<String, String>();
		keys.put("pathToDot","noPathToDot");
		keys.put("pathToTempDir","noPathToTemp");
		keys.put("rankdir","LB");
		keys.put("classShape","rectangle");
		keys.put("individualShape","rectangle");
		keys.put("literalShape","rectangle");
		keys.put("arrowhead","normal");
		keys.put("arrowtail","normal");
		keys.put("arrowdir","forward");
		keys.put("classColor","black");
		keys.put("individualColor","black");
		keys.put("literalColor","black");
		keys.put("arrowColor","black");
		keys.put("ignoreLiterals","false");
		keys.put("nodeNameMode","fulluri");
		keys.put("synthesizeObjectProperties","false");
		keys.put("ignoreRdfType","false");
		keys.put("imageSize","500");
	}	
	


	public void readConfigValues(String configPath) throws ConfigFileNotFoundException 
	{
		int lineCounter = 1;
		
		//open the file and read it line by line
		try {
				
			FileInputStream fstream;
				fstream = new FileInputStream(configPath);
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
				lineCounter++;
			}
			
			
			in.close();

		} 
		catch (FileNotFoundException e) {
			throw new ConfigFileNotFoundException(fileNotFoundErrorMsg + configPath + "\n" + e.getMessage());
		} catch (IOException e) {
			throw new ConfigFileNotFoundException(fileNotFoundErrorMsg + configPath + "\n" + e.getMessage());
		} catch (Exception e) {// Catch exception if any
			throw new ConfigFileNotFoundException(fileNotCompliantErrorMsg + lineCounter + "\n" + e.getMessage());
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
		

		if(strLine.startsWith("ignoreElementList"))
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

	
	public String toString()
	{
		String res = "Configuration Values:\n";
		res += "-------------------------------\n";
		res+="equivalentElementList:"+equivalentElementList.toString()+"\n";
		res+="specialElementsList:"+specialElementsList.toString()+"\n";
		res+="ignoreElementList"+ignoreElementList.toString()+"\n";
		res+="includeOnlyElementList"+includeOnlyElementList.toString()+"\n";
		
		Iterator it = keys.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry kv = (Map.Entry)it.next();
	        res+=kv.getKey()+":"+kv.getValue()+"\n";
	    }
		

		res += "-------------------------------\n";
	    
		return res;
	}
	
	public static Level getLogLevel() {
		return logLevel;
	}



	public static void setLogLevel(Level ll) {
		logLevel = ll;
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
	

	public boolean ignoreElements() {
		return !ignoreElementList.isEmpty();
	}
	

	public boolean includeOnlyElements() {
		return !includeOnlyElementList.isEmpty();
	}

	

	public boolean equivalentElements() {
		return !equivalentElementList.isEmpty();
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



	public void setKeys(String key, String value) throws ConfigKeyNotFound 
	{	
		if(this.keys.containsKey(key))
		{
			this.keys.put(key, value);
		}
		else
		{
			throw new ConfigKeyNotFound("Confing key " + key + " could not be found");
		}
	}
	
	public boolean ignoreLiterals()
	{
		return keys.get("ignoreLiterals").equals("true");
	}
	
	public boolean ignoreRDFType()
	{
		return keys.get("ignoreRdfType").equals("true");
	}
	
	
	public boolean synthesizeObjectProperties()
	{
		return keys.get("synthesizeObjectProperties").equals("true");
	}
	
	
	
	public NodeNameMode getNodeNameMode()
	{
		if(keys.get("nodeNameMode").equals("fulluri"))
			return NodeNameMode.FULLURI;

		if(keys.get("nodeNameMode").equals("localname"))
			return NodeNameMode.LOCALNAME;

		if(keys.get("nodeNameMode").equals("prefix"))
			return NodeNameMode.PREFIX;
		
		
		//by default we assume fulluri
		return NodeNameMode.FULLURI;
	}

	

}
