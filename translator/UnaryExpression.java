package translator;

import xtc.tree.GNode;

public class UnaryExpression extends JavaExpression{
	JavaExpression je;
	String pp;
	public UnaryExpression(JavaScope scope, GNode n) {
		super(scope, n);
		pp = n.get(0).toString();
		je = (JavaExpression)this.dispatch((GNode)n.get(1));
	}

	public StringBuffer print() {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		sb.append(pp).append(je.print());//.append(";");//.append("\n");
		return sb;
	}

	public String getTypeName() {
		if(je != null) return je.getTypeName();
		return null;
	}
	
}
