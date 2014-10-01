package es.upm.oeg.ar2dtool.utils.dot;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.tuple.MutablePair;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import es.upm.oeg.ar2dtool.utils.ConfigValues;
import es.upm.oeg.ar2dtool.utils.NodeNames;

public class DOTGenerator 
{

	//POPULAR URIS
	private static final String RDF_TYPE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	private static final String RDFS_RANGE = "http://www.w3.org/2000/01/rdf-schema#range";
	private static final String RDFS_DOMAIN = "http://www.w3.org/2000/01/rdf-schema#domain";
	
	//OBJ PROP LIST
	private Map<String,MutablePair<String,String>> objPropsMap;


	//CONF VALUES
	private ConfigValues conf;
		
	//ONTOLOGY MODEL
	private OntModel model;
	
	//DOT Triples
	private ArrayList<DOTTriple> dottriples;
	
	//SHAPES&COLORS LISTS
	private ArrayList<String> classesSC, individualsSC, literalsSC, ontPropertiesSC, dtPropertiesSC;

	private Map<String, String> prefixMap;

	
	public DOTGenerator(OntModel m, ConfigValues c)
	{
		model = m;
		conf = c;
		dottriples = new ArrayList<DOTTriple>();
		classesSC = new ArrayList<String>();
		individualsSC = new ArrayList<String>();
		literalsSC = new ArrayList<String>();
		ontPropertiesSC = new ArrayList<String>();
		dtPropertiesSC = new ArrayList<String>();
		objPropsMap = new HashMap<String,MutablePair<String,String>>();
	}
	
	/*
	 * This method traverses all the DOT triples on the 'model' field
	 * applying the transformations specified on:
	 * 
	 * 
	 * - shapes and colors
	 * - special elements list
	 * - sintetyze obj props
	 * - node names mode
	 * 
	 */
	public void applyTransformations()
	{
		//detecting classes
		detectClasses();
		
		//detecting individuals
		detectIndividuals();
		
		//detecting ont properties
		detectOntProperties();
		
		//detecting dt properties
		detectDtProperties();
		
		
		//load the prefixmap just in case
		prefixMap = model.getNsPrefixMap();
		
		StmtIterator it = model.listStatements();
		while(it.hasNext())
		{
			Statement st = it.next();
			
			Resource s = st.getSubject();
			Property p = st.getPredicate();
			RDFNode o = st.getObject();
			
			//detecting literals
			if(o.isLiteral())
			{
				literalsSC.add(st.getObject().toString());
			}

			
			//check syntetize ob props
			//if it is an obj prop and the user wants to sysntetize we will add it later
			if(conf.synthesizeObjectProperties() && checkObjPropoerties(s,p,o))
				continue;
			
			
			
			String sName = getNodeName(s);
			String pName = getNodeName(p);
			String oName = getNodeName(o);
			
			dottriples.add(new DOTTriple(sName,oName,pName));
			
		}
		
		generateSyntObjPropertiesTriples();
		
		log(printDotDriples());
		
	
	}
	
	private String printDotDriples() 
	{
		String res = "----- DOT Triples -----\n";
		for(DOTTriple dt : dottriples)
		{
			res +=dt.toString();
		}
		
		
		return res + "----- End DOT Triples -----\n";
	}

	public String generateDOTSource()
	{
		
		String dotHead = "digraph ar2dtool_diagram { \n" +
				"rankdir=" + conf.getKeys().get("rankdir") + ";\n" +
				"size=\"" + conf.getKeys().get("imageSize") + "\"\n";
		
		String dottail = "\n}";
		
		
		String dotsource = "";
		for(DOTTriple dt : dottriples)
		{
			String spoviz = "\t"+dt.getSource()+" -> " + dt.getTarget() + " [ label = "+ dt.getEdge() + " ];\n";
			dotsource += spoviz;
		}
		
		String classStyle = "node [shape = "+ conf.getKeys().get("classShape") +", color="+ conf.getKeys().get("classColor") +"]; ";
		String individualStyle = "node [shape = "+ conf.getKeys().get("individualShape") +", color="+ conf.getKeys().get("individualColor") +"]; ";
		String literalStyle = "node [shape = "+ conf.getKeys().get("literalShape") +", color="+ conf.getKeys().get("literalColor") +"]; ";
		String objPropStyle = "node [shape = "+ conf.getKeys().get("objPropShape") +", color="+ conf.getKeys().get("objPropColor") +"]; ";
		String dtPropStyle = "node [shape = "+ conf.getKeys().get("dtPropShape") +", color="+ conf.getKeys().get("dtPropColor") +"]; ";
		
		//TODO classes s&c
		String classesStyle = "";
		for (String c : classesSC)
		{
			classesStyle +=  "\"" + c + "\" ";
		}
		if(!classesStyle.equals(""))
		{
			classesStyle = classStyle + classesStyle + "; /*classes style*/\n";
		}
		
		//TODO individuals s&c
		String individualsStyle = "";
		for (String i : individualsSC)
		{
			individualsStyle +=  "\"" + i + "\" ";
		}
		if(!individualsStyle.equals(""))
		{
			individualsStyle = individualStyle + individualsStyle +  "; /*individuals style*/\n";
		}
		
		
		//TODO literals s&c
		String literalsStyle = "";
		for (String l : literalsSC)
		{
			literalsStyle +=  "\"" + l + "\" ";
		}
		if(!literalsStyle.equals(""))
		{
			literalsStyle = literalStyle + literalsStyle +  "; /*literals style*/\n";
		}
		
		//TODO obj prop s&c
		String objPropsStyle = "";
		for (String op : ontPropertiesSC)
		{
			objPropsStyle +=  "\"" + op + "\" ";
		}
		if(!objPropsStyle.equals(""))
		{
			objPropsStyle = objPropStyle + objPropsStyle +  "; /*object properties style*/\n";
		}
		
		//TODO dt prop s&c
		String dtPropsStyle = "";
		for (String dt : dtPropertiesSC)
		{
			dtPropsStyle +=  "\"" + dt + "\" ";
		}
		if(!dtPropsStyle.equals(""))
		{
			dtPropsStyle +=  dtPropStyle + dtPropsStyle + "; /*data type properties style*/\n";
		}
		
		
		//TODO special elements
		
		String res = dotHead + classesStyle + individualsStyle + literalsStyle + objPropsStyle + dtPropsStyle + dotsource + dottail;
		return res;
	}
	
	public void generateDOTDiagram(String dotSource, String outPath, String type)
	{
		GraphViz gv = new GraphViz(conf.getKeys().get("pathToDot"),conf.getKeys().get("pathToTempDir"));
		gv.add(dotSource);
		
		log("GV src:\n" + gv.getDotSource());
		
		//String type = "gif";
		//      String type = "dot";
		//      String type = "fig";    // open with xfig
		//      String type = "pdf";
		//      String type = "ps";
		//      String type = "svg";    // open with inkscape
		//      String type = "png";
		//      String type = "plain";
		
		String repesentationType= "dot";
		//		String repesentationType= "neato";
		//		String repesentationType= "fdp";
		//		String repesentationType= "sfdp";
		// 		String repesentationType= "twopi";
		// 		String repesentationType= "circo";
		
		File out = new File(outPath);   // Linux
		gv.writeGraphToFile( gv.getGraph(gv.getDotSource(), type, repesentationType), out );
	}
	
	private void generateSyntObjPropertiesTriples() 
	{
		if(!conf.synthesizeObjectProperties())
			return;
		
		Iterator it = objPropsMap.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry kv = (Map.Entry)it.next();
	        String propUri = (String) kv.getKey();
	        
	        MutablePair<String,String> mp = (MutablePair<String, String>) kv.getValue();
	        String rangeUri = mp.getRight();
	        String domainUri = mp.getLeft();

			String domainName = getNodeName(domainUri);
			String rangeName = getNodeName(rangeUri);
			String propName = getNodeName(propUri);
			
			dottriples.add(new DOTTriple(domainName,rangeName,propName));
			
	    }
	}

	private boolean checkObjPropoerties(Resource s,Property p,RDFNode o) 
	{
		if(p.getURI().equals(RDFS_DOMAIN))
		{
			MutablePair<String, String> dr = new MutablePair<String,String>();
			dr.setLeft(o.asResource().getURI());
			objPropsMap.put(s.getURI(), dr);
			return true;
		}
		

		if(p.getURI().equals(RDFS_RANGE))
		{
			MutablePair<String, String> dr = new MutablePair<String,String>();
			dr.setRight(o.asResource().getURI());
			objPropsMap.put(s.getURI(), dr);
			return true;
		}
		
		
			
		return false;
	}

	
	private String getNodeName(String n) 
	{
		return getNodeName(model.getResource(n));
	}
	
	
	private String getNodeName(RDFNode n) 
	{
		if(n.isLiteral())
			return n.toString();

		//at this point we know that we are dealing with a resource
		Resource res = n.asResource();
		switch (conf.getNodeNameMode()) 
		{
			case LOCALNAME:
			{
				return res.getLocalName();
			}
			case PREFIX:
			{
				String ns = res.getNameSpace();
				String prefix = prefixMap.get(ns);
				if(prefix==null)
				{
					//if the prefix does not exit we use the full URI
					return res.getURI();
				}
				else
				{
					//replace the ns with the prefix
					return res.getURI().replace(ns, prefix);
				}
			}
		}

		//at this point we know the user wants to use URIs
		return res.getURI();
	}


	private void detectDtProperties() 
	{
		ExtendedIterator<OntProperty> it = model.listOntProperties();
		while(it.hasNext())
		{
			ontPropertiesSC.add(getNodeName(it.next()));
		}
	}


	private void detectOntProperties() 
	{
		ExtendedIterator<DatatypeProperty> it = model.listDatatypeProperties();
		while(it.hasNext())
		{
			dtPropertiesSC.add(getNodeName(it.next()));
		}	
	}


	private void detectIndividuals() 
	{
		ExtendedIterator<Individual> it = model.listIndividuals();
		while(it.hasNext())
		{
			individualsSC.add(getNodeName(it.next()));
		}
	}


	private void detectClasses() 
	{
		ExtendedIterator<OntClass> it = model.listClasses();
		boolean empty = true;
		while(it.hasNext())
		{
			empty = false;
			classesSC.add(getNodeName(it.next()));
		}
		
		if(empty)
		{
			log("No classes detected");
		}
	}


	public ConfigValues getConf() {
		return conf;
	}


	public void setConf(ConfigValues conf) {
		this.conf = conf;
	}


	public OntModel getModel() {
		return model;
	}


	public void setModel(OntModel model) {
		this.model = model;
	}	
	
	private void log(String msg)
	{
		//log.log(logLevel, msg);
		//TODO use log instead of sys.out
		System.out.println(msg);
	}
	
	

}
