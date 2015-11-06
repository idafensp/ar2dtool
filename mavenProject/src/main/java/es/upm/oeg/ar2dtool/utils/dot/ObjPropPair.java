package es.upm.oeg.ar2dtool.utils.dot;

public class ObjPropPair<T1, T2> {
	
	private T1 left;
	private T2 right;
	
	public T1 getLeft() {
		return left;
	}
	public void setLeft(T1 left) {
		this.left = left;
	}
	public T2 getRight() {
		return right;
	}
	public void setRight(T2 right) {
		this.right = right;
	}
	
	public String toString()
	{
		return "<"+left+","+right+">";
	}
}
