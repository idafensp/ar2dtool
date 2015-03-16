package es.upm.oeg.ar2dtool.utils;

import com.hp.hpl.jena.rdf.model.Statement;

import es.upm.oeg.ar2dtool.exceptions.NullTripleMember;


//class for storing each line of the DOT syntax, including the source node, target node and the edge.
public class AR2DTriple 
{
	private String source;
	private String target;
	private String edge;
	
	public AR2DTriple(String s, String t, String e) throws NullTripleMember
	{
		source = s;
		target = t;
		edge = e;

		if((source==null)||(edge==null)||(target==null)||(source.equals("null"))||(edge.equals("null"))||(target.equals("null")))
		{
			throw new NullTripleMember("Triple with null member: <s="+source+",e="+edge+",t="+target+">");
		}
	}
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getEdge() {
		return edge;
	}

	public void setEdge(String edge) {
		this.edge = edge;
	}
	
	public String toString()
	{
		return "[" + source + "-" + edge + "->" + target + "]\n";
	}


}
