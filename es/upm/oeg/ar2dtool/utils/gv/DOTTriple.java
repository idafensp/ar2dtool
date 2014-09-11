package es.upm.oeg.ar2dtool.utils.gv;


//class for storing each line of the DOT syntax, including the source node, target node and the edge.
public class DOTTriple 
{
	public String source;
	public String target;
	public String edge;
	
	public DOTTriple(String s, String t, String e)
	{
		source = s;
		target = t;
		edge = e;
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


}
