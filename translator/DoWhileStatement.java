package translator;

import xtc.tree.GNode;

public class DoWhileStatement extends JavaStatement {
	//JavaBlock 
	JavaExpression je;
	public DoWhileStatement(JavaScope scope, GNode n) {
		super(scope, n);
		je = (JavaExpression) this.dispatch((GNode)n.get(1));
	}

	public StringBuffer print() {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		sb.append("do ").append("{").append("\n this is block \n}");//jb.print());
		sb.append("while(").append(je.print() + ")");
		return sb;
	}
	
}
