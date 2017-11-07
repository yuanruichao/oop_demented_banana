package translator;
import java.util.ArrayList;

import xtc.tree.GNode;
import xtc.tree.Node;

public class ArithmeticExpression extends JavaExpression {
    String operator;
    JavaExpression operand1;
    JavaExpression operand2;
	
    public ArithmeticExpression(JavaScope scope, GNode n) {
		super(scope, n);
		operand1 = (JavaExpression) this.dispatch((GNode) n.get(0));
		operand2 = (JavaExpression) this.dispatch((GNode) n.get(2));
		operator = (String) n.get(1);
	}
    
	public StringBuffer print() {
		StringBuffer sb = new StringBuffer();
		sb.append(operand1.print());
		sb.append(operator);
		sb.append(operand2.print());
		return sb;
	}

	public String getTypeName() {
		String typeName1, typeName2;
		typeName1 = operand1.getTypeName();
		typeName2 = operand2.getTypeName();
		if(typeName1 != null || typeName2 != null) {
			if(typeName1 == null) return typeName2;
			if(typeName2 == null) return typeName1;
			
			JavaType type1 = JavaType.getType(typeName1);
			JavaType type2 = JavaType.getType(typeName2);
			JavaType it = JavaType.getType("int");
			
			if(type1 == it || it.hasChild(type1)) {
				if(type2 == it || it.hasChild(type2)) return it.getName();
			}
			
			if(type1.getLevel() < type2.getLevel()) {
				return type1.getName();
			} else {
				return type2.getName();
			}
		}
		return null;
	}
}
