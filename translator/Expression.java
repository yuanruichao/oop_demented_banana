package translator;

import xtc.lang.JavaEntities;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.type.Type;
import xtc.util.SymbolTable;

public class Expression extends JavaExpression {
	JavaExpression operand1;
	JavaExpression operand2;
	String operator;

	public Expression(JavaScope scope, GNode n) {
		super(scope, n);
		operand1 = (JavaExpression) this.dispatch((Node) n.get(0));
		operand2 = (JavaExpression) this.dispatch((Node) n.get(2));
		operator = (String) n.get(1);
	}

	public StringBuffer print() {
		StringBuffer sb = new StringBuffer();
		
		sb.append(operand1.print()).append(" ").append(operator).append(" ");
		
		if(!operator.equals("=")) {
		sb.append(operand2.print());
		} else {
			if(operand2 instanceof PrimaryIdentifier) {
				sb.append(((PrimaryIdentifier) operand2).simplePrint());
			} else {
				sb.append(operand2.print());
			}
		}
		return sb;
	}

	public String getTypeName() {
		return null;
	}

}
