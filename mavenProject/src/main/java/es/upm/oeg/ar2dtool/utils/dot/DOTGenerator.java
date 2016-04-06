package es.upm.oeg.ar2dtool.utils.dot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import es.upm.oeg.ar2dtool.exceptions.NullTripleMember;
import es.upm.oeg.ar2dtool.logger.AR2DToolLogger;
import es.upm.oeg.ar2dtool.utils.AR2DTriple;
import es.upm.oeg.ar2dtool.utils.ConfigValues;

public class DOTGenerator 
{

	//POPULAR URIS
	private static final String RDFS_RANGE = "http://www.w3.org/2000/01/rdf-schema#range";
	private static final String RDFS_DOMAIN = "http://www.w3.org/2000/01/rdf-schema#domain";
	
	private static final AR2DToolLogger logger = AR2DToolLogger.getLogger("AR2DTOOL");
	
	//OBJ PROP LIST
	private Map<String,ObjPropPair<String,String>> objPropsMap;
	

	// LOGGING
	private static final AR2DToolLogger log = AR2DToolLogger.getLogger("AR2DTOOL");
	
	//WHEN RANGE OR DOMAINS ARE EMPTY
	private static final String DEFAULT_OBJ_PROP_VALUE = "http://www.w3.org/2002/07/owl#Thing";

	//AVOID RESTRICTIONS
	private static final boolean AVOID_RESTRICTION_NODES = true;


	//CONF VALUES
	private ConfigValues conf;
		
	//ONTOLOGY MODEL
	private OntModel model;
	
	//DOT Triples
	private ArrayList<AR2DTriple> dottriples;
	
	//ALL NODENAMES to be depicted
	private HashSet<String> allDepictedNodeNames;
	
	//SHAPES&COLORS LISTS
	private ArrayList<String> classesSC, individualsSC, literalsSC, ontPropertiesSC, dtPropertiesSC;

	private Map<String, String> prefixMap;
	
	
	//RESTRICTION LIST
	private ArrayList<String> restrictionList;

	
	public DOTGenerator(OntModel m, ConfigValues c, ArrayList<String> clsc, Map<String, String> pm, ArrayList<String> reslist)
	{
		model = m;
		conf = c;
		dottriples = new ArrayList<AR2DTriple>();
		classesSC = clsc; //new ArrayList<String>();
		individualsSC = new ArrayList<String>();
		literalsSC = new ArrayList<String>();
		ontPropertiesSC = new ArrayList<String>();
		dtPropertiesSC = new ArrayList<String>();
		objPropsMap = new HashMap<String,ObjPropPair<String,String>>();
		allDepictedNodeNames = new HashSet<String>();
		restrictionList = reslist;
		prefixMap = pm;
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
	public void applyTransformations() throws NullTripleMember
	{

		//detecting classes
		detectClasses();
		
		//detecting individuals
		detectIndividuals();
		
		//detecting ont properties
		detectOntProperties();
		
		//detecting dt properties
		detectDtProperties();
		
		
		
		StmtIterator it = model.listStatements();
		while(it.hasNext())
		{
			Statement st = it.next();
			
			Resource s = st.getSubject();
			Property p = st.getPredicate();
			RDFNode o = st.getObject();

 
			if((AVOID_RESTRICTION_NODES)&&(restrictionList.contains(s.toString())))
				continue;
			
			//detecting literals
			if(o.isLiteral())
			{
				literalsSC.add(st.getObject().toString());
			}
			else
			{
				if((AVOID_RESTRICTION_NODES)&&(restrictionList.contains(o.toString())))
					continue;
			}

			
			//check syntetize ob props
			//if it is an obj prop and the user wants to sysntetize we will add it later
			if(conf.synthesizeObjectProperties() && checkObjPropoerties(s,p,o))
				continue;
			
			
			
			String sName = getNodeName(s);
			String pName = getNodeName(p);
			String oName = getNodeName(o);
			
				
			dottriples.add(new AR2DTriple(sName,oName,pName));
			
		}
		
		generateSyntObjPropertiesTriples();
		
		log(printDotDriples());
		
	
	}

	
	private String printDotDriples() 
	{
		String res = "----- DOT Triples -----\n";
		for(AR2DTriple dt : dottriples)
		{
			res +=dt.toString();
		}
		
		
		return res + "----- End DOT Triples -----\n";
	}

	public String generateDOTSource() throws NullTripleMember
	{
		
		String dotHead = "digraph ar2dtool_diagram { \n" +
				"rankdir=" + conf.getKeys().get("rankdir") + ";\n" +
				"size=\"" + conf.getKeys().get("imageSize") + "\"\n";
		
		String dottail = "\n}";
		
		
		
		String dotsource = "";
		for(AR2DTriple dt : dottriples)
		{
			if((dt.getSource()==null)||(dt.getEdge()==null)||(dt.getTarget()==null)||(dt.getSource().equals("null"))||(dt.getEdge().equals("null"))||(dt.getTarget().equals("null")))
			{
				throw new NullTripleMember("Triple with null member: <s="+dt.getSource()+",e="+dt.getEdge()+",t="+dt.getTarget()+">");
			}
			
			String spoviz = "\t\""+dt.getSource()+"\" -> \"" + dt.getTarget() + "\" [ label = \""+ dt.getEdge() + "\" ];\n";
			dotsource += spoviz;
			
			//store all node names for later filtering ISSUE #25
			allDepictedNodeNames.add(dt.getSource());
			allDepictedNodeNames.add(dt.getEdge());
			allDepictedNodeNames.add(dt.getTarget());
		}
		
		String classStyle = "node [shape = "+ conf.getKeys().get("classShape") +", color=\""+ conf.getKeys().get("classColor") +"\"]; ";
		String individualStyle = "node [shape = "+ conf.getKeys().get("individualShape") +", color=\""+ conf.getKeys().get("individualColor") +"\"]; ";
		String literalStyle = "node [shape = "+ conf.getKeys().get("literalShape") +", color=\""+ conf.getKeys().get("literalColor") +"\"]; ";
		String objPropStyle = "node [shape = "+ conf.getKeys().get("objPropShape") +", color=\""+ conf.getKeys().get("objPropColor") +"\"]; ";
		String dtPropStyle = "node [shape = "+ conf.getKeys().get("dtPropShape") +", color=\""+ conf.getKeys().get("dtPropColor") +"\"]; ";
		
		String classesStyle = "";
		for (String c : classesSC)
		{
			if(allDepictedNodeNames.contains(c))
			{
				classesStyle +=  "\"" + c + "\" ";	
			}
		}
		if(!classesStyle.equals(""))
		{
			classesStyle = classStyle + classesStyle + "; /*classes style*/\n";
		}
		
		String individualsStyle = "";
		for (String i : individualsSC)
		{
			if(allDepictedNodeNames.contains(i))
			{
				individualsStyle +=  "\"" + i + "\" ";
			}
		}
		if(!individualsStyle.equals(""))
		{
			individualsStyle = individualStyle + individualsStyle +  "; /*individuals style*/\n";
		}
		
		
		String literalsStyle = "";
		for (String l : literalsSC)
		{
			if(allDepictedNodeNames.contains(l))
			{
				literalsStyle +=  "\"" + l + "\" ";
			}
		}
		if(!literalsStyle.equals(""))
		{
			literalsStyle = literalStyle + literalsStyle +  "; /*literals style*/\n";
		}
		
		String objPropsStyle = "";
		for (String op : ontPropertiesSC)
		{
			if(allDepictedNodeNames.contains(op))
			{
				objPropsStyle +=  "\"" + op + "\" ";
			}
		}
		if(!objPropsStyle.equals(""))
		{
			objPropsStyle = objPropStyle + objPropsStyle +  "; /*object properties style*/\n";
		}
		
		String dtPropsStyle = "";
		for (String dt : dtPropertiesSC)
		{
			if(allDepictedNodeNames.contains(dt))
			{
				dtPropsStyle +=  "\"" + dt + "\" ";
			}
		}
		if(!dtPropsStyle.equals(""))
		{
			dtPropsStyle +=  dtPropStyle + dtPropsStyle + "; /*data type properties style*/\n";
		}
		
		
		//TODO special elements
		
		String res = dotHead + classesStyle + individualsStyle + literalsStyle + objPropsStyle + dtPropsStyle + dotsource + dottail;
		return res;
	}
	
	public void saveSourceToFile(String path) throws NullTripleMember 
	{
		try {
			PrintWriter out = new PrintWriter(path);
			out.println(this.generateDOTSource());
			out.close();
		} catch (FileNotFoundException e) {
			logger.getWriter().log(e,Level.SEVERE);
			//e.printStackTrace();
		}
	
	}
	
	public int generateDOTDiagram(String dotSource, String outPath, String type)
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
		return gv.writeGraphToFile( gv.getGraph(gv.getDotSource(), type, repesentationType), out );
	}
	
	private void generateSyntObjPropertiesTriples() throws NullTripleMember 
	{
		if(!conf.synthesizeObjectProperties())
			return;
		
		Iterator<Entry<String, ObjPropPair<String, String>>> it = objPropsMap.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, ObjPropPair<String,String>> kv = (Map.Entry<String,ObjPropPair<String,String>>)it.next();
	        String propUri = (String) kv.getKey();
	        
	        ObjPropPair<String,String> mp = (ObjPropPair<String, String>) kv.getValue();
	        String rangeUri = mp.getRight();
	        String domainUri = mp.getLeft();

			if((rangeUri==null)||(rangeUri.equals("null")))
			{
				throw new NullTripleMember("null rangeUri for propUri:" + propUri + " [pair:" + mp.toString() + "]");
			}
			if((domainUri==null)||(domainUri.equals("null")))
			{
				throw new NullTripleMember("null domainUri propUri:" + propUri + " [pair:" + mp.toString() + "]");
			}

			String domainName = getNodeName(domainUri);
			String rangeName = getNodeName(rangeUri);
			String propName = getNodeName(propUri);
			
			
			dottriples.add(new AR2DTriple(domainName,rangeName,propName));
			
	    }
	    
	}

	private boolean checkObjPropoerties(Resource s,Property p,RDFNode o) throws NullTripleMember 
	{
		
		if(p.getURI().equals(RDFS_DOMAIN))
		{
			ObjPropPair<String, String> dr = new ObjPropPair<String,String>();
			if(objPropsMap.containsKey(s.getURI()))
			{
				dr = objPropsMap.get(s.getURI());
			}
			else
			{
				dr.setRight(DEFAULT_OBJ_PROP_VALUE);
			}
			
			String oString = o.asResource().getURI();
			dr.setLeft(oString);
			objPropsMap.put(s.getURI(), dr);
			return true;
		}
		

		if(p.getURI().equals(RDFS_RANGE))
		{
			ObjPropPair<String, String> dr = new ObjPropPair<String,String>();
			if(objPropsMap.containsKey(s.getURI()))
			{
				dr = objPropsMap.get(s.getURI());
			}
			else
			{
				dr.setLeft(DEFAULT_OBJ_PROP_VALUE);	
			}
			

			String oString = o.asResource().getURI();
			dr.setRight(oString);
			objPropsMap.put(s.getURI(), dr);
			return true;
		}
		
		
			
		return false;
	}

	
	private String getNodeName(String n) throws NullTripleMember 
	{
		String nn = getNodeName(model.getResource(n));
		if((nn==null)||(nn.equals("null")))
		{
			throw new NullTripleMember("null nn for n:" + n);
		}
		
		return nn;
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
					return res.getURI().replace(ns, prefix+":");
				}
			}
			default:
				
			break;
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


	private void detectClasses() throws NullTripleMember 
	{
		
		ArrayList<String> res = new ArrayList<String>();
		for(String c: classesSC)
		{
			res.add(getNodeName(c));
		}
		
		classesSC = res;
		
		if(classesSC.isEmpty())
			{
			log("No classes detected");
		}
		else
		{
			log("Classes detected: " + classesSC);
		}
		
		
		//TODO remove
//		ExtendedIterator<OntClass> it = model.listClasses();
//		boolean empty = true;
//		while(it.hasNext())
//		{
//			empty = false;
//			classesSC.add(getNodeName(it.next()));
//		}
//		
//		if(empty)
//		{
//			log("No classes detected");
//		}
//		else
//		{
//			log("Classes detected: " + classesSC);
//		}
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
		log.getWriter().log(msg);
	}

	
	
	

}
