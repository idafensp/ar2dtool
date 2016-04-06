package es.upm.oeg.ar2dtool;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NsIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import es.upm.oeg.ar2dtool.exceptions.ConfigFileNotFoundException;
import es.upm.oeg.ar2dtool.exceptions.RDFInputNotValid;
import es.upm.oeg.ar2dtool.exceptions.RDFNotFound;
import es.upm.oeg.ar2dtool.logger.AR2DToolLogger;
import es.upm.oeg.ar2dtool.utils.ConfigValues;
import es.upm.oeg.ar2dtool.utils.dot.DOTGenerator;
import es.upm.oeg.ar2dtool.utils.graphml.GraphMLGenerator;

public class RDF2Diagram {
	
	//POPULAR URIS
	private static final String RDF_TYPE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	
	
	//CONF VALUES
	private ConfigValues conf;
	
	//ONTOLOGY MODEL & SPEC
	private OntModel model;
	private static final OntModelSpec ONT_SPEC = OntModelSpec.OWL_MEM ;
	
	//Classes list for later format
	private ArrayList<String> classesSC;
	
	//Prefix map for later format
	Map<String, String> prefixMap;
	

	//Restriction List
	private ArrayList<String> restrictionList;
	
	
	//LOGGING
	private static final AR2DToolLogger log = AR2DToolLogger.getLogger("AR2DTOOL");


	private static final String DEFAULT_BASE_PREFIX_VALUE = "base";
	

	public RDF2Diagram()
	{
		conf = new ConfigValues();
		model  = ModelFactory.createOntologyModel(ONT_SPEC);
	}
	
	/*
	 * This method loads the Conf Values from file
	 * It overwrites previous Conf vaelues
	 */
	public void loadConfigValues(String pathToConfigFile)
			throws ConfigFileNotFoundException {
		conf.readConfigValues(pathToConfigFile);
		log("Config values succesfully loaded from " + pathToConfigFile);

	}
	
	/*
	 * This method load RDF info into the model
	 * it does not overwrite the info already stored 
	 * It can read a local file or a URI
	 */
	public void loadRdf(String pathToRdfFile) throws RDFNotFound, RDFInputNotValid
	{
		// Use the FileManager to find the input file
		InputStream in = FileManager.get().open(pathToRdfFile);

		if (in == null)
			throw new RDFNotFound("RDF content not found at "+pathToRdfFile);
		
		//read the input stream
		try {

			log("Loading model using RDF/XML parser...");
			model.read(in, "RDFXML");
			
		} catch (org.apache.jena.riot.RiotException e) {
			try
			{
				in.close();
				in = FileManager.get().open(pathToRdfFile);
				log("Loading model using TTL parser...");
				model.read(in, null, "TTL");
			}
			catch (org.apache.jena.riot.RiotException e2) {
				log.getWriter().log(e,Level.SEVERE);
				//e2.printStackTrace();
				throw new RDFInputNotValid("RDF content not valid at "+pathToRdfFile);
			} catch (IOException e1) {
				log.getWriter().log(e,Level.SEVERE);
				//e1.printStackTrace();
				throw new RDFNotFound("RDF content not found at "+pathToRdfFile);
			}
		}
		log("RDF model loaded from " + pathToRdfFile);

		
        detectPrefixMap();
        
        detectRestrictions();
		
		detectClasses();
		
		log("Classes loaded:" + classesSC);

	}
	
	/*
	 * This method load RDF info into the model
	 * it does not overwrite the info already stored 
	 */
	public void loadRdf(OntModel m)
	{
		model = m;
	}
	
	
	
	/*
	 * This method traverses all the triples on the 'model' field
	 * applying the filters specified on:
	 * 
	 * 
	 * - includeOnlyElementList
	 * - ignoreRdfType
	 * - ignoreElementsList
	 * - ignoreLiterals
	 * - equivalentElementList
	 * 
	 * it generates a new model that will only contain the
	 * filtered triples
	 * 
	 */
	public void applyFilters()
	{
		OntModel includeFilteredModel = ModelFactory.createOntologyModel(ONT_SPEC);
		OntModel ignoreFilteredModel = ModelFactory.createOntologyModel(ONT_SPEC);
		OntModel resModel = ModelFactory.createOntologyModel(ONT_SPEC);
		StmtIterator it = model.listStatements();
		
		log("Applying filters");
		
		if (conf.includeOnlyElements()) 
		{
			log("Applying includeOnlyElementsList filter");
			//apply include filters
			while (it.hasNext()) {
				Statement st = it.next();

				//include filters
				if (includeStatement(st)) {
					includeFilteredModel.add(st);
				}
			}
		}
		else
		{
			includeFilteredModel = model;
		}
		
		
		//we only apply if the list is not empty
		if ((conf.ignoreElements())||(conf.ignoreRDFType())||(conf.ignoreRDFType())) {
			
			log("Applying ignoreElementList filter");
			//apply ignore filters over the previous model
			it = includeFilteredModel.listStatements();
			//ignore filters
			while (it.hasNext()) {
				Statement st = it.next();

				//ignore filters
				if (!ignoreStatement(st)) {
					ignoreFilteredModel.add(st);
				}
			}
		}
		else
		{
			ignoreFilteredModel=includeFilteredModel;
		}
		
		
		//apply equivalency filters over the previous model
		//storing the new statements on resModel
		it = ignoreFilteredModel.listStatements();
		log("Applying equivalentElementList filter");
		while(it.hasNext())
		{
			Statement st = it.next();
			resModel.add(getEquivalentStatement(st,resModel));
			
		}
		
		model = resModel;		
	}
	
	

	private Statement getEquivalentStatement(Statement st, OntModel resModel) 
	{
		String sUri = st.getSubject().getURI();
		String pUri = st.getPredicate().getURI();
		
		//looking for equivalent elements
		sUri = checkEquivalentElementList(sUri);
		pUri = checkEquivalentElementList(pUri);
		
		
		//Node s =  NodeFactory.createURI(sUri);
		//Node p =  NodeFactory.createURI(pUri);
		
		RDFNode obj = st.getObject();
		if(obj.isLiteral())
		{
			Statement res = model.createLiteralStatement(model.getResource(sUri), model.getProperty(pUri), obj);
			return res;
		}
		else
		{
			String oUri = st.getObject().toString();
			
			oUri = checkEquivalentElementList(oUri);
			
			Statement res = model.createLiteralStatement(model.getResource(sUri), model.getProperty(pUri), model.getResource(oUri));
			return res;
		}
	}
	
	

	private String checkEquivalentElementList(String e)
	{
		//TODO: should we throw an exception here?
		//TODO: at least we should show some log output
		if(e==null)
			return "NULL";
		
		for(ArrayList<String> list : conf.getEquivalentElementList())
		{
			if(list.contains(e) && !e.equals(list.get(0)))
			{
				//we have found the element on a list, so we replace it by the list's first element 
				log("Found equivalence " + e + ". Replaced by " +  list.get(0));
				return list.get(0);
			}
		}
		
		//we haven't found the element on any list, so we return the same element
		return e;
	}

	
	/*
	 * This method returns true if the statement st should be ignored
	 */
	private boolean ignoreStatement(Statement st)
	{
		Resource sub = st.getSubject();
		Property pre = st.getPredicate();
		RDFNode obj = st.getObject();
		
		//if sub or pre are in the ignoreElememtList the statment 
		//must be ignored
		if(conf.getIgnoreElementList().contains(sub.getURI()))
		{
			log("Ignoring statement " + st + " due to ignoreElememtList (subject)");
			return true;
		}
		if(conf.getIgnoreElementList().contains(pre.getURI()))
		{
			log("Ignoring statement " + st + " due to ignoreElememtList (predicate)");
			return true;
		}
		
		
		//if obj is a resource and it uri is in the ignoreElementList
		//the statement must be ignored
		if(!obj.isLiteral() && conf.getIgnoreElementList().contains(obj.toString()))
		{
			log("Ignoring statement " + st + " due to ignoreElememtList (object)");
			return true;
		}
		
		//if obj is a literal and ignoreLiterals flag is true
		//the statement must be ignored
		if(obj.isLiteral() && conf.ignoreLiterals())
		{
			log("Ignoring statement " + st + " due to ignoreLiterals");
			return true;
		}
		
		//if pre is rdfType and ignoreRDFType is true
		//statement must be ignored
		if(pre.getURI().equals(RDF_TYPE_URI) && conf.ignoreRDFType())
		{
			log("Ignoring statement " + st + " due to ignoreRDFType");
			return true;
		}
		
		//log("Not ingnoring statement " + st);
		return false;
	}
	

	
	/*
	 * This method returns true if the statement st should be included
	 */
	private boolean includeStatement(Statement st) 
	{
		Resource sub = st.getSubject();
		Property pre = st.getPredicate();
		RDFNode obj = st.getObject();
		
		//if sub or pre are in the includeElememtList the statement 
		//must be included
		if(conf.getIncludeOnlyElementList().contains(sub.getURI()) ||
				conf.getIncludeOnlyElementList().contains(pre.getURI()))
		{
			log("Including statement " + st);
			return true;
		}
		
		//if obj is a resource and it uri is in the includeElementList
		//the statement must be included
		if(!obj.isLiteral() && conf.getIncludeOnlyElementList().contains(obj.toString()))
		{
			log("Including statement " + st);
			return true;
		}
		
		log("Not including statement " + st);
		return false;
	}

	public ConfigValues getConf() {
		return conf;
	}

	public void setConf(ConfigValues conf) {
		this.conf = conf;
	}
	
	private void log(String msg)
	{
		log.getWriter().log(msg);
	}

	
	public DOTGenerator getDOTGenerator()
	{
		return new DOTGenerator(model,conf, classesSC, prefixMap,restrictionList);
	}
	
	public GraphMLGenerator getGraphMLGenerator()
	{
		return new GraphMLGenerator(model,conf, classesSC, prefixMap,restrictionList);
	}

	public String printModel() {

		String res = "";
		
		StmtIterator it = model.listStatements();
		
		while(it.hasNext())
		{
			res += it.next().asTriple().toString() + "\n";
		}
		
		return res;
	}
	
	private void detectClasses() 
	{
		classesSC = new ArrayList<String>();
		ExtendedIterator<OntClass> it = model.listClasses();
		while(it.hasNext())
		{
			classesSC.add(it.next().toString());
		}
	}
	
	
	private void detectRestrictions()
	{
		
		restrictionList = new ArrayList<String>();
		
		ExtendedIterator<Restriction> itr = model.listRestrictions();
		while(itr.hasNext())
		{
			Restriction res = itr.next();
			restrictionList.add(res.toString());
			log(">>>>>>RESTRICTION:" + res);
		}
		
	}
	
	//load the nsmap and swap keys and values
	//for easier access later
	private void detectPrefixMap() 
	{
		Map<String, String> pm = model.getNsPrefixMap();
		prefixMap = new HashMap<String,String>();


        log.getWriter().log("****PREFIX MAP****",Level.INFO);
		Iterator<Map.Entry<String,String>> it = pm.entrySet().iterator();
	    while (it.hasNext()) {
	    	
	        Map.Entry<String,String> pairs = it.next();
	        String key = pairs.getKey();
	        String value = pairs.getValue();
	        
	        if(key.isEmpty())
	        {
	        	key = DEFAULT_BASE_PREFIX_VALUE;
	        }
	        
	        log.getWriter().log(value+":"+key,Level.INFO);
	        
	        prefixMap.put(value, key);
	        
	    }
	    

		Property preferredNamespaceUriProp = model.getProperty( "http://purl.org/vocab/vann/preferredNamespaceUri" );
		if(preferredNamespaceUriProp!=null)
		{
			String queryBase ="select ?s ?o where\n"+
					"{\n"+
					"  ?s <http://purl.org/vocab/vann/preferredNamespacePrefix> ?o \n"+
					"}";
			
			Query query = QueryFactory.create(queryBase); 

			QueryExecution qExe = QueryExecutionFactory.create(query, model);
			ResultSet resultsRes = qExe.execSelect();

			try {
			  while (resultsRes.hasNext()) {                
			    QuerySolution qs = resultsRes.nextSolution();
		    
			    String value = qs.get("?s").toString();
			    String key = qs.get("?o").toString();
			    
			    log.getWriter().log(value+":"+key,Level.INFO);
		        
		        prefixMap.put(value, key);
			    //never any results
			  }
			} catch (Exception ex) {
				  log.getWriter().log(ex,Level.SEVERE);
			}
		}
		
	    

		log.getWriter().log("****END PREFIX MAP****",Level.INFO);
        
	    NsIterator itns = model.listNameSpaces();
	    while(itns.hasNext())
	    {
	    	String ns = itns.next();
	    	if(!prefixMap.containsKey(ns))
	    	{
	    		prefixMap.put(ns, "");
	    	}
	    	
	    }

	}
	
}
