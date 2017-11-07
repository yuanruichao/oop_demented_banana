package translator;

import xtc.tree.GNode;
import xtc.tree.Node;

public class ComparativeExpression extends JavaExpression{
	JavaExpression operand1;
	JavaExpression operand2;
	String operator;
	
	public ComparativeExpression(JavaScope scope, GNode n) {
		super(scope, n);
		operand1 = (JavaExpression) this.dispatch((Node) n.get(0));
		operand2 = (JavaExpression) this.dispatch((Node) n.get(2));
		operator = (String)n.get(1);
		// TODO Auto-generated constructor stub
	}

	public StringBuffer print() {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		sb.append(operand1.print()).append(" ").append(operator).append(operand2.print());
		return sb;
	}

	public String getTypeName() {
		return "boolean";
	}
}
