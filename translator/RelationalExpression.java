package translator;

import xtc.tree.GNode;

public class RelationalExpression extends  JavaExpression{
	JavaExpression operand1;
	String operator;
	JavaExpression operand2;
	
	public RelationalExpression(JavaScope scope, GNode n) {
		super(scope, n);
		operand1 = (JavaExpression) this.dispatch((GNode)n.get(0));
		operator = (String) n.get(1);
		operand2 = (JavaExpression) this.dispatch((GNode)n.get(2));
	}
	
	public StringBuffer print(){
		StringBuffer sb = new StringBuffer();
		if(operator.equals(null))
			sb.append(operand1.print());
		else{
			sb.append(operand1.print() + " ").append(operator).append(" "+ operand2.print());
		}
		return sb;
	}

	public String getTypeName() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
