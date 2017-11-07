package translator;

import xtc.tree.GNode;

public class PostfixExpression extends JavaExpression {
	JavaExpression je;
	String pp;
	public PostfixExpression(JavaScope scope, GNode n) {
		super(scope, n);
		pp = n.get(1).toString();
		je = (JavaExpression)this.dispatch((GNode)n.get(0));
		// TODO Auto-generated constructor stub
	}

	public StringBuffer print() {
		StringBuffer sb = new StringBuffer();
		sb.append(je.print()).append(pp);//.append(";");//.append("\n");
		return sb;
	}

	public String getTypeName() {
		return null;
	}
	
}
