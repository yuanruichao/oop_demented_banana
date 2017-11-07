package translator;

import xtc.tree.GNode;

import java.util.ArrayList;

public class LogicExpression extends JavaExpression {
	JavaExpression operand1;
	String operator;
	JavaExpression operand2;
	
	public LogicExpression(JavaScope scope, GNode n, String exp) {
		super(scope, n);
		operand1 = (JavaExpression) this.dispatch((GNode)n.get(0));
		operand2 = (JavaExpression) this.dispatch((GNode)n.get(0));
		operator= exp;
	}
	
	public StringBuffer print() {
		StringBuffer sb = new StringBuffer();
		if(operator.equals(null))
			sb.append(operand1.print());
		else{
			sb.append(operand1.print()).append(operator).append(operand2.print());
		}
		return sb;
	}

	public String getTypeName() {
		return "boolean";
	}

}
