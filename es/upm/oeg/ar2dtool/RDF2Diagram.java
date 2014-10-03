package es.upm.oeg.ar2dtool;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

import es.upm.oeg.ar2dtool.exceptions.ConfigFileNotFoundException;
import es.upm.oeg.ar2dtool.exceptions.RDFInputNotValid;
import es.upm.oeg.ar2dtool.exceptions.RDFNotFound;
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
	
	
	//LOGGING
	private static final Logger log = Logger.getLogger("AR2DTOOL");
	


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
			model.read(in, null);
		} catch (org.apache.jena.riot.RiotException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RDFInputNotValid("RDF content not valid at "+pathToRdfFile);
		}
		log("RDF model loaded from " + pathToRdfFile);
	}
	
	
	/*
	 * 
	 * TODO
	 * 
	 */
	public void generateDOTGraph(boolean generateDriagram)
	{
		
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
		if (conf.ignoreElements()) {
			
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
		
		
		Node s =  NodeFactory.createURI(sUri);
		Node p =  NodeFactory.createURI(pUri);
		
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
		if(conf.getIgnoreElementList().contains(sub.getURI()) ||
				conf.getIgnoreElementList().contains(pre.getURI()))
		{
			log("Ignoring statement " + st + " due to ignoreElememtList");
			return true;
		}
		
		//if obj is a resource and it uri is in the ignoreElementList
		//the statement must be ignored
		if(!obj.isLiteral() && conf.getIgnoreElementList().contains(obj.toString()))
		{
			log("Ignoring statement " + st + " due to ignoreElememtList");
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

	public static Level getLogLevel() {
		return log.getLevel();
	}

	public static void setLogLevel(Level ll) {
		log.setLevel(ll); 
	}	
	
	private void log(String msg)
	{
		log.log(log.getLevel(), msg);
	}
	
	public DOTGenerator getDOTGenerator()
	{
		return new DOTGenerator(model,conf);
	}
	
	public GraphMLGenerator getGraphMLGenerator()
	{
		return new GraphMLGenerator(model,conf);
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
}
