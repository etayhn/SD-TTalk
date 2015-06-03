package il.ac.technion.cs.sd.msg.converting;

public class ComplexObject{
	
	private int i;
	private double d;
	private String s;
	
	public ComplexObject(int i, double d, String s) {
		this.i = i;
		this.d = d;
		this.s = s;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		ComplexObject other = (ComplexObject) obj;
		
		return other.i == i && other.d == d && other.s.equals(s);
	}
	
	@Override
	public String toString() {
		return "(" + i + "," + d + ",\"" + s + "\")";
	}
}