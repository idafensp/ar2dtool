package es.upm.oeg.ar2dtool.utils.gv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import es.upm.oeg.ar2dtool.ConfigValues;
import es.upm.oeg.ar2dtool.Main;

public class Rdf2Gv {
	
	private static final String rdfTypeUri = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	private static final String rdfsRange = "http://www.w3.org/2000/01/rdf-schema#range";
	private static final String rdfsDomain = "http://www.w3.org/2000/01/rdf-schema#domain";
	
	
	private static String pathToRdf;
	private static ConfigValues cv;
	
	private String gvContent;
	
	private HashSet<String> classList;
	private HashSet<String> individualList;
	private HashSet<String> literalList;
	
	private Map<String,MutablePair<String,String>> objProps;

	private ArrayList<DOTTriple> dtLines;
	
	public ArrayList<DOTTriple> getDtLines() {
		return dtLines;
	}

	private HashSet<String> gvLines;
	
	public Rdf2Gv(String path, ConfigValues c)
	{
		pathToRdf = path;
		gvContent = "";
		cv = c;
		classList = new HashSet<String>();
		individualList = new HashSet<String>();
		literalList = new HashSet<String>();
		gvLines = new HashSet<String>();

		objProps = new HashMap<String, MutablePair<String,String>>();
		
		dtLines = new ArrayList<DOTTriple>();
	}

	public void parseRdf()
	{
		OntModel cMod = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
		cMod.read(pathToRdf, null);
		
		StmtIterator it = cMod.listStatements();
		
		while(it.hasNext())
		{
			Statement st = it.next();
			String sUri = st.getSubject().toString();
			String pUri = st.getPredicate().toString();
			String oUri ="";
			

			
			RDFNode obj = st.getObject();
			if(obj.isLiteral())
			{
				if(cv.keys.get("ignoreLiterals").equals("true")) //user wants to skip literals
					continue;
					
				oUri = "\"" + st.getString() + "\"";
			}
			else
			{
				oUri = st.getObject().asResource().toString();
			}
			
			
			//includeOnlyElementList Check
			if((cv.includeOnlyElementList.size()>0)&&((!cv.includeOnlyElementList.contains(sUri))&&(!cv.includeOnlyElementList.contains(pUri))&&(!cv.includeOnlyElementList.contains(oUri))))
			{
				//one of the elements is included in the ignore list
				dbg("includeOnlyElementList->Skipping triple:" + st);
				continue;
			}
			

			//ignoreElementList Check
			if((cv.ignoreElementList.contains(sUri))||(cv.ignoreElementList.contains(pUri))||(cv.ignoreElementList.contains(oUri)))
			{
				//one of the elements is included in the ignore list
				dbg("ignoreElementList->Skipping triple:" + st);
				continue;
			}
			
			//looking for equivalent elements
			sUri = checkEquivalentElementList(sUri);
			pUri = checkEquivalentElementList(pUri);
			oUri = checkEquivalentElementList(oUri);
			

			//add quotes for formatting reasons
//			String s = "\"" + sUri + "\"";
//			String p = "\"" + pUri + "\"";
//			String o = oUri; //literals already include quotes
//			if(!obj.isLiteral())
//			{
//				o = "\"" + oUri + "\"";
//			}
			
			
			
// REMOVE			
//			if(cv.keys.get("useFullUri").equals("false"))
//			{
//				//user wants to use local names instead of full URIs
//				s="\"" + cMod.getResource(sUri).getLocalName() + "\"";
//				p="\"" + cMod.getResource(pUri).getLocalName() + "\"";
//				if(!obj.isLiteral())
//				{
//					o="\"" + cMod.getResource(oUri).getLocalName() + "\"";
//				}
//			}
			
			if(cv.keys.get("synthesizeObjectProperties").equals("true")) //user wants to synthesize obj props
			{
				if(pUri.equals(rdfsRange)) //the triple is defining the range of an obj. prop.
				{
					if(objProps.containsKey(sUri))
					{
						objProps.get(sUri).setRight(oUri);
					}
					else
					{
						MutablePair<String, String> pair = new MutablePair<String, String>();
						pair.setRight(oUri);
						objProps.put(sUri, pair);
					}
					continue; //we are done with this triple
				}
				if(pUri.equals(rdfsDomain)) //the triple is defining the domain of an obj. prop.
				{
					if(objProps.containsKey(sUri))
					{
						objProps.get(sUri).setLeft(oUri);
					}
					else
					{
						MutablePair<String, String> pair = new MutablePair<String, String>();
						pair.setLeft(oUri);
						objProps.put(sUri, pair);
					}
					continue; //we are done with this triple
				}
			}
			
			if(obj.isLiteral())
			{
				literalList.add(oUri);
			}
			

			
			if(pUri.equals(rdfTypeUri))
			{	
				individualList.add(sUri); //we have found a new individual
				classList.add(oUri); //we have found a new class

				//TODO we should ckeck here if 'o' is a Class, ObjProp or DataTypeProp
				//in those cases we are not dealing with an individual
				//this happens when representing ontologies instead of individual datasets
				
				
				dbg("adding " + sUri + "to individuals due to triple: [" + st +"]");
								
				if(cv.keys.get("ignoreRdfType").equals("true"))
				{
					//is RdfType prop and user wants to ignore it
					dbg("ignoreRdfType->Skipping triple:" + st);
					continue;
				}
			}
			
			
			//store the new DOT line
			DOTTriple dt = new DOTTriple(sUri,oUri,pUri);
			dtLines.add(dt);
		}
		
		
		
		//defining the styles
		String classStyle = "node [shape = "+ cv.keys.get("classShape") +", color="+ cv.keys.get("classColor") +"]; ";
		String individualStyle = "node [shape = "+ cv.keys.get("individualShape") +", color="+ cv.keys.get("individualColor") +"]; ";
		String literalStyle = "node [shape = "+ cv.keys.get("literalShape") +", color="+ cv.keys.get("literalColor") +"]; ";
		String syntObjProp = getSynthesizeObjectPropertiesString(cMod);

		
		if(cv.keys.get("useFullUri").equals("false"))
		{
			classList = getLocalNames(cMod,classList);
			literalList = getLocalNames(cMod,literalList);
			individualList = getLocalNames(cMod,individualList);
		}
		
		//if we are ignoring the Rdf type property we don't include classes style 
		//to avoid them to appear alone without any link pointing to them
		if(cv.keys.get("ignoreRdfType").equals("true")) 
		{
			classStyle="/*classes style ignored due to ignoreRdfType*/\n";
		}
		else
		{
			for(String c : classList)
			{
				classStyle += "\"" + c + "\" ";
			}
			classStyle += ";/*classes*/\n";
		}
		
		//if we are ignoring literals we don't include their style 
		//to avoid them to appear alone without any link pointing to them
		if(cv.keys.get("ignoreLiterals").equals("true"))
		{
			literalStyle = "/*literal elements ignored*/\n";
		}
		else
		{
			if(literalList.size()>0)
			{
				for(String l : literalList)
				{
					literalStyle += "\"" + l + "\" ";
				}
				literalStyle += ";/*literals*/\n";
			}
		}

		String specialElementsStyle ="";
		for(ArrayList<String> se : cv.specialElementsList)
		{
			if(cv.keys.get("useFullUri").equals("true"))
			{
				specialElementsStyle +="node [shape = "+ se.get(1) +", color="+ se.get(2) +"]; \"" + se.get(0) + "\";\n";
			}
			else
			{
				Resource r = cMod.getResource(se.get(0));
				if(!r.isLiteral())
				{
					specialElementsStyle +="node [shape = "+ se.get(1) +", color="+ se.get(2) +"]; \"" + r.getLocalName() + "\";\n";
				}
				else
				{
					specialElementsStyle +="node [shape = "+ se.get(1) +", color="+ se.get(2) +"]; \"" + r.asLiteral().getString() + "\";\n";
				}
			}
		}
		
		
		for(String i : individualList)
		{
			dbg("checking " + i + " that has " + objProps.get(i));
			if(objProps.containsKey(i))//if it is on the synt obj. prop list it doesn't make any sense to define its style
			{
				dbg("skippingindividualSytle synt.:" + i);
				continue;
			}
			else
			{
				individualStyle += "\"" + i + "\" ";
			}
			
		}
		dbg("individualStyle:" + individualStyle + ":");
		individualStyle += "/*individuals*/\n";
		
		
		
		
		if(cv.keys.get("useFullUri").equals("false"))
		{
			//we load all the triple URIs and get their local names and add quotes
			ArrayList<DOTTriple> dtLinesLocal = new ArrayList<DOTTriple>();
			for(DOTTriple dt : dtLines)
			{
				String s="\"" + cMod.getResource(dt.getSource()).getLocalName() + "\"";
				String t="\"" + cMod.getResource(dt.getTarget()).getLocalName() + "\"";
				String e="\"" + cMod.getResource(dt.getEdge()).getLocalName() + "\"";
				
				DOTTriple dtl = new DOTTriple(s,t,e);
				dtLinesLocal.add(dtl);
			}
			dtLines = dtLinesLocal;
		}
		else
		{
			//we add  quotes to all URIs
			ArrayList<DOTTriple> dtLinesLocal = new ArrayList<DOTTriple>();
			for(DOTTriple dt : dtLines)
			{
				String s="\"" + dt.getSource() + "\"";
				String t="\"" + dt.getTarget() + "\"";
				String e="\"" + dt.getEdge() + "\"";
				
				DOTTriple dtl = new DOTTriple(s,t,e);
				dtLinesLocal.add(dtl);
			}
			dtLines = dtLinesLocal;
		}
		
		for(DOTTriple dt : dtLines)
		{
			String spoviz = "\t"+dt.getSource()+" -> " + dt.getTarget() + " [ label = "+ dt.getEdge() + " ];\n";
			gvContent += spoviz;
		}
		
		String conHead = "digraph ar2dtool_diagram { \n" +
				"rankdir=" + cv.keys.get("rankdir") + ";\n" +
				"size=\"" + cv.keys.get("imageSize") + "\"\n" +
				specialElementsStyle+
				classStyle+
				individualStyle+
				literalStyle+
				"node [shape = ellipse];\n" +
				"edge [color=\"" + cv.keys.get("arrowColor") + "\", dir=\"" +cv.keys.get("arrowdir") + "\", arrowhead=\"" +cv.keys.get("arrowhead") + "\",arrowtail=\"" +cv.keys.get("arrowtail") + "\"]\n";
		
		String conTail = "\n}";
		gvContent = conHead + gvContent + syntObjProp + conTail ;
		
		dbg("-------->"+objProps.toString());
		
		
	}
	

	private String getSynthesizeObjectPropertiesString(OntModel cMod) 
	{
		String res ="";
		Iterator it = objProps.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry kv = (Map.Entry)it.next();
	        String propUri = (String) kv.getKey();
	        MutablePair<String,String> mp = (MutablePair<String, String>) kv.getValue();
	        String rangeUri = mp.getRight();
	        String domainUri = mp.getLeft();
	        
			if(cv.keys.get("useFullUri").equals("false"))
			{
				//user wants to use local names instead of full URIs
				propUri="\"" + cMod.getResource(propUri).getLocalName() + "\"";
				rangeUri="\"" + cMod.getResource(rangeUri).getLocalName() + "\"";
				domainUri="\"" + cMod.getResource(domainUri).getLocalName() + "\"";
			}
			else
			{
				propUri="\"" + propUri + "\"";
				rangeUri="\"" + rangeUri + "\"";
				domainUri="\"" + domainUri + "\"";
			}
	        
			res += "\t"+domainUri+" -> " + rangeUri + " [ label = "+ propUri + " ];\n";
			
	    }
		return res;
	}

	private static String checkEquivalentElementList(String e)
	{
		for(ArrayList<String> list : cv.equivalentElementList)
		{
			if(list.contains(e))
			{
				//we have found the element on a list, so we replace it by the list's first element 
				dbg("equivalentElement->Replace element:" + e + " by " + list.get(0));
				return list.get(0);
			}
		}
		
		//we haven't found the element on any list, so we return the same element
		return e;
	}
	
	private HashSet<String> getLocalNames(OntModel cMod, HashSet<String> l)
	{
		HashSet<String> res = new HashSet<String>();
		
		for (String uri : l)
		{
			String ln = cMod.getResource(uri).getLocalName();
			res.add(ln);
		}
		
		return res;
	}
	
	public static void dbg(String msg)
	{
		Main.dbg(msg);
	}
	
	public String getGvContent()
	{
		return gvContent;
	}

	public String getClasses()
	{
		return classList.toString();
	}

	public String getLiterals()
	{
		return literalList.toString();
	}
	
	public String getIndividuals()
	{
		return individualList.toString();
	}
	
}
