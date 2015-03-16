package es.upm.oeg.ar2dtool.utils.graphml;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.logging.Logger;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.NsIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import es.upm.oeg.ar2dtool.exceptions.NullTripleMember;
import es.upm.oeg.ar2dtool.utils.AR2DTriple;
import es.upm.oeg.ar2dtool.utils.ConfigValues;
import es.upm.oeg.ar2dtool.utils.dot.ObjPropPair;


public class GraphMLGenerator 
{
	
	//POPULAR URIS
	private static final String RDFS_RANGE = "http://www.w3.org/2000/01/rdf-schema#range";
	private static final String RDFS_DOMAIN = "http://www.w3.org/2000/01/rdf-schema#domain";
	
	//OBJ PROP LIST
	private Map<String,ObjPropPair<String,String>> objPropsMap;
	

	// LOGGING
	private static final Logger log = Logger.getLogger("AR2DTOOL");
	
	//DEFAULT SHAPES AND COLORS
	private static final String DEFAULT_NODE_COLOR = "black";
	private static final String DEFAULT_NODE_SHAPE = "rectangle";
	private static final String DEFAULT_EDGE_COLOR = "black";
	
	//ID PREFIXES
	private static final String NODE_ID_PREFIX = "nid_";
	private static final String EDGE_ID_PREFIX = "eid_";


	//CONF VALUES
	private ConfigValues conf;
		
	//ONTOLOGY MODEL
	private OntModel model;
	
	//DOT Triples
	private ArrayList<AR2DTriple> gmltriples;
	
	//SHAPES&COLORS LISTS
	private ArrayList<String> classesSC, individualsSC, literalsSC, ontPropertiesSC, dtPropertiesSC;

	private Map<String, String> prefixMap;

	
	public GraphMLGenerator(OntModel m, ConfigValues c)
	{
		model = m;
		conf = c;
		gmltriples = new ArrayList<AR2DTriple>();
		classesSC = new ArrayList<String>();
		individualsSC = new ArrayList<String>();
		literalsSC = new ArrayList<String>();
		ontPropertiesSC = new ArrayList<String>();
		dtPropertiesSC = new ArrayList<String>();
		objPropsMap = new HashMap<String,ObjPropPair<String,String>>();
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

		//load the prefixmap just in case
		prefixMap = loadPrefixMap();
		
		
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
			
			gmltriples.add(new AR2DTriple(sName,oName,pName));
			
		}
		
		generateSyntObjPropertiesTriples();
		
		log(printGmlDriples());
		
	
	}
	
	
	//load the nsmap and swap keys and values
	//for easier access later
	private Map<String, String> loadPrefixMap() {
		
		Map<String, String> pm = model.getNsPrefixMap();
		Map<String, String> res = new HashMap<String,String>();
		
		
		Iterator<Map.Entry<String,String>> it = pm.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String,String> pairs = it.next();
	        String key = pairs.getKey();
	        String value = pairs.getValue();
	        res.put(value, key);
	        
	    }
	    
	    NsIterator itns = model.listNameSpaces();
	    while(itns.hasNext())
	    {
	    	String ns = itns.next();
	    	if(!res.containsKey(ns))
	    	{
	    		res.put(ns, "");
	    	}
	    	
	    }
		
		return res;
	}

	private String printGmlDriples() 
	{
		String res = "----- GML Triples -----\n";
		for(AR2DTriple dt : gmltriples)
		{
			res +=dt.toString();
		}
		
		
		return res + "----- End GML Triples -----\n";
	}

	public String generateGraphMLSource()
	{
		String res ="";
		
		//a list to avoid duplicated generation of nodes
		LinkedHashSet<String> generatedNodes = new LinkedHashSet<String>();
		
		
		int edgeCounter = 0;
		for(AR2DTriple gt : gmltriples)
		{
			String source = gt.getSource();
			String target = gt.getTarget();
			String edge = gt.getEdge();
			
			
			//generate source node if necessary
			if(!generatedNodes.contains(source))
			{
				log("Generating node for " + source);
				res += getNode(source,getNodeColor(source), getNodeShape(source));
				generatedNodes.add(source);
			}


			//generate source target if necessary
			if(!generatedNodes.contains(target))
			{
				log("Generating node for " + target);
				res += getNode(target,getNodeColor(source), getNodeShape(source));
				generatedNodes.add(target);
			}
			

			log("Generating edge " + edge + " from " + source + " to "  + target);
			res+=getEdge(edge, edgeCounter, source, target, getEdgeColor(source));
			edgeCounter++;
			
		}
		
		res=getGraphMLHeader(edgeCounter,generatedNodes.size()) + res;
		
		res+=getGraphMLTail();
		
		return res;
	}
	
	
	/*
	 * 
	 * 
		keys.put("classColor","#000000");
		keys.put("individualColor","#000000");
		keys.put("literalColor","#000000");
		keys.put("arrowColor","#000000");
		 classesSC, individualsSC, literalsSC, ontPropertiesSC, dtPropertiesSC;
		
		keys.put("classShape","rectangle");
		keys.put("individualShape","rectangle");
		keys.put("literalShape","rectangle");
	 */
	private String getNodeColor(String source) 
	{
		if(classesSC.contains(source))
		{
			return conf.getKeys().get("classColor");
		}
		if(individualsSC.contains(source))
		{
			return conf.getKeys().get("individualColor");
		}
		if(literalsSC.contains(source))
		{
			return conf.getKeys().get("literalColor");
		}
		
		
		return DEFAULT_NODE_COLOR;
	}

	
	private String getNodeShape(String source) 
	{
		if(classesSC.contains(source))
		{
			return conf.getKeys().get("classShape");
		}
		if(individualsSC.contains(source))
		{
			return conf.getKeys().get("individualShape");
		}
		if(literalsSC.contains(source))
		{
			return conf.getKeys().get("literalShape");
		}
		
		return DEFAULT_NODE_SHAPE;
	}
	
	private String getEdgeColor(String source)
	{
		if(ontPropertiesSC.contains(source))
		{
			return conf.getKeys().get("arrowColor");
		}
		if(dtPropertiesSC.contains(source))
		{
			return conf.getKeys().get("arrowColor");
		}
		//TODO ontPropertiesSC, dtPropertiesSC
		return DEFAULT_EDGE_COLOR;
	}

	public void saveSourceToFile(String path) 
	{
		try {
			PrintWriter out = new PrintWriter(path);
			String src = this.generateGraphMLSource();
			out.println(src);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	
	private void generateSyntObjPropertiesTriples() throws NullTripleMember 
	{
		if(!conf.synthesizeObjectProperties())
			return;
		
		Iterator it = objPropsMap.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry kv = (Map.Entry)it.next();
	        String propUri = (String) kv.getKey();
	        
	        ObjPropPair<String,String> mp = (ObjPropPair<String, String>) kv.getValue();
	        String rangeUri = mp.getRight();
	        String domainUri = mp.getLeft();

			String domainName = getNodeName(domainUri);
			String rangeName = getNodeName(rangeUri);
			String propName = getNodeName(propUri);
			
			gmltriples.add(new AR2DTriple(domainName,rangeName,propName));
			
	    }
	}

	private boolean checkObjPropoerties(Resource s,Property p,RDFNode o) 
	{
		if(p.getURI().equals(RDFS_DOMAIN))
		{
			ObjPropPair<String, String> dr = new ObjPropPair<String,String>();
			dr.setLeft(o.asResource().getURI());
			objPropsMap.put(s.getURI(), dr);
			return true;
		}
		

		if(p.getURI().equals(RDFS_RANGE))
		{
			ObjPropPair<String, String> dr = new ObjPropPair<String,String>();
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
				log(n + " looking for namespace " + ns + " found " + prefix);
				log("pfxmap" + prefixMap.keySet());
				if(prefix==null)
				{
					//if the prefix does not exit we use the full URI
					return res.getURI();
				}
				else
				{
					//replace the ns with the prefix		
					log("ReturnPrefix" + res.getURI().replace(ns, prefix+":"));
					return res.getURI().replace(ns, prefix+":");
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
		log.log(log.getLevel(), msg);
	}
	
	private String getGraphMLHeader(int edges, int nodes)
	{
		String head = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns/graphml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:y=\"http://www.yworks.com/xml/graphml\" xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns/graphml http://www.yworks.com/xml/schema/graphml/1.0/ygraphml.xsd\">\n" +
				"   <key for=\"node\" id=\"d0\" yfiles.type=\"nodegraphics\" />\n" +
				"   <key attr.name=\"description\" attr.type=\"string\" for=\"node\" id=\"d1\" />\n" +
				"   <key for=\"edge\" id=\"d2\" yfiles.type=\"edgegraphics\" />\n" +
				"   <key attr.name=\"description\" attr.type=\"string\" for=\"edge\" id=\"d3\" />\n" +
				"   <key for=\"graphml\" id=\"d4\" yfiles.type=\"resources\" />\n" +
				"   <graph edgedefault=\"directed\" id=\"G\" parse.edges=\""+edges+"\" parse.nodes=\""+nodes+"\" parse.order=\"free\">\n";
		return head;
	}
	
	private String getGraphMLTail()
	{
		String tail = "   </graph>\n" +
					"</graphml>";
		return tail;
	}
	
	private String getNode(String nodeLabel, String nodeColor, String nodeShape)
	{
		getHexColorCode(nodeColor);
		
		if(nodeLabel==null)
			nodeLabel="null";
		
		double widthScaleFactor = 10;
		
		int l = nodeLabel.length();
		
		if(l==0)
		{
			l=1;
		}
		double width = l * widthScaleFactor;
		
		String node = "      <node id=\""+NODE_ID_PREFIX+nodeLabel+"\">\n" +
				"         <data key=\"d0\">\n" +
				"            <y:ShapeNode>\n" +
				"               <y:Geometry height=\"30.0\" width=\"" + width + "\" x=\"0.0\" y=\"0.0\" />\n" +
				"               <y:Fill color=\"" + nodeColor + "\" transparent=\"false\" />\n" +
				"               <y:BorderStyle color=\"#000000\" type=\"line\" width=\"1.0\" />\n" +
				"               <y:NodeLabel alignment=\"center\" autoSizePolicy=\"content\" fontFamily=\"Dialog\" " +
				"fontSize=\"12\" fontStyle=\"plain\" hasBackgroundColor=\"false\" hasLineColor=\"false\" modelName=\"internal\" modelPosition=\"c\" " +
				"textColor=\"#000000\" visible=\"true\">" + nodeLabel + "</y:NodeLabel>\n" +
				"               <y:Shape type=\""+nodeShape+"\" />\n" +
				"            </y:ShapeNode>\n" +
				"         </data>\n" +
				"         <data key=\"d1\" />\n" +
				"      </node>\n";
		
		return node;
	}

	//black (default), red, blue, green, orange, yellow
	private String getHexColorCode(String nodeColor) 
	{
		if(nodeColor.equals("black"))
		{
			return "#00000000";
		}
		if(nodeColor.equals("red"))
		{
			return "#00FF0000";
		}
		if(nodeColor.equals("blue"))
		{
			return "#000000FF";
		}
		if(nodeColor.equals("green"))
		{
			return "#0000FF00";
		}
		if(nodeColor.equals("orange"))
		{
			return "#00FFA500";
		}
		if(nodeColor.equals("yellow"))
		{
			return "#00FFFF00";
		}
		
		return "#00000000";
	}

	private String getEdge(String edgeLabel, int edgeCounter, String source, String target, String edgeColor)
	{
		String edge = "    <edge id=\""+EDGE_ID_PREFIX+edgeLabel+edgeCounter+"\" source=\""+NODE_ID_PREFIX+source+"\" target=\""+NODE_ID_PREFIX+target+"\">\n" +
				"         <data key=\"d2\">\n" +
				"            <y:PolyLineEdge>\n" +
				"               <y:LineStyle color=\""+edgeColor+"\" type=\"line\" width=\"1.0\" />\n" +
				"               <y:Arrows source=\"none\" target=\"normal\" />\n" +
				"               <y:EdgeLabel alignment=\"center\" distance=\"2.0\" fontFamily=\"Dialog\" fontSize=\"12\" fontStyle=\"plain\" " +
				"hasBackgroundColor=\"false\" hasLineColor=\"false\" modelName=\"six_pos\" modelPosition=\"tail\" preferredPlacement=\"anywhere\" " +
				"ratio=\"0.5\" textColor=\"#000000\" visible=\"true\">" + edgeLabel + "</y:EdgeLabel>\n" +
				"               <y:BendStyle smoothed=\"false\" />\n" +
				"            </y:PolyLineEdge>\n" +
				"         </data>\n" +
				"         <data key=\"d3\" />\n" +
				"      </edge>\n";
		
		return edge;
	}
	
}
