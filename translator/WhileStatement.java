package translator;

import xtc.tree.GNode;

public class WhileStatement extends JavaStatement {
	JavaExpression conditional;
	JavaBlock body;
	public WhileStatement(JavaScope scope, GNode n){
		super(scope,n);
		conditional = (JavaExpression) this.dispatch((GNode)n.get(0));
		body = new JavaBlock(this,(GNode)n.get(1));
	}
	
	public StringBuffer print() {
		StringBuffer sb = new StringBuffer();
		//System.out.println("!!!!!!!!!!!!!!");
		sb.append("while(").append(conditional.print()).append(")").append("\n");
		sb.append(body.print()).append("\n");
		return sb;
	}
	
}
