package translator;

import xtc.tree.GNode;

public class Modifier extends JavaScope{
	private String modify;
	private StringBuffer sb = new StringBuffer();
	public Modifier(JavaScope scope, GNode n) {
		super(scope, n);
		modify = (String) n.get(0);
		// TODO Auto-generated constructor stub
	}
	public StringBuffer print(){
		sb.append(modify);
		return sb;
	}
}