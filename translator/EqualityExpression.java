package translator;

import xtc.tree.GNode;

public class EqualityExpression extends JavaExpression {
	JavaExpression operand1;
	String operator;
	JavaExpression operand2;
	StringBuffer sb = new StringBuffer();
	
	public EqualityExpression(JavaScope scope, GNode n) {
		super(scope, n);
		operand1 = (JavaExpression) this.dispatch((GNode)n.get(0));
		operator = (String) n.get(1);
		operand2 = (JavaExpression) this.dispatch((GNode)n.get(2));
		// TODO Auto-generated constructor stub
	}
	public StringBuffer print() {
		if(operator.equals(null))
			sb.append(operand1.print());
		else{
			sb.append(operand1.print()).append(operator).append(operand2.print());
		}
		return sb;
	}
	public String getTypeName() {
		return null;
	}
}