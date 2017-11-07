package translator;

import xtc.tree.GNode;

public class ExpressionStatement extends JavaStatement {
	JavaExpression je;
	public ExpressionStatement(JavaScope scope, GNode n) {
		super(scope, n);
		je = (JavaExpression) this.dispatch((GNode)n.get(0));
	}

	public StringBuffer print() {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		
		
		sb.append(je.print());
		return sb;
	}
	
}
