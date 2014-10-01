package es.upm.oeg.ar2dtool.utils.graphml;

import java.util.ArrayList;
import java.util.HashSet;

import es.upm.oeg.ar2dtool.utils.dot.DOTTriple;

public class GraphMLGenerator {

	private HashSet<String> nodeList;
	private HashSet<String> edgeList;
	private ArrayList<DOTTriple> dtLines;
	
	private String nodeHead1 ="      <node id=\"";
	
	private String nodeHead2 = "\">\n" +
			"         <data key=\"d0\">\n" +
			"            <y:ShapeNode>\n" +
			"               <y:Geometry height=\"30.0\" width=\"30.0\" x=\"0.0\" y=\"0.0\" />\n" +
			"               <y:Fill color=\"#FFFFFF\" transparent=\"false\" />\n" +
			"               <y:BorderStyle color=\"#000000\" type=\"line\" width=\"1.0\" />\n" +
			"               <y:NodeLabel alignment=\"center\" autoSizePolicy=\"content\" fontFamily=\"Dialog\" fontSize=\"12\" fontStyle=\"plain\" hasBackgroundColor=\"false\" hasLineColor=\"false\" modelName=\"internal\" modelPosition=\"c\" textColor=\"#000000\" visible=\"true\">";
	
	private String nodeTail = "</y:NodeLabel>\n" +
			"               <y:Shape type=\"rectangle\" />\n" +
			"            </y:ShapeNode>\n" +
			"         </data>\n" +
			"         <data key=\"d1\" />\n" +
			"      </node>\n";
	
	private String edgeHead = ">\n" +
			"         <data key=\"d2\">\n" +
			"            <y:PolyLineEdge>\n" +
			"               <y:LineStyle color=\"#000000\" type=\"line\" width=\"1.0\" />\n" +
			"               <y:Arrows source=\"normal\" target=\"normal\" />\n" +
			"               <y:EdgeLabel alignment=\"center\" distance=\"2.0\" fontFamily=\"Dialog\" fontSize=\"12\" fontStyle=\"plain\" hasBackgroundColor=\"false\" hasLineColor=\"false\" modelName=\"six_pos\" modelPosition=\"tail\" preferredPlacement=\"anywhere\" ratio=\"0.5\" textColor=\"#000000\" visible=\"true\">";
	
	private String edgeTail = "</y:EdgeLabel>\n" +
			"               <y:BendStyle smoothed=\"false\" />\n" +
			"            </y:PolyLineEdge>\n" +
			"         </data>\n" +
			"         <data key=\"d3\" />\n" +
			"      </edge>\n";
	
	
	
	public GraphMLGenerator(ArrayList<DOTTriple> dtl)
	{

		nodeList = new HashSet<String>();
		edgeList = new HashSet<String>();
		dtLines = dtl;
		
//		//we add every source and target node to de node list (HashSet avoids duplicate entries)
//		//we also load every edge
//		for(DOTTriple dt : dtl)
//		{
//			nodeList.add(dt.source);
//			nodeList.add(dt.target);
//			
//			edgeList.add(dt.edge);
//		}
	}

	public String generateXML()
	{
		String nodeXML ="";
		String edgeXML ="";
		String res = "";
		
	    int edgeCount = 0;	
	    
	    //Rdf2Gv class generates the list including quotes for DOT formatting reasons
	    //we need to remove them here as they are not longer necessary
	    dtLines = clearQuotes(dtLines);
		 
		for(DOTTriple dt : dtLines)
		{
			//generate the source node info if necessary
			if(!nodeList.contains(dt.source))
			{
				nodeList.add(dt.source);
				nodeXML += generateNodeXML(dt.source);
			}
			
			//generate the target node info if necessary
			if(!nodeList.contains(dt.target))
			{
				nodeList.add(dt.target);
				nodeXML += generateNodeXML(dt.target);
			}
			
			edgeXML += generateEdgeXML(dt,edgeCount);
			
			
			
			edgeCount++;
			
		}
		
		res = nodeXML + "\n\n\n" + edgeXML;
		
		return res;
	}

	private ArrayList<DOTTriple> clearQuotes(ArrayList<DOTTriple> dtQuotes) {
		
		ArrayList<DOTTriple> dtClear = new ArrayList<DOTTriple>();
		
		for(DOTTriple dt : dtQuotes)
		{
			String s = dt.source.replaceAll("\"", "");
			String t = dt.target.replaceAll("\"", "");
			String e = dt.edge.replaceAll("\"", "");
			
			DOTTriple dtTemp = new DOTTriple(s,t,e);
			
			dtClear.add(dtTemp);
		}
		
		return dtClear;
	}

	private String generateEdgeXML(DOTTriple dt, int edgeCount) {
		String edgePreHead = "<edge id=\""+dt.edge+edgeCount+"\" source=\""+dt.source+"\" target=\""+dt.target+"\"";
		String res = edgePreHead+edgeHead+dt.edge+edgeTail;
		return res;
	}

	private String generateNodeXML(String n) {
		String res = nodeHead1+n+nodeHead2+n+nodeTail;
		
		return res;
	}
	
	
	
	
	
}
