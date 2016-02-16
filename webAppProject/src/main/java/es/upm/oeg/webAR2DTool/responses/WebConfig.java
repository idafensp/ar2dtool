package es.upm.oeg.webAR2DTool.responses;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import es.upm.oeg.ar2dtool.exceptions.ConfigFileNotFoundException;
import es.upm.oeg.ar2dtool.exceptions.ConfigKeyNotFound;
import es.upm.oeg.ar2dtool.utils.ConfigValues;
import es.upm.oeg.webAR2DTool.utils.Constants;

@JsonInclude(Include.NON_EMPTY)
public class WebConfig  implements Serializable{
	
	private static final long serialVersionUID = 7856022544247210990L;
	private ArrayList<ArrayList<String>> equivalentElementList;
	private ArrayList<ArrayList<String>> specialElementsList;
	private ArrayList<String> ignoreElementList;
	private ArrayList<String> includeOnlyElementList;
	private Map<String,String> keys;
	private Logger logger = Logger.getLogger(Constants.WEBAPP_NAME);
	
	//Invariables, the final user cant change it
	private static Map<String,String> serverKeys;
	
	//For serialization ignore it.
	@SuppressWarnings("unused")
	private WebConfig(){
		
	}
	
	
	public WebConfig(@Context ServletContext sContext){
		if(serverKeys==null){
			serverKeys = new HashMap<String,String>();
			Properties prop = new Properties();
			try {
				prop.load(new FileInputStream(sContext.getRealPath(Constants.SERVER_PROPERTIES)));
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Can not load server.properties", e);
			}
		}
		equivalentElementList = new ArrayList<ArrayList<String>>();
		specialElementsList = new ArrayList<ArrayList<String>>();
		ignoreElementList = new ArrayList<String>();
		includeOnlyElementList = new ArrayList<String>();
		keys = new HashMap<String,String>();
		//Server configurations
		//keys.put("pathToDot","/usr/local/bin/dot");
		//keys.put("pathToTempDir", "/tmp");
		
		//TO PUT FROM A SERVER CONFIG FILE
		//keys.put("generateGraphMLFile","false");
		//keys.put("generateGvFile","false");
		
		
		//FOR TESTING REMOVE IT
		equivalentElementList.add(new ArrayList<String>(Arrays.asList(new String[]{"holaEquivalent","adiosEquivalent"})));
		specialElementsList.add(new ArrayList<String>(Arrays.asList(new String[]{"holaSpecial","adiosSpecial"})));
		ignoreElementList.addAll(new ArrayList<String>(Arrays.asList(new String[]{"holaIgnore","adiosIgnore"})));
		includeOnlyElementList.addAll(new ArrayList<String>(Arrays.asList(new String[]{"holaInclude","adiosInclude"})));
		//END FOR TESTING
		
		ConfigValues config = new ConfigValues();
		try {
			config.readConfigValues(sContext.getRealPath(Constants.DEFAULT_AR2DTOOL_CONFIG));
			this.equivalentElementList.addAll(config.getEquivalentElementList());
			this.ignoreElementList.addAll(config.getIgnoreElementList());
			this.includeOnlyElementList.addAll(config.getIncludeOnlyElementList());
			this.specialElementsList.addAll(config.getSpecialElementsList());
			for(String key:config.getKeys().keySet()){
				if(!serverKeys.containsKey(key)){
					keys.put(key, config.getKeys().get(key));
				}
			}
		} catch (ConfigFileNotFoundException e) {
			logger.log(Level.SEVERE, "Can not load default config.",e);
		}
		
	}
	
	public ConfigValues toConfigValues(){
		ConfigValues toReturn = new ConfigValues();
		toReturn.setEquivalentElementList(equivalentElementList);
		toReturn.setIgnoreElementList(ignoreElementList);
		toReturn.setIncludeOnlyElementList(includeOnlyElementList);
		toReturn.setSpecialElementsList(specialElementsList);
		for(String key:keys.keySet()){
			try {
				toReturn.setKeys(key, keys.get(key));
			} catch (ConfigKeyNotFound e) {
				logger.log(Level.WARNING, "Not found action for key: "+key+", ignore it.", e);
			}
		}
		for(String key:serverKeys.keySet()){
			try {
				toReturn.setKeys(key, serverKeys.get(key));
			} catch (ConfigKeyNotFound e) {
				logger.log(Level.WARNING, "Not found action for key: "+key+", ignore it.", e);
			}
		}
		return toReturn;
	}
	
	
	//GETTERS AND SETTERS FOR JACKSON serialization
	
	public ArrayList<ArrayList<String>> getEquivalentElementList() {
		return equivalentElementList;
	}

	public void setEquivalentElementList(ArrayList<ArrayList<String>> equivalentElementList) {
		this.equivalentElementList = equivalentElementList;
	}

	public ArrayList<ArrayList<String>> getSpecialElementsList() {
		return specialElementsList;
	}

	public void setSpecialElementsList(ArrayList<ArrayList<String>> specialElementsList) {
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
